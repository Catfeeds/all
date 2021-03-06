package com.zh.education.control.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.HashMap;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;

import com.zh.education.R;
import com.zh.education.business.UserLoginBusiness;
import com.zh.education.business.UserLoginBusiness.GetLoginCallback;
import com.zh.education.model.NoticesInfo;
import com.zh.education.model.UserLoginInfo;
import com.zh.education.utils.CommonUtils;
import com.zh.education.utils.DatesUtils;
import com.zh.education.utils.MyEditText;
import com.zh.education.utils.ProgressDialogUtils;
import com.zh.education.utils.WebServiceUtils;
import com.zh.education.utils.WebServiceUtils.WebServiceCallBack;

import Decoder.BASE64Encoder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author 作者：jun
 * @date 创建时间：2015-12-28 下午4:31:30
 * @Description 类描述：登錄
 */
public class UserLoginActivity extends BaseActivity {

	private Button userlogin_btn_login;
	private MyEditText userlogin_et_username, userlogin_et_password;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.userlogin_btn_login:
//			 String name = userlogin_et_username.getText().toString();
//			 String pw = userlogin_et_password.getText().toString();
			String name = "yinxin@tedaedu.cn";
			String pw = "Teda.cable";
//			String name ="mapp@tedaedu.cn";
//			String pw ="Pass@word@2015";
			
			if (checkStatus(name, pw)) {
				String account = getBase64(name, pw);
				login(account);
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.userlogin);
		CommonUtils.getInstance().addActivity(this);
	}

	@Override
	protected void findViewById() {
		userlogin_btn_login = (Button) findViewById(R.id.userlogin_btn_login);
		userlogin_et_username = (MyEditText) findViewById(R.id.userlogin_et_username);
		userlogin_et_password = (MyEditText) findViewById(R.id.userlogin_et_password);
	}

	@Override
	protected void setListener() {
		userlogin_btn_login.setOnClickListener(this);
	}

	/**
	 * 得到输入框的实际宽度，并设置，防止输入时宽度拉伸
	 */
	@Override
	protected void processLogic() {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		userlogin_btn_login.measure(w, h);
		userlogin_et_username
				.setWidth(userlogin_et_username.getMeasuredWidth());
		userlogin_et_password
				.setWidth(userlogin_et_username.getMeasuredWidth());
		Log.i("imageview2",
				"width: " + userlogin_et_username.getMeasuredWidth());
	}

	// administrator@contoso.com;P@ssw0rd
	// eWlueGluQHRqZWR1LmNuO1RlZGEuY2FibGU=
	public String getBase64(String name, String pw) {
		byte[] b = null;
		String s = null;
		try {
			b = (name + ";" + pw).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		Log.e("sss", s);
		return s;
	}

	private void login(final String account) {
		// 显示进度条
		ProgressDialogUtils.showProgressDialog(this, "数据加载中...","userLogin");
		// 通过工具类调用WebService接口
		HashMap<String, String> mhashmap = new HashMap<String, String>();
		mhashmap.put("loginStr", account);
		
		new UserLoginBusiness(this, mhashmap, new GetLoginCallback() {
			public void afterDataGet(HashMap<String, Object> dataMap) {
				// 关闭进度条
				ProgressDialogUtils.dismissProgressDialog("userLogin");
				if (dataMap != null) {
					String status = (String) dataMap.get("status");
					if (status.equals("200")) {
						UserLoginInfo loginInfo = (UserLoginInfo) dataMap
								.get("loginInfo");
						if(loginInfo!=null){
						String SchoolUrl = loginInfo.getSchoolUrl();
						String tokenStr = loginInfo.getTokenStr();
						String name = loginInfo.getName();
						String note = loginInfo.getNotes();
						String pictureUrl = loginInfo.getPictureUrl();
						String BlogUrl = loginInfo.getBlogUrl();
						SharedPreferences zhedu_spf = CommonUtils.getInstance()
								.getBestDoInfoSharedPrefs(
										UserLoginActivity.this);
						Editor editor = zhedu_spf.edit();
						editor.putString("schoolUrl", SchoolUrl);
						editor.putString("tokenStr", tokenStr);
						editor.putString("name", name);
						editor.putString("note", note);
						editor.putString("loginStr", account);
						editor.putString("pictureUrl", pictureUrl);
						editor.putString("BlogUrl", BlogUrl);
						String loginTime=DatesUtils.getInstance().getTimeStampToDate(DatesUtils.getInstance().syncCurrentTime(), "yyyy-MM-dd");
						editor.putString("loginTime", loginTime);
						editor.commit();
						}
					} else {
						Toast.makeText(context, (String) dataMap.get("msg"),
								Toast.LENGTH_SHORT).show();
					}
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				} else {
					Toast.makeText(context, "网络不稳，请稍后重试！", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

	}

	private boolean checkStatus(String name, String pw) {
		if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pw)) {
			return true;
		} else {
			Toast.makeText(context, "请输入账户或密码", 0).show();
		}
		return false;
	}

	/**
	 * 监听返回键
	 */
	public void onBackPressed() {
		CommonUtils.getInstance().defineBackPressed(this,
				CommonUtils.getInstance().exit);
	}
}
