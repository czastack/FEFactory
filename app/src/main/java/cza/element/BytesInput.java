package cza.element;

import cza.MyFE.R;
import cza.util.Pull;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class BytesInput extends BaseChild {
	private BytesEditText mInput;
	
	public BytesInput(Context context, Pull pull){
		super(pull);
		View view = View.inflate(context, R.layout.element_child_bytes_input, null);
		TextView titleView = (TextView)view.findViewById(R.id.titleView);
		titleView.setText(mTitle);
		titleView.setOnLongClickListener(this);
		mInput = (BytesEditText)view.findViewById(R.id.editView);
		mView = view;
	}

	@Override
	public void setValue(int value) {
		mInput.setValue(value, mSize);
	}

	@Override
	public int getValue() {
		return (int)mInput.getValue();
	}
}
