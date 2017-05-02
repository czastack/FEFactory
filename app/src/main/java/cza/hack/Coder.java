package cza.hack;

import cza.util.Calculator;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coder {
	public static final int
	FLAG_8BIT = 0x000000FF,
	FLAG_16BIT = 0x0000FFFF;
	public static final long
	FLAG_32BIT = 0xFFFFFFFFL;
	private static StringBuilder mBuilder = new StringBuilder();
	
	public static final Calculator calculator = Calculator.getInstance();
	/**
	 * 十六进制　转　十进制
	 * 比如："12" 转化为 18
	 */
	public static long hexToDec(String text) {
		if (text.isEmpty())
			return 0;
		long dec = 0;
		int i = 0;
		int length = text.length();
		char hex;
		//忽略前面的0
		while (i < length && (hex = text.charAt(i)) == '0')
			i++;
		while (i < length) {
			hex = text.charAt(i++);
			if (hex >= '0' && hex <= '9')
				dec += hex & 15;
			else if (hex >= 'A' && hex <= 'F')
				dec += hex - 55;
			else if (hex >= 'a' && hex <= 'f')
				dec += hex - 87;
			else 
				continue;
			dec <<= 4;
		}
		dec >>>= 4;
		return dec;
	}

	public static int reverseHalfWord(int value){
		return ((value >>> 8) & FLAG_8BIT) | ((value & FLAG_8BIT) << 8);
	}

	public static long reverseWord(long value){
		return reverseHalfWord((int)((value >>> 16) & FLAG_16BIT)) | ((long)reverseHalfWord((int)(value & FLAG_16BIT)) << 16);
	}

	public static long reverse(long value, int size){
		if (size == 0)
			return 0;
		switch (size){
			default:
			case 1:
				return value;
			case 2:
				return reverseHalfWord((int)value);
			case 4:
				return reverseWord(value);
		}
	}
	
	public static int readBytes(CharSequence text, int offset, int byteCount){
		if (text == null || text.length() == 0)
			return 0;
		int dec = 0;
		int i = offset;
		int length = Math.min(text.length(), byteCount << 1);
		char hex;
		while (i < length) {
			hex = text.charAt(i++);
			if (hex >= '0' && hex <= '9')
				dec += hex & 15;
			else if (hex >= 'A' && hex <= 'F')
				dec += hex - 55;
			else if (hex >= 'a' && hex <= 'f')
				dec += hex - 87;
			else 
				continue;
			dec <<= 4;
		}
		dec >>>= 4;
		return dec;
	}

	public static int readBytes(CharSequence text, int offset){
		if (text == null || text.length() == 0)
			return 0;
		int dec = 0;
		int i = offset;
		int length = text.length();
		char hex;
		while (text.charAt(i) == '0'){
			i++;
		}
		while (i < length) {
			hex = text.charAt(i++);
			if (hex >= '0' && hex <= '9')
				dec |= hex & 15;
			else if (hex >= 'A' && hex <= 'F')
				dec |= hex - 55;
			else if (hex >= 'a' && hex <= 'f')
				dec |= hex - 87;
			else 
				break;
			dec <<= 4;
		}
		dec >>>= 4;
		return dec;
	}

	public static String toHexString(long num, int byteCount){
		return ao(toHEX(num), byteCount << 1);
	}
	
	public static String toByteString(int num){
		return toHexString(num, 1);
	}

	public static String toHalfWordString(int num){
		return toHexString(num, 2);
	}

	public static String toWordString(long num){
		return toHexString(num, 4);
	}

	/**
	 * 验证16进制字符
	 */
	public static boolean isHex(char code) {
		return '0' <= code && code <= '9'
			|| 'A' <= code && code <= 'F'
			|| 'a' <= code && code <= 'f';
	}

	/**
	 * 字符串添加前缀0以达到指定长度
	 * @param src  字符串
	 * @param len 需要达到的长度
	 * @return 处理后的字符串
	 */
	public static String ao(String src, int len){
		mBuilder.replace(0, mBuilder.length(), src);
		ao(mBuilder, 0, len);
		return mBuilder.toString();
	}

	public static void ao(StringBuilder sb, int start, int len){
		int mLen = sb.length() - start;
		if (mLen < len) {
			char[] zero = new char[len - mLen];
			Arrays.fill(zero, '0');
			sb.insert(start, zero);
		}
	}

	public static String upper(String text){
		return text.toUpperCase(Locale.getDefault());
	}

	public static int parseHex(CharSequence text, int def){
		String str = text.toString();
		if (text == null || str.isEmpty())
			return def;
		return fromHex(str);
	}

	public static int fromHex(String s){
		if (s == null)
			return 0;
		else 
			return Integer.valueOf(s, 16);
	}

	public static long fromLongHex(String s){
		if (s == null)
			return 0;
		else 
			return Long.valueOf(s, 16);
	}

	public static String toHex(int n){
		return Integer.toHexString(n);
	}

	public static String toHEX(int n){
		return upper(toHex(n));
	}

	public static String toHEX(long n){
		return upper(toHex(n));
	}

	public static String toHex(long n){
		return Long.toHexString(n);
	}

	/**
	 * 替换进制
	 */
	public static String toBaseString(CharSequence text, int basein, int baseout){
		Pattern pattern = Pattern.compile("[0-9a-fA-F]+");
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			long dec = Long.parseLong(matcher.group(), basein);
			String out = Long.toString(dec, baseout);
			matcher.appendReplacement(sb, out);
		}
		matcher.appendTail(sb);
		String result = upper(sb.toString());
		return result;
	}

	public String offset(String in, String offset){
		if (offset == null)
			return in;
		String expression = toBaseString(in + offset, 16, 10);
		long result = calculator.compute(expression);
		return ao(toHex(result), in.length());
	}

	public static long offset(long in, String offset){
		if (offset == null)
			return in;
		String expression = in + toBaseString(offset, 16, 10);
		return Calculator.getInstance().compute(expression);
	}
}
