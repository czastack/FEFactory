package cza.MyFE;

import cza.element.Element;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class TextConverActivity extends MyActivity implements 
View.OnClickListener {

	/**
	 * 文字转换
	 */
	private CheckBox mSpaceView;
	private EditText mInputTextView;
	private EditText mInputCodeView;
	private EditText mHuffmanView;
	private FEReader mReader;

	@Override
    public void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		if (!ensureFile(MyApplication.SD))
			return;
		mReader = Element.mReader;
		if (mReader == null || mReader.mDict == null){
			finish();
			return;
		}
		setContentView(R.layout.text_conver);
		mInputTextView = (EditText)findView(R.id.inputTextView);
		mInputCodeView = (EditText)findView(R.id.inputCodeView);
		mHuffmanView = (EditText)findView(R.id.huffmanView);
		mSpaceView = (CheckBox)findView(R.id.spaceView);
		findView(R.id.btn_fromText).setOnClickListener(this);
		findView(R.id.btn_fromCode).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_fromText:
				fromText();
				break;
			case R.id.btn_fromCode:
				fromCode();
				break;
		}
	}
	
	private void toHuffman(String text){
		try {
			mHuffmanView.setText(mReader.toHuffmanText(text));
		} catch (Exception e) {
			toast(e.getMessage());
		}
	}

	private void fromText(){
		String input = mInputTextView.getText().toString();
		mInputCodeView.setText(mReader.mDict.getCode(input, mSpaceView.isChecked()));
		toHuffman(input);
	}
	
	private void fromCode(){
		String input = mInputCodeView.getText().toString();
		String text = mReader.mDict.getText(input);
		mInputTextView.setText(text);
		toHuffman(text);
	}
}
