package cza.element;

import cza.MyFE.R;
import cza.app.Dialog;
import cza.hack.Coder;
import cza.hack.HackLog;
import cza.util.Pull;
import android.view.View;
import android.widget.TextView;

public abstract class BaseChild extends Element implements View.OnLongClickListener {
	public int mOffset;
	public int mSize;
	public int mAddr;
	public int defaultValue;
	public String mTitle;
	protected View mView;
	
	public BaseChild(Pull pull){
		mOffset = pull.getInt(ATTR_OFFSET);
		mSize = pull.getInt(ATTR_SIZE);
		mAddr = Coder.fromHex(pull.getValue(ATTR_ADDR));
		mTitle = pull.getValue(ATTR_TITLE);
	}

	public int getOffset() {
		return mOffset;
	}

	public int getSize() {
		return mSize;
	}
	
	public int getAddr(){
		return mAddr;
	}

	@Override
	public View getView() {
		return mView;
	}

	@Override
	public boolean onLongClick(View v) {
		if (mSize < 1)
			return false;
		Dialog dialog = new Dialog(v.getContext());
		dialog.setTitle(mTitle);
		dialog.setBack();
		dialog.setView(R.layout.element_child_dialog);
		TextView addrView = (TextView)dialog.findView(R.id.addrView);
		TextView sizeView = (TextView)dialog.findView(R.id.sizeView);
		TextView defaultValueView = (TextView)dialog.findView(R.id.defaultValueView);
		addrView.setText(Coder.toWordString(mAddr));
		sizeView.setText(Integer.toString(mSize));
		defaultValueView.setText(Coder.toHexString(defaultValue, mSize));
		dialog.show();
		return true;
	}

	public void read(int addr) {
		if (addr > 0)
			mAddr = addr;
		defaultValue = (int)mReader.read(mAddr, mSize);
		setValue(defaultValue);
	}

	public void write(int addr) {
		if (mSize == 0)
			return;
		if (addr > 0)
			mAddr = addr;
		//
	}

	public void checkChanged() {
		int value = getValue();
		if (value != defaultValue){
			HackLog log = new HackLog();
			log.title = mTitle;
			log.addr = mAddr;
			log.value = value;
			log.size = mSize;
			mReader.mTempLogs.add(log);
		}
	}

	public abstract void setValue(int value);
	
	public abstract int getValue();
}
