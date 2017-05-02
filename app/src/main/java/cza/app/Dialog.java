package cza.app;

import cza.MyFE.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Dialog extends AlertDialog implements 
		DialogInterface.OnClickListener,
		DialogInterface.OnCancelListener {

	public LayoutInflater mInflater;
	public View mLayout;
	private OnOKListener mOnOKListener;

	public Dialog(Context c){
		super(c);
		mInflater = LayoutInflater.from(c);
		setOnCancelListener(this);
	}
	
	public void setMessage(int resId){
		setMessage(getContext().getText(resId));
	}

	public void showMsg(CharSequence msg){
		setMessage(msg);
		show();
	}

	public void showText(CharSequence title, CharSequence msg){
		setTitle(title);
		showMsg(msg);
	}

	public View inflateView(int layoutResID){
		return mInflater.inflate(layoutResID, null);
	}
	
	public void setView(int resId){
		View view = inflateView(resId);
		setView(mLayout = view);
	}
	
	public View findView(int id){
		return mLayout.findViewById(id);
	}

	public View addHeader(int layoutId){
		View header = null;
		View layout = findView(R.id.layout);
		if (layout instanceof ViewGroup){
			header = inflateView(layoutId);
			((ViewGroup) layout).addView(header, 0);
		}
		return header;
	}
	
	@Override
	public void show() {
		onShow();
		super.show();
	}

	public void onShow(){}

	public void setBack() {
		setButton(Dialog.BUTTON_NEGATIVE, "返回", this);
	}

	public void setConfirm(DialogInterface.OnClickListener l) {
		setButton(Dialog.BUTTON_POSITIVE, "确定", l);
		setButton(Dialog.BUTTON_NEGATIVE, "取消", l);
	}

	public void setConfirm() {
		setConfirm(this);
	}

	@Override
	public void dismiss() {}

	public void onDismiss(){}

	public void close(){
		onDismiss();
		super.dismiss();
	}

	@Override
	public void onClick(DialogInterface d, int which) {
		switch (which){
			case BUTTON_POSITIVE:
				if (onOk())
					close();
				break;
			case BUTTON_NEGATIVE:
				onCancel();
				break;
			case BUTTON_NEUTRAL:
				onNeutral();
				break;
		}
	}

	public boolean onOk() {
		if (mOnOKListener != null) {
			return mOnOKListener.onOK(this);
		}
		return true;
	}

	public void onCancel(){
		close();
	}

	@Override
	public void onCancel(DialogInterface d) {
		onCancel();
	}

	public void onNeutral(){}

	public void setOnOKListener(OnOKListener l) {
		mOnOKListener = l;
	}

	public interface OnOKListener {
		public boolean onOK(Dialog dialog);
	}
	
}
