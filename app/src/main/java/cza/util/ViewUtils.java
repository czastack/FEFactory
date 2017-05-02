package cza.util;

import cza.MyFE.R;
import cza.app.MyApp;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ViewUtils {
	
	public static void setOnClickListenerIn(ViewGroup container, View.OnClickListener l){
		for (int i = 0, len = container.getChildCount(); i < len; i++){
			container.getChildAt(i).setOnClickListener(l);
		}
	}

	public static void registerClick(View container, View.OnClickListener l, int...ids){
		for (int id: ids){
			container.findViewById(id).setOnClickListener(l);
		}
	}

	public static void registerCheck(View container, CompoundButton.OnCheckedChangeListener l, int...ids){
		for (int id: ids){
			((CompoundButton)container.findViewById(id)).setOnCheckedChangeListener(l);
		}
	}
	
	public static void clearAutoFocus(View v) {
		View parent = (View) v.getParent();
		parent.setFocusable(true);
		parent.setFocusableInTouchMode(true);
	}

	public static void setCheckable(View v) {
		v.setClickable(true);
		v.setFocusable(true);
	}
	
	public static void chkRadioAt(RadioGroup parent, int position){
		((RadioButton)parent.getChildAt(position)).setChecked(true);
	}
	
	public static void display(View v){
		v.setVisibility(v.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}

	public static void hide(View v, boolean hidden){
		v.setVisibility(hidden ? View.GONE : View.VISIBLE);
	}

	public static void edit(EditText v, int id){
		switch (id){
			case R.id.btn_clear:
				v.getText().clear();
				break;
			case android.R.id.paste:
				v.setText(MyApp.mCB.getText());
				break;
			case android.R.id.copy:
				MyApp.mCB.copy(v.getText());
				break;
		}
	}
	
	public static void setOnDown(TextView v,  TextView.OnEditorActionListener l) {
		v.setSingleLine(true);
		v.setImeOptions(6);
		v.setOnEditorActionListener(l);
	}
	
	public static void setColorList(TextView v){
		Context context = v.getContext();
		int i[] = new int[]{android.R.attr.textColorSecondary};
		TypedArray a = context.getTheme().obtainStyledAttributes(i);
		int origin = a.getColor(0, Color.BLACK);
		int highlight = context.getResources().getColor(R.color.highlight);
		int[] colors = new int[] {highlight, origin};  
        int[][] states = new int[2][];  
        states[0] = new int[] {android.R.attr.state_selected};
		states[1] = new int[] {};
        v.setTextColor(new ColorStateList(states, colors));  
	}
	
	public static void replaceView(View origin, View current){
		ViewGroup parent = (ViewGroup)origin.getParent();
		if (parent == null)
			return;
		parent.removeView(current);
		int index = parent.indexOfChild(origin);
		parent.removeView(origin);
		parent.addView(current, index, origin.getLayoutParams());
	}
}

