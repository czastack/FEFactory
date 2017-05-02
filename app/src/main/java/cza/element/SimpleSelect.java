package cza.element;

import cza.MyFE.R;
import cza.hack.Coder;
import cza.util.ArrayUtils;
import cza.util.Pull;
import cza.widget.StringArrayAdapter;
import android.content.Context;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class SimpleSelect extends BaseChild {
	
	public int[] mValues;
	public Spinner mSelect;
	public View mView;
	
	public SimpleSelect(Context context, Pull pull){
		super(pull);
		mView = View.inflate(context, R.layout.element_simple_select, null);
		TextView titleView = (TextView)mView.findViewById(R.id.textView);
		titleView.setText(mTitle);
		titleView.setOnLongClickListener(this);
		mSelect = (Spinner)mView.findViewById(R.id.selectView);
		String[] items = mReader.mRom.readEntries(pull.getValue(ATTR_ENTRIES));
		StringArrayAdapter adapter = new StringArrayAdapter(context, items, StringArrayAdapter.TYPE_SPINNER);
		mSelect.setAdapter(adapter);
		int length = items.length;
		int[] values = new int[length];
		for (int i = 0; i < length; i++){
			values[i] = Coder.readBytes(items[i], 0, 1);
		}
		mValues = values;
	}
	
	public void setSelection(int value){
		mSelect.setSelection(ArrayUtils.indexOf(mValues, value));
	}

	@Override
	public View getView() {
		return mView;
	}

	@Override
	public void setValue(int value) {
		setSelection(value);
	}

	public int getValue(){
		return mValues[mSelect.getSelectedItemPosition()];
	}
}
