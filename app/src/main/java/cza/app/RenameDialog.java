package cza.app;

import cza.MyFE.R;
import cza.util.ViewUtils;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

public class RenameDialog extends EditDialog implements TextView.OnEditorActionListener {
	public static final int
	ERROR_EMPTY = R.string.emptyFilename,
	ERROR_EXIST = R.string.fileExists;
	private OnSummitListener mOnSummitListener;
	
	public RenameDialog(Context c){
		super(c, MODE_SHOW);
		setTitle("重命名");
		init();
	}
	
	public RenameDialog(Context c, int layoutId){
		super(c, MODE_EMPTY);
		setView(layoutId);
		textarea = (EditText) findView(R.id.iet);
		init();
	}
	
	private void init(){
		ViewUtils.setOnDown(textarea, this);
		setConfirm();
	}

	public boolean error(int code){
		MyApp.toast(getContext(), ERROR_EMPTY);
		return false;
	}
	
	@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (onOk()) close();
		return true;
	}

	@Override 
	public boolean onOk() {
		return mOnSummitListener.onSummit(this, textarea.getText().toString());
	}

	public void pre(CharSequence text) {
		setMessage(text);
		textarea.selectAll();
	}

	public void setOnSummitListener(OnSummitListener l) {
		mOnSummitListener = l;
	}

	public static interface OnSummitListener {
		public boolean onSummit(RenameDialog d, String text);
	}
}
