package cza.MyFE;

import cza.element.Element;
import cza.hack.Coder;
import cza.widget.StringArrayAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import cza.app.Dialog;

public class TextTableActivity extends RomTextActivity implements 
AdapterView.OnItemSelectedListener,
AdapterView.OnItemLongClickListener {
	
	private Spinner mRangeView;
	private int mTotalCount;
	private int mItemCount;
	private int mTailCount;
	private int mLastRange;

	@Override
    public void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		if (!checkEnvironment())
			return;
		setContentView(R.layout.text_table_activity);
		mRangeView = (Spinner)findViewById(R.id.rangeView);
		mListView = (ListView)findViewById(R.id.list);
		mRangeView.setOnItemSelectedListener(this);
		Data[] datas = new Data[0x100];
		for (int i = 0; i < datas.length; i++){
			Data data = new Data();
			datas[i] = data;
		}
		mDatas = datas;
		changeRange(0);
		mListView.setAdapter(mAdapter);
		//长按对话框
		mListView.setOnItemLongClickListener(this);
		Rom rom = Element.mReader.mRom;
		mTotalCount = ((rom.pointerEnd - rom.pointerStart) >> 2) + 1;
		int rangeCount = mTotalCount >> 8;
		mTailCount = mTotalCount - (rangeCount << 8);
		mLastRange = rangeCount++;
		StringBuilder sb = new StringBuilder();
		String[] rangeArray = new String[rangeCount];
		int start;
		int end;
		for (int i = 0; i < rangeCount; i++){
			start = i << 8;
			end = start + (i == mLastRange ? mTailCount : 0xFF);
			sb.delete(0, 9);
			sb.append(Coder.toHalfWordString(start)).append('~').append(Coder.toHalfWordString(end));
			rangeArray[i] = sb.toString();
		}
		StringArrayAdapter rangeAdapter = new StringArrayAdapter(this, rangeArray, StringArrayAdapter.TYPE_SPINNER);
		mRangeView.setAdapter(rangeAdapter);
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.text_table_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_outputText:
				outputText();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> view, View item, int position, long id) {
		if (view == mRangeView) {
			if (position == mLastRange)
				mItemCount = mTailCount;
			else 
				mItemCount = mDatas.length;
			changeRange(position);
			refresh();
			mListView.setSelection(0);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {}
	
	/**
	 * 列表长按
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> view, View item, int position, long id) {
		showDetailDialog(position);
		return true;
	}
	
	private void changeRange(int position){
		int offset = position << 8;
		int length = mItemCount;
		Data[] datas = mDatas;
		Rom rom = Element.mReader.mRom;
		for (int i = 0; i < length; i++){
			Data data = datas[i];
			data.index = offset | i;
			data.addr = rom.getPointer(data.index);
			data.text = null;
		}
	}
	
	/**
	 * 详细列表
	 */
	private Dialog mDetailDialog;
	private TextView mIndexView, mPointerAddrView, mPointerView, mCodeView, mTextView;
	
	private void showDetailDialog(int position){
		Dialog dialog = mDetailDialog;
		if (dialog == null){
			dialog = new Dialog(this);
			mDetailDialog = dialog;
			dialog.setTitle(R.string.text);
			dialog.setView(R.layout.text_table_activity_detail);
			dialog.setBack();
			mIndexView = (TextView)dialog.findView(R.id.indexView);
			mPointerAddrView = (TextView)dialog.findView(R.id.pointerAddrView);
			mPointerView = (TextView)dialog.findView(R.id.pointerView);
			mCodeView = (TextView)dialog.findView(R.id.inputCodeView);
			mTextView = (TextView)dialog.findView(R.id.inputTextView);
		}
		Data data = mDatas[position];
		mIndexView.setText(Coder.toHalfWordString(data.index));
		mPointerAddrView.setText(Coder.toWordString(data.addr));
		mPointerView.setText(Coder.toWordString(Element.mReader.readWord(data.addr)));
		mCodeView.setText(Element.mReader.mDict.getCode(data.text, false));
		mTextView.setText(data.text);
		dialog.show();
	}
	
	
	public void outputText(){
		
	}
	
	//填充数据
	@Override
	public int getCount() {
		return mItemCount;
	}
}
