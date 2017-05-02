package cza.app;

import cza.file.FileUtils;
import android.content.Context;
import java.io.File;

public class FileDeleteConfirm extends Dialog {
	private File mFile;

	public FileDeleteConfirm(Context c) {
		super(c);
		setTitle("确认删除");
		setConfirm();
	}
	
	public void setFile(File file){
		mFile = file;
	}
	
	public void setHint(){
		setHint(mFile.getName(), FileUtils.size(mFile));
	}
	
	public void setHint(String name, String size){
		setMessage(name + "\t" + size);
	}
	
	@Override
	public boolean onOk() {
		mFile.delete();
		return super.onOk();
	}
}
