package cza.app;

import cza.MyFE.R;
import cza.util.ViewUtils;
import android.content.Context;
import android.widget.EditText;

public class EditDialog extends Dialog {

	public EditText textarea;
	public static final int 
	MODE_INPUT = 0,
	MODE_SHOW = 1,
	MODE_EMPTY = -1;

	public EditDialog(Context c, int mode){
		super(c);
		if (mode == MODE_INPUT){
			setView(R.layout.multi_text_input);
			textarea = (EditText) findView(R.id.iet);
			setConfirm();
		} else if (mode == MODE_SHOW){
			setView(textarea = new EditText(c));
		}
	}

	public Dialog setCopy() {
		setButton(Dialog.BUTTON_NEGATIVE, "取消", this);
		setButton(Dialog.BUTTON_NEUTRAL, "复制", this);
		return this;
	}

	@Override
	public void onNeutral() {
		ViewUtils.edit(textarea, android.R.id.copy);
		close();
	}

	@Override
	public void setMessage(CharSequence text){
		textarea.setText(text);
	}

	public String getText() {
		return textarea.getText().toString();
	}
}
