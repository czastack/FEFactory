package cza.element;

import cza.hack.Coder;
import cza.hack.HexFilter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class BytesEditText extends EditText implements 
View.OnTouchListener  {
	private static Bitmap btnReverseBp;
	private int btnReverseLeft;
	public long mValue;
	public int mSize;
	public boolean mIsReversed;

	public BytesEditText(Context c) {
		this(c, null);
	}

	public BytesEditText(Context c, AttributeSet attr) {
		super(c, attr);
		if (btnReverseBp == null){
			//初始化按钮资源
			Resources res = getResources();
			btnReverseBp = BitmapFactory.decodeResource(res, android.R.drawable.ic_menu_revert);
		}
		setInputType(2);
		InputFilter[] filters = {new HexFilter(8)};
		setFilters(filters);
		setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		btnReverseLeft = width - btnReverseBp.getWidth();
		canvas.drawBitmap(btnReverseBp, btnReverseLeft, (height - btnReverseBp.getHeight()) / 2, null);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		if (action == MotionEvent.ACTION_DOWN){
			float eX = event.getX();
			if (eX > btnReverseLeft){
				reverse();
				return true;
			}
		}
		return false;
	}

	public void setValue(long value, int byteCount){
		mValue = value & Coder.FLAG_32BIT;
		mSize = byteCount;
		refreshValue();
	}

	public long getValue(){
		long value = Coder.readBytes(getText(), 0, mSize);
		if (mIsReversed)
			value = Coder.reverse(value, mSize);
		return value;
	}

	public void refreshValue(){
		long value = mValue;
		if (mIsReversed){
			value = Coder.reverse(value, mSize);
		}
		setText(Coder.toHexString(value, mSize));
	}

	public void reverse(){
		mIsReversed = !mIsReversed;
		refreshValue();
	}
}
