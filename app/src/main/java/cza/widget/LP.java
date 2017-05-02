package cza.widget;

import android.widget.LinearLayout;
import cza.app.MyApp;

public class LP extends LinearLayout.LayoutParams {
	public static int fit(int i){
		return i > 0 ? MyApp.dip2px(i) : i;
	}

	public LP(int w, int h) {
		super(fit(w), fit(h));
	}

	public LP(int w, int h, int wt) {
		this(w, h);
		weight = wt;
	}

	public LP setMargin(int i){
		i = MyApp.dip2px(i);
		setMargins(i, i, i, i);
		return this;
	}

	public LP setGravity(int i){
		gravity = i;
		return this;
	}

	public static final LP
	O = new LP(0, 0),
	F = new LP(-1, -1),
	W = new LP(-2, -2),
	FW = new LP(-1, -2),
	WF = new LP(-2, -1),
	VLine = new LP(-1, 0, 1),
	HLine = new LP(0, -2, 1),
	B = new LP(-2, -2).setGravity(80);
}
