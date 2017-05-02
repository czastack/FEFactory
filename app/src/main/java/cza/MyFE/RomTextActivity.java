package cza.MyFE;

import cza.element.Element;
import cza.hack.Coder;
import cza.widget.MyAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class RomTextActivity extends MyActivity implements 
	MyAdapter.Helper {
	
	protected FEReader mReader;
	protected ListView mListView;
	protected MyAdapter mAdapter;
	protected Data[] mDatas;

	@Override
    public void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		mReader = Element.mReader;
		mAdapter = new MyAdapter();
		mAdapter.setHelper(this);
	}
	
	protected boolean checkEnvironment(){
		if (!ensureFile(MyApplication.SD) || mReader == null || mReader.mDict == null){
			toast(R.string.loadRomFirst);
			finish();
			return false;
		}
		return true;
	}

	protected void refresh(){
		mAdapter.notifyDataSetChanged();
	}
	
	//填充数据
	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.length;
	}

	@Override
	public View getView(int position, View item) {
		Holder holder;
		if (item == null) {
			holder = new Holder();
			item = inflateView(R.layout.text_table_item);
			holder.findView(item);
			item.setTag(holder);
		} else {
			holder = (Holder)item.getTag();
		}
		holder.set(mDatas[position]);
		return item;
	}

	private class Holder {
		private TextView indexView, pointerView, textView;

		private void findView(View item){
			indexView = (TextView) item.findViewById(R.id.indexView);
			pointerView = (TextView) item.findViewById(R.id.pointerView);
			textView = (TextView) item.findViewById(R.id.textView);
		}

		public void set(Data data){
			indexView.setText(Coder.toHalfWordString(data.index));
			pointerView.setText(Coder.toWordString(data.addr));
			if (data.text == null) {
				int addr = (int)Element.mReader.readWord(data.addr);
				data.text = Element.mReader.readText(addr);
			}
			textView.setText(data.text);
		}
	}

	protected class Data {
		public int index, addr;
		public String text;
	}
}
