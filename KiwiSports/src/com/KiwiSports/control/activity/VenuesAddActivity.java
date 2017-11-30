package com.KiwiSports.control.activity;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.KiwiSports.R;
import com.KiwiSports.utils.CommonUtils;
import com.KiwiSports.utils.ScreenShareUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;
import android.widget.AdapterView.OnItemClickListener;

public class VenuesAddActivity extends BaseActivity
		implements BDLocationListener, BaiduMap.OnMapStatusChangeListener, OnGetGeoCoderResultListener {

	private LinearLayout pagetop_layout_back;
	private TextView pagetop_tv_name;
	private TextView tv_left;
	private TextView tv_right;
	private RelativeLayout relay_sporttype;
	private TextView tv_sporttype;
	private TextView tv_next;
	private ImageView map_iv_covering;
	private ImageView map_iv_cutreslut;
	private FrameLayout mFrameLayout;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pagetop_layout_back:
			doBack();
			break;
		case R.id.relay_sporttype:
			Intent intent = new Intent(this, VenuesTypeActivity.class);
			intent.putExtra("sportsType", sportsType);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intent, 1);
			CommonUtils.getInstance().setPageIntentAnim(intent, this);
			break;
		case R.id.tv_next:
			if (!TextUtils.isEmpty(sportsType)) {
				mBaiduMap.snapshot(callback);
			} else {
				CommonUtils.getInstance().initToast(context, "请选择场地类型");
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.venues_add);
		CommonUtils.getInstance().addActivity(this);
	}

	@Override
	protected void findViewById() {
		pagetop_layout_back = (LinearLayout) findViewById(R.id.pagetop_layout_back);
		pagetop_tv_name = (TextView) findViewById(R.id.pagetop_tv_name);
		map_iv_covering = (ImageView) findViewById(R.id.map_iv_covering);
		map_iv_cutreslut = (ImageView) findViewById(R.id.map_iv_cutreslut);
		mFrameLayout = (FrameLayout) findViewById(R.id.mFrameLayout);
		tv_left = (TextView) findViewById(R.id.tv_left);
		tv_right = (TextView) findViewById(R.id.tv_right);
		relay_sporttype = (RelativeLayout) findViewById(R.id.relay_sporttype);
		tv_sporttype = (TextView) findViewById(R.id.tv_sporttype);
		tv_next = (TextView) findViewById(R.id.tv_next);
	}

	@Override
	protected void setListener() {
		pagetop_layout_back.setOnClickListener(this);
		relay_sporttype.setOnClickListener(this);
		tv_next.setOnClickListener(this);
		pagetop_tv_name.setText("场地");
	}

	@Override
	protected void processLogic() {
		initMapView();
	}

	/**
	 * =================定位======================
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private LocationClientOption option;
	private float STARTZOOM = 15.0f;
	private boolean isFirstLoc = true;

	private double longitude_me;
	private double latitude_me;
	private LatLng lefttopLatLng;
	private LatLng rightbottomLatLng;
	private String sportsType = "";
	private String sportsTypeName = "";

	/**
	 * 初始化定位的SDK
	 */
	private void initMapView() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.mMapView);
		mBaiduMap = mMapView.getMap();
		/*
		 * 百度地图 UI 控制器
		 */
		UiSettings mUiSettings = mBaiduMap.getUiSettings();
		mUiSettings.setCompassEnabled(false);// 隐藏指南针
		mUiSettings.setRotateGesturesEnabled(false);// 阻止旋转
		// 设置显示缩放比例
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(STARTZOOM);
		mBaiduMap.setMapStatus(msu);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 隐藏缩放控件
		int childCount = mMapView.getChildCount();
		if (childCount >= 3) {
			// 删除比例尺控件
			mMapView.removeViewAt(3);
			mMapView.removeViewAt(2);
			View child = mMapView.getChildAt(1);
			if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
				child.setVisibility(View.INVISIBLE);
			}
			mMapView.showZoomControls(false);
			mMapView.showScaleControl(false);
		}

		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(this);
		// 创建一个定位客户端的参数
		option = new LocationClientOption();
		option.setOpenGps(true);// 是否打开GPS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
		mLocClient.setLocOption(option);// 设置给定位客户端
		mLocClient.start();// 启动定位客户端
		// 修改为自定义marker
		// BitmapDescriptor mCurrentMarker =
		// BitmapDescriptorFactory.fromAssetWithDpi("icon_myposition_map.png");
		// mBaiduMap.setMyLocationConfigeration(new
		// MyLocationConfiguration(LocationMode.NORMAL, false, mCurrentMarker));
		// 地图状态改变相关监听
		mBaiduMap.setOnMapStatusChangeListener(this);
	}

	Bitmap temBitmap_BG;
	SnapshotReadyCallback callback = new SnapshotReadyCallback() {
		/**
		 * 地图截屏回调接口
		 * 
		 * @param snapshot
		 *            截屏返回的 bitmap 数据
		 */
		public void onSnapshotReady(Bitmap snapshot) {
			temBitmap_BG = snapshot;
			minitHandler.sendEmptyMessage(SET);
		}
	};
	private final int SET = 2;
	Handler minitHandler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET:
				BitmapDrawable bd = new BitmapDrawable(temBitmap_BG);
				map_iv_cutreslut.setBackgroundDrawable(bd);
				Bitmap bmp = ScreenShareUtil.takeScreenShot(mFrameLayout, false);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] bitmapByte = baos.toByteArray();
				Intent intent = new Intent(context, VenuesAddNextActivity.class);
				intent.putExtra("sportsType", sportsType);
				intent.putExtra("bitmap", bitmapByte);
				intent.putExtra("address", address);
				intent.putExtra("top_left_x", lefttopLatLng.latitude + "");
				intent.putExtra("top_left_y", lefttopLatLng.longitude + "");
				intent.putExtra("bottom_right_x", rightbottomLatLng.latitude + "");
				intent.putExtra("bottom_right_y", rightbottomLatLng.longitude + "");
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				CommonUtils.getInstance().setPageIntentAnim(intent, context);
				map_iv_cutreslut.setBackgroundDrawable(null);
				break;
			}
		}
	};
	public GeoCoder geoCoder;
	private String address;

	@Override
	public void onReceiveLocation(BDLocation location) {

		try {
			System.err.println("*********************************");
			if (isFirstLoc) {
				// --------------------*定自己的位置信息-----------------------------------------------
				try {
					if (location.getLocType() == 62) {
						longitude_me = 116.404269;
						latitude_me = 39.91405;
					} else {
						longitude_me = location.getLongitude();
						latitude_me = location.getLatitude();
					}
					MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
							// 此处设置开发者获取到的方向信息，顺时针0-360
							.direction(0).latitude(latitude_me).longitude(longitude_me).build();
					mBaiduMap.setMyLocationData(locData);
					locData = null;
					// ---------1.首先判断当前城市是否有，没有则默认距离北京坐标，我的位置为当前定位-----------------------------
				} catch (Exception e) {
				}
				// 创建GeoCoder实例对象
				geoCoder = GeoCoder.newInstance();
				// 发起反地理编码请求(经纬度->地址信息)
				ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
				// 设置反地理编码位置坐标
				reverseGeoCodeOption.location(new LatLng(latitude_me, longitude_me));
				geoCoder.reverseGeoCode(reverseGeoCodeOption);

				// 设置查询结果监听者
				geoCoder.setOnGetGeoCodeResultListener(this);
				option.setOpenGps(false);
				isFirstLoc = false;
			}
		} catch (Exception e) {
		} finally {
			moveToCenter();
			getSiteLocation();
		}

	}

	// 地理编码查询结果回调函数
	@Override
	public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
	}

	// 反地理编码查询结果回调函数
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
		final List<PoiInfo> poiInfos = reverseGeoCodeResult.getPoiList();
		if (poiInfos != null && poiInfos.size() > 0) {
			address = poiInfos.get(0).address;
		}
	}

	/**
	 * 手势操作地图，设置地图状态等操作导致地图状态开始改变
	 * 
	 * @param mapStatus
	 *            地图状态改变开始时的地图状态
	 */
	@Override
	public void onMapStatusChangeStart(MapStatus mapStatus) {
	}

	/**
	 * 地图状态变化中
	 * 
	 * @param mapStatus
	 *            当前地图状态
	 */
	@Override
	public void onMapStatusChange(MapStatus mapStatus) {
	}

	/**
	 * 地图状态改变结束
	 * 
	 * @param mapStatus
	 *            地图状态改变结束后的地图状态
	 */
	@Override
	public void onMapStatusChangeFinish(MapStatus mapStatus) {
		// 地图操作的中心点
		getSiteLocation();
		LatLng cenpt = mapStatus.target;
		if (geoCoder != null && cenpt != null)
			geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(cenpt));
	}

	private void getSiteLocation() {
		lefttopLatLng = getSite(1);
		Log.e("TESTLOG", "左上角经度 x:" + lefttopLatLng.latitude + " 左上角纬度 y:" + lefttopLatLng.longitude);
		tv_left.setText(lefttopLatLng.longitude + "," + lefttopLatLng.latitude);
		rightbottomLatLng = getSite(4);
		Log.e("TESTLOG", "右下角经度 x:" + rightbottomLatLng.latitude + "右下角纬度 y:" + rightbottomLatLng.longitude);
		tv_right.setText(rightbottomLatLng.longitude + "," + rightbottomLatLng.latitude);
	}

	/**
	 * 获取坐标点
	 * 
	 * @param type
	 * @return
	 */
	private LatLng getSite(int type) {
		Point pt = new Point();
		if (type == 1) {
			// 左上角
			pt.x = mMapView.getMeasuredWidth() / 2 - map_iv_covering.getMeasuredWidth() / 2;
			pt.y = mMapView.getMeasuredHeight() / 2 - map_iv_covering.getMeasuredHeight() / 2;
		}
		if (type == 2) {
			// 左下角
			pt.x = mMapView.getMeasuredWidth() / 2 - map_iv_covering.getMeasuredWidth() / 2;
			pt.y = mMapView.getMeasuredHeight() / 2 + map_iv_covering.getMeasuredHeight() / 2;
		}
		if (type == 3) {
			// 右上角
			pt.x = mMapView.getMeasuredWidth() / 2 + map_iv_covering.getMeasuredWidth() / 2;
			pt.y = mMapView.getMeasuredHeight() / 2 - map_iv_covering.getMeasuredHeight() / 2;
		}
		if (type == 4) {
			// 右下角
			pt.x = mMapView.getMeasuredWidth() / 2 + map_iv_covering.getMeasuredWidth() / 2;
			pt.y = mMapView.getMeasuredHeight() / 2 + map_iv_covering.getMeasuredHeight() / 2;
		}

		LatLng ll = mBaiduMap.getProjection().fromScreenLocation(pt);

		return ll;

	}

	/**
	 * 设置中心点的焦点
	 */
	private void moveToCenter() {
		LatLng mypoint = new LatLng(latitude_me, longitude_me);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mypoint);
		if (u != null && mBaiduMap != null) {
			// mBaiduMap.animateMapStatus(u);//以动画方式更新地图状态，动画耗时 300 ms
			mBaiduMap.setMapStatus(u);// 改变地图状态
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPause() {
		try {
			if (mMapView != null) {
				mMapView.onPause();
			}
			super.onPause();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onResume() {
		try {
			if (mMapView != null) {
				mMapView.onResume();
			}
			super.onResume();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		try {
			// 退出时销毁定位
			if (mLocClient != null)
				mLocClient.stop();
			// 关闭定位图层
			mBaiduMap.setMyLocationEnabled(false);
			mBaiduMap.clear();
			mBaiduMap = null;
			mMapView.removeAllViews();
			mMapView.onDestroy();
			mMapView = null;
			option = null;
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode == 1) {
				sportsTypeName = data.getStringExtra("sportsTypeName");
				sportsType = data.getStringExtra("sportsType");
				tv_sporttype.setText(sportsTypeName);
			}
		} catch (Exception e) {
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void doBack() {
		finish();
		CommonUtils.getInstance().setPageBackAnim(this);
	}

	/**
	 * 重写onkeydown 用于监听返回键
	 */
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doBack();
		}
		return false;
	}

}
