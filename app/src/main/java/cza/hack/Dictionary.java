package cza.hack;

import android.util.SparseIntArray;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Dictionary {
	public SparseIntArray mCodeMap;
	public SparseIntArray mTextMap;
	
	public void load(String path) throws Exception {
		load(new FileInputStream(path));
	}
	
	public boolean load(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(is));
		String line = reader.readLine();
		if (line == null)
			return false;
		while (line.isEmpty())
			line = reader.readLine();
		SparseIntArray codeMap;
		SparseIntArray textMap;
		int code;
		char text;
		if (mCodeMap == null) {
			codeMap = new SparseIntArray();
			textMap = new SparseIntArray();
			mCodeMap = codeMap;
			mTextMap = textMap;
		} else {
			codeMap = mCodeMap;
			textMap = mTextMap;
			codeMap.clear();
			textMap.clear();
		}
		int codeStart = 0;
		int textStart = 5;
		line = line.trim();
		if (line.charAt(1) == '=') {
			//文字在前
			codeStart = 2;
			textStart = 0;
		}
		do {
			if (line.isEmpty())
				continue;
			code = Coder.readBytes(line, codeStart, 2);
			text = line.charAt(textStart);
			codeMap.append(text, code);
			textMap.append(code, text);
		} while ((line = reader.readLine()) != null);
		reader.close();
		return true;
	}
	
	public char getText(int code){
		return (char)mTextMap.get(code);
	}
	
	/**
	 * 通过byte数组获取文字
	 */
	public String getText(byte[] byteArray){
		int length = byteArray.length / 2;
		char[] buffer = new char[length];
		int pointer;
		for (int i = 0; i < length; i++){
			pointer = i << 1;
			buffer[i] = getText(((byteArray[pointer] & 0xFF) << 8) | (byteArray[pointer + 1] & 0xFF));
		}
		return String.valueOf(buffer);
	}

	/**
	 * 通过int数组获取文字
	 */
	public String getText(List<Integer> codeArray){
		char[] buffer = new char[codeArray.size()];
		for (int i = 0; i < buffer.length; i++){
			buffer[i] = getText(codeArray.get(i));
		}
		return String.valueOf(buffer);
	}

	/**
	 * 通过byte[2]获取一个字符
	 */
	public char getChar(byte[] byteArray){
		return getText(((byteArray[0] & 0xFF) << 8) | (byteArray[1] & 0xFF));
	}

	/**
	 * 16进制代码获取文字
	 */
	public String getText(String codeString){
		codeString = codeString.replace(" ", "");
		int length = codeString.length() / 4;
		char[] buffer = new char[length];
		int i;
		int code;
		int p = 0;
		for (i = 0; i < length; i++){
			code = Integer.parseInt(codeString.substring(p, p += 4), 16);
			buffer[i] = getText(code);
		}
		return String.valueOf(buffer);
	}
	
	/**
	 * 文字获取16进制代码
	 */
	public int getCode(char text){
		return mCodeMap.get(text);
	}

	public void getCodes(String text, int[] array){
		int length = Math.min(array.length, text.length());
		for (int i = 0; i < length; i++){
			array[i] = getCode(text.charAt(i));
		}
	}

	public int[] getCodes(String text){
		int[] array  = new int[text.length()];
		getCodes(text, array);
		return array;
	}
	
	public String getCode(String text, boolean space){
		if (text == null || text.isEmpty())
			return "";
		final int LENGTH = 4;
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder(LENGTH);
		int length = text.length();
		for (int i = 0; i < length; i++){
			temp.delete(0, LENGTH);
			temp.append(Coder.toHEX(getCode(text.charAt(i))));
			Coder.ao(temp, 0, LENGTH);
			sb.append(temp);
			if (space)
				sb.append(' ');
		}
		if (space)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
