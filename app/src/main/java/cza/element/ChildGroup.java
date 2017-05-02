package cza.element;

import cza.MyFE.R;
import cza.util.Pull;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ChildGroup extends BaseChild {
	
	public ChildGroup(Context context, Pull pull){
		super(pull);
		mView = View.inflate(context, R.layout.widget_title, null);
		TextView titleView = (TextView)mView.findViewById(R.id.titleView);
		titleView.setText(mTitle);
	}

	@Override
	public void setValue(int value) {
		// TODO: Implement this method
	}

	@Override
	public int getValue() {
		// TODO: Implement this method
		return 0;
	}
}
