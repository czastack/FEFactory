package cza.MyFE;

import cza.app.Dialog;
import cza.hack.Coder;
import cza.hack.HackLog;
import cza.widget.MyAdapter;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class SaveChangeDialog extends Dialog implements MyAdapter.Helper {

	private List<HackLog> mList;
	private boolean mCancelable;
	
	public SaveChangeDialog(Context context, List<HackLog> list, boolean cancelable){
		super(context);
		mList = list;
		setView(R.layout.dialog_list);
		mCancelable = cancelable;
		setCanceledOnTouchOutside(cancelable);
		ListView listView = (ListView)findView(R.id.list);
		MyAdapter adapter = new MyAdapter();
		adapter.setHelper(this);
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
        }
		return super.onKeyDown(keyCode, event);
	}
	
	//填充数据
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public View getView(int position, View item) {
		Holder holder;
		if (item == null) {
			holder = new Holder();
			item = inflateView(R.layout.element_save_change_item);
			holder.findView(item);
			item.setTag(holder);
		} else {
			holder = (Holder)item.getTag();
		}
		holder.set(mList.get(position));
		return item;
	}

	private class Holder {
		private TextView titleView, addrView, valueView;

		private void findView(View item) {
			titleView = (TextView) item.findViewById(R.id.titleView);
			addrView = (TextView) item.findViewById(R.id.addrView);
			valueView = (TextView) item.findViewById(R.id.valueView);
		}

		public void set(HackLog log) {
			titleView.setText(log.title);
			addrView.setText(Coder.toWordString(log.addr));
			valueView.setText(Coder.toHexString(log.value, log.size));
		}
	}
}
