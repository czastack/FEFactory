package cza.MyFE;

import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Rom {
	public static final String 
	TAG = "item",
	TITLE = "title",
	NAME = "name",
	DIR = "dir",
	POINTER_END = "pointerEnd",
	FILE_DICT = "dict.txt",
	FILE_ELEMENT_LIST = "elementList.xml",
	FILE_LOGO = "logo.png";
	public static final int 
	FONT_POINTER = 0x06E0,
	POINTER_START_POINTER = 0x06DC;
	
	public String title, name, dir;
	public int pointerStart, pointerEnd, fontStart;
	public AssetManager mAsset;

	/**
	 * 打开流
	 */
	public InputStream open(String name) throws Exception{
		return mAsset.open(dir + name);
	}
	
	public int getPointer(int index){
		return pointerStart + (index << 2);
	}
	
	public String[] readEntries(String path) {
		String[] items = null;
		try {
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(open(path), "UTF-8"));
			String str = reader.readLine();
			int length = Integer.parseInt(str);
			items = new String[length];
			int i = 0;
			while (i < length && (str = reader.readLine()) != null) {
				if (str.isEmpty())
					continue;
				items[i++] = str;
			}
			reader.close();
		} catch (Exception e) {}
		return items;
	}
}
