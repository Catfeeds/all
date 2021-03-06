package com.bestdo.bestdoStadiums.utils.parser;

import java.util.HashMap;
import org.json.JSONObject;

/**
 * 
 * @author qyy
 * 
 */
public class GetVersonParser extends BaseParser<Object> {

	@Override
	public HashMap<String, Object> parseJSON(JSONObject jsonObject) {
		HashMap<String, Object> mHashMap = null;
		try {
			String status = jsonObject.getString("code");
			mHashMap = new HashMap<String, Object>();
			mHashMap.put("status", status);
			if (status.equals("400")) {
				String msg = jsonObject.getString("msg");
				mHashMap.put("msg", msg);
			} else if (status.equals("200")) {
				try {
					JSONObject oo = jsonObject.optJSONObject("data");
					String url = oo.optString("url", "");
					String description = oo.optString("description", "");
					String version = oo.optString("version", "");
					String level = oo.optString("level", "");
					mHashMap.put("url", url);
					mHashMap.put("description", description);
					mHashMap.put("version", version);
					mHashMap.put("level", level);
				} catch (Exception e) {
				}

			} else if (status.equals("201")) {
				String msg = jsonObject.getString("msg");
				mHashMap.put("msg", msg);
			}

		} catch (Exception e) {
		}
		return mHashMap;
	}

}
