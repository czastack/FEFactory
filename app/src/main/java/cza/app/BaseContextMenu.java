package cza.app;

import cza.MyFE.R;
import cza.widget.StringArrayAdapter;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class BaseContextMenu extends Dialog implements AdapterView.OnItemClickListener {

	public ListView listView;
	private String[] texts;
	private BaseAdapter mAdpt;
	private Callback mCallback;

	public BaseContextMenu(Context c){
		super(c);
		setView(R.layout.dialog_list);
		listView = (ListView) findView(R.id.list);
		listView.setOnItemClickListener(this);
	}

	public void setList(String...list){
		mAdpt = new StringArrayAdapter(getContext(), texts = list, StringArrayAdapter.TYPE_LIST);
		listView.setAdapter(mAdpt);
	}

	public void setList(int resId){
		setList(listView.getResources().getStringArray(resId));
	}

	public String getText(int i){
		return texts[i];
	}

	public boolean onLongClick() {
		if ((mCallback != null) && mCallback.onShowMenu(this)) {
			show();
			return true;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int index, long id) {
		if (this.mCallback != null) {
			this.mCallback.onItemClick(this, index);
			close();
		}
	}
	
	public void setCallback(Callback callback) {
		mCallback = callback;
	}
	
	public interface Callback {
		public void onItemClick(BaseContextMenu dialog, int position);
		public boolean onShowMenu(BaseContextMenu dialog);
	}
}






