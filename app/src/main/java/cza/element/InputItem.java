package cza.element;

import cza.MyFE.R;
import cza.hack.Coder;
import cza.hack.HexFilter;
import cza.util.Pull;
import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.TextView;

public class InputItem extends BaseChild {
	
	public static InputFilter[][] FILTERS = {
		null,
		{new HexFilter(2)},
		{new HexFilter(4)},
		null,
		{new HexFilter(8)},
	};
	
	private TextView mInput;
	
	public InputItem(Context context, Pull pull){
		super(pull);
		boolean readOnly = pull.getBoolean(ATTR_READ_ONLY);
		int resId = readOnly ? 
			R.layout.element_child_input_item_read_only:
			R.layout.element_child_input_item;
		View view = View.inflate(context, resId, null);
		TextView titleView = (TextView)view.findViewById(R.id.titleView);
		titleView.setText(mTitle);
		titleView.setOnLongClickListener(this);
		mInput = (TextView)view.findViewById(R.id.editView);
		if (!readOnly){
			mInput.setFilters(FILTERS[mSize]);
		}
		mView = view;
	}

	@Override
	public void setValue(int value) {
		if (mSize > 0)
			mInput.setText(Coder.toHexString(value, mSize));
		else 
			mInput.setText(Coder.toWordString(mAddr));
	}

	@Override
	public int getValue() {
		return Coder.readBytes(mInput.getText(), 0, mSize);
	}
}
