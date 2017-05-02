package cza.util;

import java.lang.reflect.Array;

public class ArrayUtils {
	public static String join(String[] strAry){
        return join(strAry, ",");
    }

	public static String join(String[] strAry, CharSequence sep){
        StringBuilder sb = new StringBuilder(strAry[0]);
		for(int i = 1; i < strAry.length; i++){
			sb.append(sep + strAry[i]);
        }
        return sb.toString();
    }

	public static void reverse(String[] arr){
		int len = arr.length;
		String temp;
		for (int i = 0; i < len / 2; i++){
			temp = arr[i];
			int key = len - 1 - i;
			arr[i] = arr[key];
			arr[key] = temp;
		}
	}
	
	public static Object combine(Object[]...list) {
		int i;
		int length = 0;
		int start = 0;
		for (i = 0; i < list.length; i++){
			length += list[i].length;
		}
		Object newArray = Array.newInstance(list[0].getClass().getComponentType(), length);
		for (i = 0; i < list.length; i++){
			int mLength = list[i].length;
			System.arraycopy(list[i], 0, newArray, start, mLength);
			start += mLength;
		}
		return newArray;
	}
	
	public static int indexOf(int[] array, int e){
		int index = -1;
		int i;
		for (i = 0; i < array.length; i++){
			if (array[i] == e){
				index = i;
				break;
			}
		}
		return index;
	}
}
