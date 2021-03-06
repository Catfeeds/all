package com.bestdo.bestdoStadiums.control.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bestdo.bestdoStadiums.utils.CommonUtils;
import com.bestdo.bestdoStadiums.utils.MyDialog;
import com.bestdo.bestdoStadiums.R;

/**
 * 唯一设备登录提示
 * 
 * @author jun
 * 
 */
public class UserLoginBack403Utils {
	private static UserLoginBack403Utils mUtils;

	public synchronized static UserLoginBack403Utils getInstance() {
		if (mUtils == null) {
			mUtils = new UserLoginBack403Utils();
		}
		return mUtils;
	}

	/**
	 * 提示设备重新登录
	 */
	public void showDialogPromptReLogin(final Activity context) {
		final MyDialog selectDialog = new MyDialog(context, R.style.dialog, R.layout.dialog_logout);
		selectDialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域关闭Dialog
		selectDialog.show();
		selectDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				return true;
			}
		});
		TextView myexit_text_off = (TextView) selectDialog.findViewById(R.id.myexit_text_off);
		TextView text_sure = (TextView) selectDialog.findViewById(R.id.myexit_text_sure);
		myexit_text_off.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectDialog.dismiss();
				CommonUtils.getInstance().clearAllBestDoInfoSharedPrefs(context);
				CommonUtils.getInstance().skipMainActivity(context);
			}
		});
		text_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectDialog.dismiss();
				CommonUtils.getInstance().clearAllBestDoInfoSharedPrefs(context);
				CommonUtils.getInstance().setLoginBack403(context);
			}
		});
	}

}
