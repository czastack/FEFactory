package cza.MyFE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import cza.app.ConfirmDialog;
import cza.app.Dialog;
import cza.app.Shortcut;
import cza.element.BaseParent;
import cza.element.Element;
import cza.element.GroupParent;
import cza.element.SelectParent;
import cza.hack.Coder;
import cza.util.Pull;
import cza.widget.StringArrayAdapter;

public class MainActivity extends MyActivity implements 
View.OnClickListener,
AdapterView.OnItemSelectedListener,
FEReader.Callback,
Dialog.OnClickListener {
	
	private static final int 
	REQUEST_LOAD_ROM = 0,
	REQUEST_SAVE_ROM = 1,
	SAVE_CHANGE_NONE = 0,
	SAVE_CHANGE_OUTER = 2, //页间
	SAVE_CHANGE_LOOK_UP_LOG = 3,
	SAVE_CHANGE_SAVE = 4,
	SAVE_CHANGE_SAVE_AS = 5;
	private static final String
	PREFER_LAST_ROM_DIR = "lastRomDir";
	
	private MyApplication qz;
	private String mRomDir;
	private boolean dirChanged;
	private Rom[] mRoms;
	private FEReader mReader;
	private BaseParent[] mElements;
	private BaseParent mElement;
	private boolean mSkipNextChange; //是否忽略子控件保存提示框
	private int mLastIndex; //LoadElements 设为了-1
	private Spinner mFunctionView;
	private ViewGroup mElementParent;
	private int mSaveChangeMode;
	
	@Override
    public void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		if (!ensureFile(MyApplication.SD))
			return;
		qz = (MyApplication) getApplication();
		Intent intent = getIntent();
		setContentView(R.layout.main_activity);
		mFunctionView = (Spinner)findView(R.id.functionView);
		mElementParent = (ViewGroup)findView(R.id.layout);
		View btn = findView(R.id.menu_openRom);
		btn.setOnClickListener(this);
		mFunctionView.setEmptyView(btn);
		mFunctionView.setOnItemSelectedListener(this);
		//读取路径
		SharedPreferences prefer = getPreferences(MODE_PRIVATE);
		mRomDir = prefer.getString(PREFER_LAST_ROM_DIR, MyApplication.SDPath);
		if (Element.mReader == null) {
			Element.mReader = mReader = new FEReader();
		} else {
			mReader = Element.mReader;
		}
		mReader.setCallback(this);
		loadData();
		if (intent.getData() != null){
			loadRom(intent.getData().getPath());
		}
    }

	@Override
	protected void onDestroy() {
		if (dirChanged) {
			SharedPreferences prefer = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = prefer.edit();
			editor.putString(PREFER_LAST_ROM_DIR, mRomDir);
			editor.commit();
			//解除引用，避免内存泄露
			mReader.mRom = null;
			mReader.mCallback = null;
			try {
				mReader.close();
			} catch (Exception e) {}
		}
		super.onDestroy();
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity, menu);
		inflater.inflate(R.menu.create_shortcut, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_openRom) {
			loadRom();
			return true;
		}
		else if (id == android.R.id.home){
			onQuite();
			return true;
		}
		else if (!noRom()) {
			switch (id) {
				case R.id.menu_runGame:
					runGame();
					return true;
				case R.id.menu_save:
					saveRom();
					return true;
				case R.id.menu_saveAs:
					saveAsRom();
					return true;
				case R.id.menu_clearChange:
					showClearChangeDialog();
					return true;
				case R.id.menu_lookUpLog:
					showLookUpLogDialog();
					return true;
				case R.id.menu_textConver:
					bringToFront(TextConverActivity.class);
					return true;
				case R.id.menu_textTable:
					bringToFront(TextTableActivity.class);
					return true;
				case R.id.menu_createShortcut:
					createShortCut();
					return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.menu_openRom:
				loadRom();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK) return;
		String path = intent.getStringExtra("path");
		switch (requestCode) {
			case REQUEST_LOAD_ROM:
				loadRom(path);
				break;
			case REQUEST_SAVE_ROM:
				saveAsRom(path);
				break;
		}
	}

	@Override
	public boolean onOK(Dialog dialog) {
		if (dialog == mClearChangeDialog){
			mReader.mLogs.clear();
			mElement.read();
		}
		else if (ConfirmDialog.class.isInstance(dialog)){
			int id = ((ConfirmDialog)dialog).getId();
			if (id == R.id.dialogQuite){
				finish();
				return true;
			}
		}
		return super.onOK(dialog);
	}

	@Override
	public void onItemSelected(AdapterView<?> view, View item, int position, long id) {
		if (view == mFunctionView)
			changeElement(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {}
	
	/**
	 * 读取配置
	 */
	private void loadData(){
		Rom[] roms =new Rom[3];
		int i = 0;
		Pull pull = new Pull();
		try {
			pull.start(getAssets().open("gameData.xml"));
			int type;
			AssetManager asset = getAssets();
			while ((type = pull.parser.next()) != 1 && i < roms.length) {
				if (type != 2) continue;
				if (Rom.TAG.equals(pull.parser.getName())) {  
					Rom rom = new Rom();
					rom.title = pull.getValue(Rom.TITLE);
					rom.name = pull.getValue(Rom.NAME);
					rom.dir = pull.getValue(Rom.DIR);
					rom.pointerEnd = Coder.fromHex(pull.getValue(Rom.POINTER_END));
					rom.mAsset = asset;
					roms[i++] = rom;
				}
			}
		} catch (Exception e) {}
		mRoms = roms;
	}

	/**
	 * 加载Rom
	 */
	private void loadRom(){
		Intent intent = new Intent(this, FileActivity.class)
			.putExtra(INTENT_MODE, FileActivity.MODE_PICKFILE)
			.putExtra(INTENT_TITLE,getString(R.string.chooseTypeFile, "gba"))
			.putExtra(INTENT_PATH, mRomDir)
			.putExtra(INTENT_TYPE, "gba");
		startActivityForResult(intent, REQUEST_LOAD_ROM);
	}
	
	private void loadRom(String path){
		Rom mRom = null;
		boolean succeed = false;
		try {
			mReader.load(path);
			String dir = mReader.mFile.getParent();
			if (!mRomDir.equals(dir)) {
				mRomDir = dir;
				dirChanged = true;
			}
			String romTitle = mReader.getRomInfo().trim();
			for (Rom rom: mRoms) {
				if (rom.title.equals(romTitle)){
					mRom = rom;
					succeed = true;
					break;
				}
			}
		} catch (Exception e) {}
		if (succeed){
			toast(mRom.title);
			setTitle(mRom.name);
			try {
				mReader.mDict.load(mRom.open(Rom.FILE_DICT));
				mReader.loadRom(mRom);
				loadElements(mRom);
			} catch (Exception e) {alert(e);}
		}
	}

	/**
	 * 加载组件
	 */
	private void loadElements(Rom rom) throws Exception  {
		BaseParent[] elements = null;
		String[] titleList = null;
		Pull pull = new Pull();
		pull.start(rom.open(Rom.FILE_ELEMENT_LIST));
		int type;
		int index = 0;
		int count = 15;
		BaseParent element;
		while ((type = pull.parser.next()) != 1) {
			if (type != 2)
				continue;
			if (index == count)
				break;
			element = null;
			String tag = pull.parser.getName();
			if (Element.TAG_PARENT_SELECT.equals(tag)) {
				element = new SelectParent(this, pull);
			} else if (Element.TAG_PARENT_GROUP.equals(tag)) {
				element = new GroupParent(this, pull);
			} else if (Element.TAG_ELEMENTS.equals(tag)) {
				count = pull.getInt(Element.ATTR_COUNT, count);
				elements = new BaseParent[count];
				titleList = new String[count];
			}
			if (element != null) {
				titleList[index] = pull.getValue(Element.ATTR_TITLE);
				elements[index++] = element;
			}
		}
		mElements = elements;
		mLastIndex = -1;
		mFunctionView.setAdapter(new StringArrayAdapter(this, titleList, StringArrayAdapter.TYPE_SPINNER));
	}
	
	/**
	 * 切换组件
	 */
	private void changeElement(int position){
		if (mLastIndex == position) {
			//取消保存修改
			return;
		}
		if (!checkChange(SAVE_CHANGE_OUTER)) {
			mLastIndex = position;
			mElement = mElements[position];
			View view = mElement.getView();
			if (view != null) {
				mElementParent.removeAllViews();
				mElementParent.addView(view);
			}
		}
	}
	
	private boolean noRom(){
		if (mReader.mFile == null){
			toast(R.string.loadRomFirst);
			return true;
		}
		return false;
	}
	
	/**
	 * 检测修改
	 * @return 是否已修改
	 */
	public boolean checkChange(int mode){
		if (mSkipNextChange) {
			if (mode != SAVE_CHANGE_LOOK_UP_LOG)
				mSkipNextChange = false;
			return false;
		}
		if (mSaveChangeMode != mode && mElement != null && mElement.checkChanged()) {
			mSaveChangeMode = mode;
			onSaveChange();
			return true;
		}
		return false;
	}

	/**
	 * 保存ROM
	 */
	private void saveRom(){
		if (!checkChange(SAVE_CHANGE_SAVE))
			mReader.save();
	}
	
	private void saveAsRom(){
		if (!checkChange(SAVE_CHANGE_SAVE_AS)) {
			Intent intent = new Intent(this, FileActivity.class)
				.putExtra(INTENT_MODE, FileActivity.MODE_SAVE)
				.putExtra(INTENT_PATH, mRomDir)
				.putExtra(INTENT_SAVA_AS_NAME, mReader.mFile.getName());
			startActivityForResult(intent, REQUEST_SAVE_ROM);
		}
	}
	
	private void saveAsRom(String path){
		try {
			mReader.saveAs(path);
		} catch (Exception e) {}
	}
	
	/**
	 * 清空修改
	 */
	private Dialog mClearChangeDialog;
	public void showClearChangeDialog(){
		Dialog dialog = mClearChangeDialog;
		if (dialog == null){
			dialog = new Dialog(this);
			mClearChangeDialog = dialog;
			dialog.setTitle(R.string.clearChange);
			dialog.setMessage(R.string.clearChangeMsg);
			dialog.setConfirm();
			dialog.setOnOKListener(this);
		}
		dialog.show();
	}
	
	/**
	 * 保存修改
	 */
	private Dialog mSaveChangeDialog;
	
	public void onSaveChange() {
		Dialog dialog = mSaveChangeDialog;
		if (dialog == null) {
			dialog = new SaveChangeDialog(this, mReader.mTempLogs, false);
			mSaveChangeDialog = dialog;
			dialog.setTitle(R.string.saveChange);
			dialog.addHeader(R.layout.element_save_change_dialog);
			dialog.setButton(Dialog.BUTTON_POSITIVE, "保存", this);
			dialog.setButton(Dialog.BUTTON_NEGATIVE, "舍弃", this);
			dialog.setButton(Dialog.BUTTON_NEUTRAL, "返回", this);
		}
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == mSaveChangeDialog){
			boolean keep = false;
			switch (which){
				case Dialog.BUTTON_POSITIVE:
					mReader.addLog();
					break;
				case Dialog.BUTTON_NEUTRAL:
					keep = true;
					break;
			}
			mSaveChangeDialog.close();
			if(mSaveChangeMode != SAVE_CHANGE_LOOK_UP_LOG)
				mElement.onCheckChangeCallback(keep);
			switch (mSaveChangeMode){
				case SAVE_CHANGE_LOOK_UP_LOG:
					if(!keep) {
						showLookUpLogDialog();
						mSkipNextChange = true;
					}
					break;
				case SAVE_CHANGE_OUTER:
					if (keep)
						mFunctionView.setSelection(mLastIndex);
					else 
						changeElement(mFunctionView.getSelectedItemPosition());
					break;
				case SAVE_CHANGE_SAVE:
					if(!keep)
						saveRom();
					break;
				case SAVE_CHANGE_SAVE_AS:
					if(!keep)
						saveAsRom();
					break;
			}
			mSaveChangeMode = SAVE_CHANGE_NONE;
		}
	}
	
	/**
	 * 查看修改记录
	 */
	private Dialog mLookUpLogDialog;
	
	private void showLookUpLogDialog(){
		if (!checkChange(SAVE_CHANGE_LOOK_UP_LOG)){
			if (mReader.mLogs.isEmpty()){
				toast(R.string.noSavedLog);
				return;
			}
			Dialog dialog = mLookUpLogDialog;
			if (dialog == null) {
				dialog = new SaveChangeDialog(this, mReader.mLogs, true);
				mLookUpLogDialog = dialog;
				dialog.setTitle(R.string.changeLog);
				dialog.setBack();
			}
			dialog.show();
		}
	}
	
	@Override
	public void onRomWrite() {
		toast(getString(R.string.saveSucceed, mReader.mFile.getPath()));
		for (BaseParent element : mElements){
			element.read();
		}
	}
	
	/**
	 * 运行游戏
	 */
	public void runGame() {
		if (noRom())
			return;
		try {
			startActivity(MyApplication.openFile(mReader.mFile));
		} catch (Exception e) {}
	}

	/**
	 * 创建快捷方式
	 */
	public void createShortCut(){
		if (noRom())
			return;
		try {
			Shortcut shortcut = new Shortcut()
				.setTitle(mReader.mRom.name)
				.setIcon(MyApplication.readBitmap(mReader.mRom.open(Rom.FILE_LOGO)))
				.setIntent(new Intent(this, getClass()).setData(Uri.fromFile(mReader.mFile)));
			sendBroadcast(shortcut);
		} catch (Exception e) {}
	}

	private void onQuite(){
		Dialog dialog = new ConfirmDialog(R.id.dialogQuite, this, R.string.quite, R.string.confirmQuite);
		dialog.setOnOKListener(this);
		dialog.show();
	}
}

