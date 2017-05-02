package cza.app;

import cza.MyFE.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import java.io.File;
import java.lang.reflect.Method;

public class MyActivity extends Activity implements Dialog.OnOKListener {
	protected LayoutInflater mInflater;
	protected View rootLayout;
	
	public static final String 
	INTENT_AUTO_OPEN = "autoOpen",
	INTENT_AUTO_MENU = "autoMenu",
	INTENT_CHANGED = "changed",
	INTENT_CHECKED = "checked",
	INTENT_PATH = "path",
	INTENT_SAVA_AS_NAME = "saveAsName",
	INTENT_SCREEN_TEXT = "screenText",
	INTENT_SCREEN_TYPE = "screenType",
	INTENT_TYPE = "type",
	INTENT_TITLE = "title",
	INTENT_MODE = "mode",
	INTENT_IS_MYBOY = "isMyBoy",
	INTENT_PLAYING = "playing";

	protected void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		ActionBar actionbar = getActionBar();
		if (actionbar != null)
			actionbar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setContentView(int layoutResID) {
		mInflater = LayoutInflater.from(this);
		setContentView(rootLayout = inflateView(layoutResID));
	}

	protected View inflateView(int layoutResID){
		return mInflater.inflate(layoutResID, null);
	}
	
	protected View findView(int id){
		return rootLayout.findViewById(id);
	}
	
	/**
	 * 平板隐藏系统栏
	 */
	protected void hideSystemBar(boolean hidden){
		getWindow().getDecorView().setSystemUiVisibility(hidden ? 
			View.STATUS_BAR_HIDDEN : 
			View.STATUS_BAR_VISIBLE);
	}
	
	/**
	 * 隐藏状态栏
	 */
	protected void hideStatusBar(){
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
			WindowManager.LayoutParams. FLAG_FULLSCREEN);
	}
	
	/**
	 * 菜单项显示图标
	 */
	protected void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
			m.setAccessible(true);
			m.invoke(menu, enable);
		} catch (Exception e) {}
	}
	
	protected void toast(CharSequence text){
		MyApp.toast(this, text);
	}

	protected void toast(int resId){
		MyApp.toast(this, resId);
	}
	
	protected void alert(String title, CharSequence text){
		new EditDialog(this, EditDialog.MODE_SHOW).setCopy().showText(title, text);
	}
	
	protected void alert(Exception e){
		StringBuilder sb = new StringBuilder();
		sb.append(e.toString()).append('\n');
		StackTraceElement[] list = e.getStackTrace();
		for (int i = 0; i < list.length; i++){
			sb.append(i)
				.append('.')
				.append(list[i])
				.append('\n');
		}
		alert("错误", sb);
	}
	
	/**
	 * 打开之前的Activity
	 */
	protected void bringToFront(Class<? extends Activity> klass){
		startActivity(getToFrontIntent(klass));
	}
	
	public Intent getToFrontIntent(Class<? extends Activity> klass){
		return new Intent(this, klass)
			.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	}
	
	/**
	 * 检查文件
	 */
	private Dialog fileNotFoundDialog;
	
	public boolean ensureFile(File file){
		if (file.exists())
			return true;
		Dialog dialog = fileNotFoundDialog;
		if (dialog == null){
			fileNotFoundDialog = dialog = new Dialog(this);
			dialog.setTitle(R.string.fileNotFound);
			dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.quite), dialog);
		}
		dialog.showMsg(getString(R.string.fileNotFoundBelow, file.getPath()));
		return false;
	}

	@Override
	public boolean onOK(Dialog dialog) {
		if (fileNotFoundDialog == dialog) 
			finish();
		return true;
	}
}
