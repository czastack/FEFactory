package cza.app;

import cza.MyFE.R;
import cza.widget.MyAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;

public class ListDialog extends Dialog implements 
		AdapterView.OnItemClickListener, 
		MyAdapter.Helper {
	
	public ListView mListView;
	public final static int MODE_ITEM = 0;
	public final static int MODE_SINGLE = 1;
	public final static int MODE_DOUBLE = 2; //要双击的单选
	public final static int MODE_MULTI = 3;
	private int mLayoutRes;
	private BaseAdapter mAdapter;
	private OnClickListener mListener;
	private OnSubmitListener mOnSubmitListener;
	private boolean checkable;
	private boolean multiple;
	//简易模式
	private boolean simple;
	private boolean advanced; //启用内部Helper
	private boolean singleClose; //单选模式下选取后关闭
	CharSequence[] mItems;
	List<String> mList;
	public int mCheckedIndex = -1;
	//多选
	public boolean[] mCheckedList;
	public int checkedCount;

	public ListDialog(Context c){
		super(c);
		setView(R.layout.dialog_list);
	}

	@Override
	public void setView(int resId) {
		super.setView(resId);
		mListView = (ListView) findView(R.id.list);
	}
	
	public void setOnSubmitListener(OnSubmitListener listener){
		mOnSubmitListener = listener;
	}
	
	//填充数据
	@Override
	public int getCount() {
		return simple ? 
			simpleGetCount(): 
			helperGetCount();
	}

	@Override
	public View getView(int position, View item) {
		return simple ? 
			simpleGetView(position, item): 
			helperGetView(position, item);
	}

	//点击事件
	@Override
	public void onItemClick(AdapterView<?> adpt, View item, int position, long id) {
		onCheck(position);
	}
	
	public boolean isChecked(int position){
		return multiple ? mCheckedList[position] : (mCheckedIndex == position);
	}

	public void onCheck(int position) {
		if (checkable){
			//针对过滤
			if (advanced)
				position = ((Helper)mHelper).getRealPosition(position);
			if (multiple){
				//多选
				if (simple || (advanced && ((Helper)mHelper).isMultiCheckable(position))){
					if (mCheckedList[position] = !mCheckedList[position])
						checkedCount++;
					else 
						checkedCount--;
				}
			} else {
				//单选
				if (singleClose) {
					mCheckedIndex = position;
					submit();
				} else if (mCheckedIndex == position)
					submit();
				else 
					mCheckedIndex = position;
			}
			refresh();
		}
		if (mListener != null)
			mListener.onClick(this, position);
	}

	@Override
	public boolean onOk() {
		if (checkable){
			submit();
			return true;
		} else {
			return super.onOk();
		}
	}
	
	public void checkAll(boolean checked){
		Arrays.fill(mCheckedList, checked);
		checkedCount = checked ? mCheckedList.length : 0;
		refresh();
	}
	
	public void countChecked(){
		int count = 0;
		for (int i = 0; i < mCheckedList.length; i++){
			if (mCheckedList[i])
				count++;
		}
		checkedCount = count;
	}
	
	public int[] getCheckedIndexs(){
		int[] indexs;
		if (!multiple){
			indexs = new int[]{mCheckedIndex};
		} else {
			indexs = new int[checkedCount];
			if (checkedCount > 0){
				int key = 0;
				boolean[] arr = mCheckedList;
				for (int index = 0; index < arr.length; index++){
					if (arr[index])
						indexs[key++] = index;
				}
			}
		}
		return indexs;
	}
	
	public void submit(){
		if (mOnSubmitListener != null) {
			mOnSubmitListener.onSubmit(this, getCheckedIndexs());
		}
		close();
	}
	
	public void refresh(){
		mAdapter.notifyDataSetChanged();
	}
	
	//多选
	public void ensureCapacity(int size){
		mCheckedList = new boolean[size];
	}

	private void register(){
		mListView.setAdapter(mAdapter);
		if (checkable || mListener != null){
			mListView.setOnItemClickListener(this);
		}
	}
	
	private void initParams(int mode){
		switch (mode){
			case MODE_ITEM:
				checkable = false;
				break;
			case MODE_SINGLE:
				multiple = false;
				checkable = true;
				singleClose = true;
				setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel), this);
				break;
			case MODE_DOUBLE:
				multiple = false;
				checkable = true;
				singleClose = false;
				setConfirm();
				break;
			case MODE_MULTI:
				multiple = true;
				checkable = true;
				setConfirm();
				break;
		}
	}

	//简易模式
	private void simpleInit(CharSequence[] items, final OnClickListener listener){
		simple = true;
		mItems = items;
		mListener = listener;
		MyAdapter adpt = new MyAdapter();
		adpt.setHelper(this);
		mAdapter = adpt;
		register();
	}
	
	public void setItems(CharSequence[] items, final OnClickListener listener){
		initParams(MODE_ITEM);
		mLayoutRes = android.R.layout.simple_list_item_1;
		simpleInit(items, listener);
	}

	public void setItems(CharSequence[] items, int checkedIndex, OnClickListener listener, boolean singleClose){
		initParams(singleClose ? MODE_SINGLE : MODE_DOUBLE);
		mLayoutRes = R.layout.simple_list_item_single_choice;
		mCheckedIndex = checkedIndex;
		simpleInit(items, listener);
	}

	public void setItems(CharSequence[] items, boolean[] checkedList, OnClickListener listener){
		initParams(MODE_MULTI);
		mLayoutRes = R.layout.simple_list_item_multiple_choice;
		if (checkedList != null){
			mCheckedList = checkedList;
			countChecked();
		} else {
			ensureCapacity(items.length);
		}
		simpleInit(items, listener);
	}
	
	public void setList(List<String> list){
		mList = list;
	}
	
	private CharSequence simpleGetItem(int position){
		return mItems != null ? mItems[position] : mList.get(position);
	}
	
	public int simpleGetCount() {
		return mItems != null ? mItems.length : mList.size();
	}

	public View simpleGetView(int position, View item) {
		if (item == null) 
			item = inflateView(mLayoutRes);
		((TextView) item).setText(simpleGetItem(position));
		if (checkable)
			((Checkable)item).setChecked(isChecked(position));
		return item;
	}
	
	//代理 开始
	private MyAdapter.Helper mHelper;

	//代理适配器
	public void setItems(MyAdapter.Helper helper, final OnClickListener listener, int mode){
		simple = false;
		initParams(mode);
		switch (mode){
			case MODE_ITEM:
				mLayoutRes = 0;
				break;
			case MODE_SINGLE:
			case MODE_DOUBLE:
				mLayoutRes = R.layout.list_singlechoice;
				break;
			case MODE_MULTI:
				mLayoutRes = R.layout.list_multichoice;
				ensureCapacity(helper.getCount());
				break;
		}
		MyAdapter adpt = new MyAdapter();
		adpt.setHelper(this);
		mAdapter = adpt;
		mHelper = helper;
		advanced = helper instanceof Helper;
		mListener = listener;
		register();
	}
	
	public int helperGetCount() {
		return mHelper.getCount();
	}

	public View helperGetView(int position, View item) {
		//针对过滤
		if (advanced)
			position = ((Helper)mHelper).getRealPosition(position);
		if (!checkable)
			return mHelper.getView(position, item);
		else if (advanced && ((Helper)mHelper).unCheckable(position))
			return mHelper.getView(position, null);
		View btn, view;
		if (item == null || !(item.getTag() instanceof View)) {
			ViewGroup container = (ViewGroup) inflateView(mLayoutRes);
			ViewGroup content = (ViewGroup) container.findViewById(R.id.content);
			view = mHelper.getView(position, null);
			content.addView(view, -1, -2);
			btn = container.findViewById(R.id.btn);
			item = container;
			item.setTag(btn);
			btn.setTag(view);
		} else {
			btn = (View) item.getTag();
			view = (View) btn.getTag();
			mHelper.getView(position, view);
		}
		((Checkable)btn).setChecked(isChecked(position));
		return item;
	}
	//代理 结束
	
	public interface Helper extends MyAdapter.Helper {
		//是否显示选择框
		public boolean unCheckable(int position);
		//多选检查边界
		public boolean isMultiCheckable(int position);
		//针对过滤
		public int getRealPosition(int position);
	}
	
	public interface OnSubmitListener {
		public void onSubmit(ListDialog dialog, int[] checkedIndexs);
	}
}
