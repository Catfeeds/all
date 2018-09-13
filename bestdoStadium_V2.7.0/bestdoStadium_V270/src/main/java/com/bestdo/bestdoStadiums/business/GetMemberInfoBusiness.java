package com.bestdo.bestdoStadiums.business;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.bestdo.bestdoStadiums.utils.Constans;
import com.bestdo.bestdoStadiums.utils.parser.AddWonderfulParser;
import com.bestdo.bestdoStadiums.utils.parser.CampaignFavoriteParser;
import com.bestdo.bestdoStadiums.utils.parser.GetMeMberInfoParser;
import com.bestdo.bestdoStadiums.utils.parser.GetMeMberListParser;
import com.bestdo.bestdoStadiums.utils.volley.RequestUtils;

/**
 * 获取当前用户身份信息
 * 
 * @author qyy
 * 
 */
public class GetMemberInfoBusiness {

	public interface GetMemberInfoCallback {
		public void afterDataGet(HashMap<String, Object> dataMap);
	}

	private GetMemberInfoCallback mGetDataCallback;
	HashMap<String, String> mhashmap;
	Context mContext;

	public GetMemberInfoBusiness(Context mContext, HashMap<String, String> mhashmap,
			GetMemberInfoCallback mGetDataCallback) {
		this.mGetDataCallback = mGetDataCallback;
		this.mhashmap = mhashmap;
		this.mContext = mContext;
		getDate();
	}

	private void getDate() {
		String path = Constans.GETMEMBERINFO;
		StringRequest mJsonObjectRequest = new StringRequest(Method.POST, path, new Listener<String>() {
			public void onResponse(String response) {
				Log.e("JsonObjectRequest----", response.toString());
				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				GetMeMberInfoParser mParser = new GetMeMberInfoParser();
				JSONObject jsonObject = RequestUtils.String2JSON(response);
				dataMap = mParser.parseJSON(jsonObject);
				mGetDataCallback.afterDataGet(dataMap);
				mParser = null;
				jsonObject = null;
			}
		}, new Response.ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				mGetDataCallback.afterDataGet(null);
				Log.e("error----", error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				return mhashmap;
			}
		};
		RequestUtils.addRequest(mJsonObjectRequest, mContext);

	}
}