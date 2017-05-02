package cza.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyAdapter extends BaseAdapter {
	private Helper mHelper;

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mHelper.getCount();
	}

	@Override
	public View getView(int position, View item, ViewGroup parent) {
		return mHelper.getView(position, item);
	}

	public void setHelper(Helper helper){
		mHelper = helper;
	}

	public interface Helper{
		public int getCount();
		public View getView(int position, View item);
	}
}
