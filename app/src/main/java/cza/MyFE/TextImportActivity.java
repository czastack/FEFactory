package cza.MyFE;

import cza.hack.Coder;
import cza.util.ViewUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TextImportActivity extends RomTextActivity implements 
View.OnClickListener {

	private static final int REQUEST_LOAD_DATA = 0;
	private static final String 
	ATTR_START = "start=",
	ATTR_STEP = "step=";
	
	private View btnWrite;
	private CheckBox mUseHuffmanView;
	private int mStartAddr = 0x01000000;
	private int mStep = 0x10;
	private int mNeededCapacity;
	
	@Override
    public void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		setContentView(R.layout.text_import_activity);
		mUseHuffmanView = (CheckBox)findViewById(R.id.option_useHuffman);
		mListView = (ListView)findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		findView(R.id.btn_browser).setOnClickListener(this);
		btnWrite = findView(R.id.btn_write);
		btnWrite.setOnClickListener(this);
		ViewUtils.hide(btnWrite, true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_browser:
				loadData();
				break;
			case R.id.btn_write:
				write();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK) return;
		String path = intent.getStringExtra("path");
		switch (requestCode) {
			case REQUEST_LOAD_DATA:
				loadData(path);
				break;
		}
	}

	public void loadData(){
		Intent intent = new Intent(this, FileActivity.class)
			.putExtra(INTENT_MODE, FileActivity.MODE_PICKFILE)
			.putExtra(INTENT_TITLE,getString(R.string.chooseTypeFile, "txt"))
			.putExtra(INTENT_PATH, MyApplication.TEXT_DIR)
			.putExtra(INTENT_TYPE, "txt");
		startActivityForResult(intent, REQUEST_LOAD_DATA);
	}
	
	public void loadData(String path){
		BufferedReader reader = null;
		int lineIndex = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line;
			char ch;
			int i;
			int length;
			ArrayList<Data> tempList = new ArrayList<Data>();
			Data data;
			int index;
			while ((line = reader.readLine()) != null && line.isEmpty()) {
				lineIndex++;
				continue;
			}
			while (line != null && !line.isEmpty()) {
				if (line.startsWith(ATTR_START))
					mStartAddr = Coder.readBytes(line, ATTR_START.length());
				else if (line.startsWith(ATTR_STEP))
					mStep = Coder.readBytes(line, ATTR_STEP.length());
				line = reader.readLine();
				lineIndex++;
			}
			while ((line = reader.readLine()) != null && !line.isEmpty()){
				lineIndex++;
				if (line.isEmpty())
					continue;
				length = line.length();
				i = 0;
				while (i < length && !Coder.isHex(line.charAt(i))){
					//跳过序号前的空白
					i++;
				}
				if (i == length)
					continue;
				index = Coder.readBytes(line, i);
				while (i < length && (Coder.isHex(ch = line.charAt(i)) || Character.isSpace(ch))){
					//跳过序号及之后的空白
					i++;
				}
				if (i == length)
					continue;
				data = new Data();
				data.index = index;
				data.text = line.substring(i);
				tempList.add(data);
			}
			mDatas = new Data[tempList.size()];
			mDatas = tempList.toArray(mDatas);
			ViewUtils.hide(btnWrite, false);
		} catch (Exception e) {
			toast(getString(R.string.loadRomFailed, lineIndex));
		}
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {}
		}
		processAddr();
	}
	
	public void processAddr(){
		if (mDatas == null)
			return;
		int addr = mStartAddr & FEReader.ROM_FLAG;
		int size;
		final int step = mStep;
		final int mask = ~(mStep - 1);
		for (Data data : mDatas){
			data.addr = addr;
			size = data.text.length() << 1;
			addr += (size & mask) + step;
		}
		mNeededCapacity = addr;
		refresh();
	}
	
	private void write() {
		long pointerFlag;
		try {
			mReader.ensureCapacity(mNeededCapacity);
			if (mUseHuffmanView.isChecked()) {
				for (Data data : mDatas) {
					mReader.writeHuffman(data.addr, data.text);
				}
				pointerFlag = mReader.POINTER_FLAG;
			} else {
				for (Data data : mDatas) {
					mReader.writeDictCode(data.addr, data.text);
				}
				pointerFlag = mReader.PATCHED_POINTER_FLAG;
			}
			int pointerAddr;
			long pointer;
			for (Data data : mDatas) {
				pointerAddr = mReader.mRom.getPointer(data.index);
				pointer = data.addr | pointerFlag;
				mReader.writeWord(pointerAddr, pointer);
			}
		} catch (IOException e) {
			toast(R.string.capacityFailed);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			toast(e.getMessage());
			return;
		}
		mReader.notifyDataChanged();
	}
}
