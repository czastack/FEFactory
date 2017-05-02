package cza.MyFE;

import cza.app.MyApp;
import java.io.File;

public class MyApplication extends MyApp {
	
	public static String PATH_BOOK_MARK, MY_DIR, TEXT_DIR;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	protected void initFile(){
		super.initFile();
		if (hasSD){
			File myDir = new File(SD, "MyFE");
			File textDir = new File(myDir, "text");
			File bookmark = new File(myDir, "bookmark.xml");
			myDir.mkdir();
			textDir.mkdir();
			MY_DIR = myDir.getPath();
			TEXT_DIR = textDir.getPath();
			PATH_BOOK_MARK = bookmark.getPath();
		}
	}
}
