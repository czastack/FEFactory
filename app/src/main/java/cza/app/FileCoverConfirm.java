package cza.app;

import cza.file.FileUtils;
import android.content.Context;
import java.io.File;

public class FileCoverConfirm extends Dialog {
	private File from, to;
	public FileCoverConfirm(Context c) {
		super(c);
		setTitle("确认覆盖");
		setConfirm();
		setMessage("此操作会覆盖原有文件，确定继续？");
	}
	
	public Dialog setFile(File f, File t){
		from = f;
		to = t;
		return this;
	}
	
	@Override
	public boolean onOk() {
		FileUtils.copy(from, to);
		return true;
	}
}
