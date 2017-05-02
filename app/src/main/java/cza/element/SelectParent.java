package cza.element;
import cza.MyFE.R;
import cza.hack.Coder;
import cza.util.Pull;
import cza.widget.Select;
import cza.widget.SelectCallback;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.regex.Pattern;

public class SelectParent extends BaseParent {

	private String mTitle, mEntryName;
	private int mBaseAddr;
	private int mIncrement;
	private int mEntriesSkip;
	private int mValueSize;
	private int mDataAddr;
	private Data mData;
	private Data mTempData;
	private Select mSelect;
	
	public SelectParent(Context context, Pull pull) {
		super(context, pull);
	}

	@Override
	public void onInitChildren(Pull pull) {
		mBaseAddr = Coder.fromHex(pull.getValue(ATTR_ADDR));
		mIncrement = Coder.fromHex(pull.getValue(ATTR_INCREMENT));
		mEntriesSkip = pull.getInt(ATTR_ENTRIES_SKIP);
		mValueSize = pull.getInt(ATTR_SIZE, 1);
		mTitle = pull.getValue(ATTR_ITEM_TITLE);
		mEntryName = pull.getValue(ATTR_ENTRIES);
	}

	@Override
	public View getView() {
		if (mView == null){
			ViewGroup layout = (ViewGroup)View.inflate(mContext, R.layout.element_parent, null);
			mView = layout;
			loadChildren();
			for (BaseChild child : mChildren){
				layout.addView(child.getView());
			}
			//mEntryName在加载子控件时才读取
			Helper helper = new Helper(mContext, mTitle);
			String[] tempItems, items;
			tempItems = mReader.mRom.readEntries(mEntryName);
			if (mEntriesSkip > 0) {
				int length = tempItems.length - mEntriesSkip;
				items = new String[length];
				System.arraycopy(tempItems, mEntriesSkip, items, 0, length);
			} else {
				items = tempItems;
			}
			helper.readEntries(items);
			layout.addView(mSelect, 0);
			mSelect.manualSelect(0);
		}
		return mView;
	}

	public void read() {
		if (mData == null)
			return;
		//地址
		final int addr = mData.value * mIncrement + mBaseAddr;
		int offset = 0;
		mDataAddr = addr;
		for (BaseChild child : mChildren){
			if (child.getOffset() != 0)
				offset = child.getOffset();
			child.read(addr + offset);
			offset += child.getSize();
		}
	}
	
	/**
	 * checkChanged() -> 
	 * true : FEReader.mCallback.onSaveChange() -> FEReader.addLog(); Element.read();
	 * false: read();
	 */
	public void onDataChanged(Data data){
		if (mData == null){
			mData = data;
			read();
		} else if (mData != data){
			if (mReader.requestCheckInnerChange()) {
				mTempData = data;
			} else {
				mData = data;
				read();
			}
		}
	}
	
	public void onCheckChangeCallback(boolean cancel){
		if (cancel) {
			mSelect.manualSelect(mData.realIndex);
		} else {
			if (mTempData != null) {
				//页内切换
				mData = mTempData;
				mTempData = null;
			}
			read();
		}
			
	}
	
	private class Data {
		public String name;
		public int value;
		public int realIndex; //过滤后在该位置上显示的Data真正的序号
	}
	
	private class Helper extends SelectCallback implements 
	SearchView.OnQueryTextListener,
	SearchView.OnCloseListener {

		private static final int MIN_SEARCH_LENGTH = 10;
		private LayoutInflater mInflater;
		private Data[] mDatas;
		private int mEnableCount;
		private boolean isSearching;
		private SearchView mSearchView;

		public Helper(Context context, String label) {
			mInflater = LayoutInflater.from(context);
			Select select = new Select(context, label, this);
			mSelect = select;
			select.setOrientation(0);
			select.dialog.ensureCapacity(getCount());
		}

		/**
		 * 读入数据
		 */
		public void readEntries(String[] items) {
			int length = items.length;
			Data[] datas = new Data[length];
			mDatas = datas;
			mEnableCount = length;
			String item;
			boolean hasValue = Coder.isHex(items[0].charAt(0));
			int size = mValueSize;
			for (int i = 0; i < length; i++) {
				item = items[i];
				Data data = new Data();
				data.realIndex = i;
				if (hasValue)
					data.value = Coder.readBytes(item, 0, size);
				else 
					data.value = i;
				data.name = item;
				datas[i] = data;
			}
			initSearch();
		}

		/**
		 * 搜索
		 */
		public void initSearch() {
			mSearchView = (SearchView)mSelect.dialog.findView(R.id.searchView);
			if (mDatas.length > MIN_SEARCH_LENGTH) {
				mSearchView.setOnQueryTextListener(this);
				mSearchView.setOnCloseListener(this);
			} else {
				mSearchView.setVisibility(View.GONE);
			}
		}

		public boolean onClose() {
			isSearching = false;
			Data[] datas = mDatas;
			for (int i = 0; i < datas.length; i++)
				datas[i].realIndex = i;
			mEnableCount = mDatas.length;
			mSelect.dialog.refresh();
			return false;
		}

		public boolean onQueryTextSubmit(String text) {
			isSearching = true;
			Pattern regex = Pattern.compile(text);
			Data[] datas = mDatas;
			int index = 0;
			int count = 0;
			for (int i = index; i < datas.length; i++) {
				Data data = datas[i];
				if (regex.matcher(data.name).find()) {
					datas[index++].realIndex = i;
					count++;
				}
			}
			mEnableCount = count;
			mSelect.dialog.refresh();
			return true;
		}

		@Override
		public boolean onQueryTextChange(String text) {
			return false;
		}

		public int getRealPosition(int position) {
			return mDatas[position].realIndex;
		}

		@Override
		public boolean unCheckable(int position) {
			return false;
		}

		@Override
		public boolean isMultiCheckable(int position) {
			return false;
		}

		/**
		 * 单选选中某项
		 */
		@Override
		public String getTitle(int position) {
			return mDatas[position].name;
		}

		/**
		 * 单选中某项或多选按下确定
		 */
		@Override
		public void onSubmit(boolean multiple, int[] checkedIndexs) {
			onDataChanged(mDatas[checkedIndexs[0]]);
		}

		protected View inflateView(int layoutResID) {
			return mInflater.inflate(layoutResID, null);
		}

		public int getCount() {
			return mEnableCount;
		}

		public View getView(int position, View item) {
			Holder holder;
			if (item == null) {
				holder = new Holder();
				item = inflateView(R.layout.listitem_text);
				holder.findView(item);
				item.setTag(holder);
			} else {
				holder = (Holder) item.getTag();
			}
			holder.set(mDatas[position]);
			return item;
		}


		private class Holder {
			private TextView name;

			public void findView(View item) {
				name = (TextView) item.findViewById(R.id.title);
			}

			public void set(Data data) {
				name.setText(data.name);
			}
		}
	}
}
