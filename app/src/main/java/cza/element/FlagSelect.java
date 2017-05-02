package cza.element;

import cza.app.ListDialog;
import cza.hack.Coder;
import cza.util.Pull;
import cza.widget.SelectSty;
import android.content.Context;
import android.view.View;

public class FlagSelect extends BaseChild implements 
ListDialog.OnSubmitListener {
	public int mValue;
	private SelectSty mView;
	private String[] mItems;
	public StringBuilder mText = new StringBuilder();
	
	public FlagSelect(Context c, Pull pull){
		super(pull);
		mView = new SelectSty(c, mTitle);
		String[] items = Element.mReader.mRom.readEntries(pull.getValue(ATTR_ENTRIES));
		mView.setOrientation(SelectSty.HORIZONTAL);
		ListDialog dialog = new ListDialog(c);
		dialog.setTitle(mView.mTitle);
		dialog.setOnSubmitListener(this);
		mItems = items;
		dialog.setItems(items, null, null);
		mView.dialog = dialog;
		mView.setOnLongClickListener(this);
		mText.append("xy=");
	}

	@Override
	public View getView() {
		return mView;
	}
	
	public void setValue(int value){
		mValue = value;
		boolean[] list = new boolean[8];
		for (int i = 0; i < list.length; i++){
			if ((value & (1 << i)) != 0) {
				list[i] = true;
				mText.append(mItems[i]).append('|');
			}
		}
		((ListDialog)mView.dialog).setItems(mItems, list, null);
		refreshHint();
	}

	@Override
	public int getValue() {
		return mValue;
	}
	
	@Override
	public void onSubmit(ListDialog dialog, int[] checkedIndexs) {
		int value = 0;
		for (int i : checkedIndexs){
			value |= 1 << i;
			mText.append(mItems[i]).append('|');
		}
		mValue = value;
		refreshHint();
	}
	
	private void refreshHint(){
		int last = mText.length() - 1;
		if (last > 2){
			mText.replace(0, 2, Coder.toByteString(mValue));
			mText.deleteCharAt(last);
			mView.setHint(mText);
			mText.delete(3, last);
		} else {
			mView.setHint(Coder.toByteString(mValue));
		}
	}

	public void read() {
		setValue(mReader.readByte(mAddr));
	}

	public void write() {
		
	}
}
