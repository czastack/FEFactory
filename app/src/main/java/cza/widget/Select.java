package cza.widget;

import cza.MyFE.R;
import cza.app.ListDialog;
import android.content.Context;
import android.view.View;

public class Select extends SelectSty implements ListDialog.OnSubmitListener {

	public int mIndex;
	public ListDialog dialog;
	public BtnBar btnBar;
	protected View stateBar;
	protected SelectCallback mCallback;

	public Select(Context c, String title, SelectCallback callback){
		super(c, title);
		ListDialog d = new ListDialog(c);
		d.addHeader(R.layout.dialog_select);
		btnBar = (BtnBar) d.findView(R.id.btnBar);
		stateBar = d.findView(R.id.mulChkBar);
		d.setTitle(title);
		super.dialog = dialog = d;
		d.setOnSubmitListener(this);
		mCallback = callback;
		init();
	}

	protected void init(){
		stateBar.setVisibility(View.GONE);
		dialog.setItems(mCallback, null, ListDialog.MODE_SINGLE);
	}

	public void select(int i){
		setHint(mCallback.getTitle(mIndex = i));
		dialog.mListView.setSelection(i);
	}
	
	/**
	 * 手动选中
	 */
	public void manualSelect(int position){
		dialog.onCheck(position);
	}

	@Override
	public void onClick(View v) {
		mCallback.onShow();
		super.onClick(v);
	}
	
	public void onSubmit(ListDialog dialog, int[] checkedIndexs){
		select(checkedIndexs[0]);
		mCallback.onSubmit(false, checkedIndexs);
	}
}
