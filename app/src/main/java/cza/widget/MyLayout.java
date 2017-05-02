package cza.widget;

import android.content.Context;
import android.widget.LinearLayout;
import cza.app.MyApp;

public class MyLayout extends LinearLayout {
	public MyLayout(int def, Context c) {
		super(c, null, def);
	}

	public MyLayout(Context c, int o){
		super(c);
		set(o, LP.FW);
	}

	public void set(int o, LP lp){
		setOrientation(o);
		setLayoutParams(lp);
	}

	void setPadding(int i){
		i = MyApp.dip2px(i);
		setPadding(i, i, i, i);
	}
}
