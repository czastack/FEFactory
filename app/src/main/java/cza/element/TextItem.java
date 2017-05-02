package cza.element;

import cza.MyFE.R;
import cza.hack.Coder;
import cza.util.Pull;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class TextItem extends BaseChild {
	private TextView mTitleView, mIndexView, mPreview, mPointerAddrView;
	private BytesEditText mPointerView;
	
	public TextItem(Context context, Pull pull){
		super(pull);
		View view = View.inflate(context, R.layout.element_text_item, null);
		mView = view;
		mTitleView = (TextView)view.findViewById(R.id.textTitleView);
		mIndexView = (TextView)view.findViewById(R.id.textIndexView);
		mPreview = (TextView)view.findViewById(R.id.textPreview);
		mPointerAddrView = (TextView)view.findViewById(R.id.textPointerAddrView);
		mPointerView = (BytesEditText)view.findViewById(R.id.textPointerView);
		mTitleView.setText(mTitle);
		mTitleView.setOnLongClickListener(this);
		mIndexView.setFilters(InputItem.FILTERS[mSize]);
	}

	@Override
	public View getView() {
		return mView;
	}

	@Override
	public void setValue(int value) {
		int pointerAddr = mReader.mRom.getPointer(value);
		int pointer = (int)mReader.readWord(pointerAddr);
		mIndexView.setText(Coder.toHalfWordString(value));
		mPreview.setText(mReader.readText(pointer));
		mPointerAddrView.setText(Coder.toWordString(pointerAddr));
		mPointerView.setValue(pointer, 4);
	}

	@Override
	public int getValue() {
		return Coder.readBytes(mIndexView.getText(), 0, mSize);
	}
}
