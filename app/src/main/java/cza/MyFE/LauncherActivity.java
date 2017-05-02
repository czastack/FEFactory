package cza.MyFE;

import cza.app.MenuAdapter;
import cza.app.Shortcut;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class LauncherActivity extends MyActivity implements 
		AdapterView.OnItemClickListener {

	private ListView mListView;
	private MenuAdapter mAdapter;
	
	@Override
    public void onCreate(Bundle sIS) {
		super.onCreate(sIS);
		setContentView(R.layout.launcher_activity);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.launcher, menu);
		mAdapter = new MenuAdapter(this, menu);
		mListView.setAdapter(mAdapter);
		//真正的菜单
		inflater.inflate(R.menu.create_shortcut, menu);
		return false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adpt, View item, int position, long id) {
		Class<? extends Activity> klass = null;
		switch ((int)id) {
			case R.id.menu_mainActivity:
				klass = MainActivity.class;
				break;
			case R.id.menu_calculator:
				klass = CalculatorActivity.class;
				break;
			case R.id.menu_filebrowser:
				klass = FileActivity.class;
				break;
			case R.id.menu_textImport:
				klass = TextImportActivity.class;
				break;
			case R.id.menu_createShortcut:
				createShortCut();
				return;
		}
		if (klass != null)
			bringToFront(klass);
	}

	/**
	 * 创建快捷方式
	 */
	public void createShortCut(){
		Shortcut shortcut = new Shortcut()
			.setTitle(getTitle())
			.setIcon(this, android.R.drawable.ic_menu_preferences)
			.setIntent(new Intent(this, getClass()));
		sendBroadcast(shortcut);
	}
}
