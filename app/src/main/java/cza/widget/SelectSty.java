package cza.widget;

import cza.MyFE.R;
import cza.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectSty extends LinearLayout implements View.OnClickListener {
	
	public static int margin, marginX;
	protected TextView text_title, text_hint;
	public String mTitle;
	public Dialog dialog;

	public SelectSty(Context c, String title){
		super(c, null, R.attr.clickableBar);
		initMargin(this);
		setOrientation(1);
		LayoutInflater mInflater = LayoutInflater.from(c);
		mInflater.inflate(R.layout.widget_selectsty, this, true);
		text_title = (TextView) findViewById(R.id.title);
		text_hint = (TextView) findViewById(R.id.hint);
		text_title.setText(mTitle = title);
		setOnClickListener(this);
	}

	@Override
	public void setOrientation(int o) {
		super.setOrientation(o);
		int px = o == 0 ? margin : marginX;
		setPadding(px, margin, px, margin);
	}
	
	@Override
	public void onClick(View v) {
		dialog.show();
	}

	public void setHint(CharSequence text){
		text_hint.setText(text);
	}
	
	public static void initMargin(View v){
		if (margin == 0){
			Resources res = v.getResources();
			margin = res.getDimensionPixelSize(R.dimen.selectsty_padding);
			marginX = res.getDimensionPixelSize(R.dimen.selectsty_padding_x);
		}
	}
}
