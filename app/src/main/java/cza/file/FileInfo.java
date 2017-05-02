package cza.file;

import cza.app.MyApp;
import cza.util.Checkable;
import java.io.File;
import java.io.Serializable;

public class FileInfo extends Checkable implements Serializable {
	public String name, size, type = "";
	public boolean isDir;
	public int icon;

	public FileInfo(){}

	public FileInfo(File file){
		name = file.getName();
		if (file.isDirectory()) {
			isDir = true;
			icon = MyApp.ICON_DIR;
		} else {
			size = FileUtils.size(file);
			type = FileUtils.getType(name);
			icon = MyApp.getIcon(type);
		}
	}

	boolean after(FileInfo f) {
		return MyApp.COMPARATOR.compare(name, f.name) > 0;
	}
}
