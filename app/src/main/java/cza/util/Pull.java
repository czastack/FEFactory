package cza.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class Pull {
	public XmlPullParser parser;

	public void start(InputStream is) throws Exception {
		parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(is, "UTF-8");
	}

	public void start(File in) throws Exception {
		start(new FileInputStream(in));
	}

	public String getValue(String name){
		return parser.getAttributeValue("", name);
	}

	public boolean getBoolean(String name){
		return "true".equals(getValue(name));
	}

	public int getInt(String name){
		return getInt(name, 0);
	}

	public int getInt(String name, int def){
		String value = getValue(name);
		if (value == null || value.isEmpty())
			return def;
		return Integer.parseInt(value);
	}
}
