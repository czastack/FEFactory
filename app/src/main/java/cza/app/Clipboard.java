package cza.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Clipboard {
	public String hint;
	private ClipboardManager manager;
	private ClipData cd;

	public Clipboard (Context c){
		manager = (ClipboardManager)c.getSystemService(Context.CLIPBOARD_SERVICE);
	}

	public void copy(CharSequence text) {
		cd = ClipData.newPlainText("label", text);
		manager.setPrimaryClip(cd);
	}

	public String getText() {
		if (manager.hasPrimaryClip()) {
			cd = manager.getPrimaryClip();
			ClipData.Item item = cd.getItemAt(0);
			return item.getText().toString();
		}
		return "";
	}
}
