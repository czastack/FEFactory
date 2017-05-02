package cza.element;

import cza.util.Pull;
import android.content.Context;
import android.view.ViewGroup;

public abstract class BaseParent extends Element {
	protected Context mContext;
	protected ViewGroup mView;
	protected String mChildrenFile;
	protected BaseChild[] mChildren;

	public BaseParent(Context context, Pull pull){
		mContext = context;
		mChildrenFile = pull.getValue(ATTR_CHILDREN_LIST);
	}
	
	public void loadChildren(){
		if (mChildren != null)
			return;
		try {
			mChildren = loadChildren(mChildrenFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载子控件
	 */
	protected BaseChild[] loadChildren(String filename) throws Exception{
		BaseChild[] children = null;
		Pull pull = new Pull();
		pull.start(mReader.mRom.open(filename));
		int type;
		int index = 0;
		int count = 0;
		String tag;
		BaseChild child;
		Context context = mContext;
		while ((type = pull.parser.next()) != 1) {
			if (type != 2)
				continue;
			child = null;
			tag = pull.parser.getName();
			if (TAG_CHILDREN_TEXT_ITEM.equals(tag)) {
				child = new TextItem(context, pull);
			} else if (TAG_CHILDREN_GROUP.equals(tag)) {
				child = new ChildGroup(context, pull);
			} else if (TAG_CHILDREN_INPUT_ITEM.equals(tag)) {
				child = new InputItem(context, pull);
			} else if (TAG_CHILDREN_BYTES_INPUT.equals(tag)) {
				child = new BytesInput(context, pull);
			} else if (TAG_CHILDREN_FLAG_SELECT.equals(tag)) {
				child = new FlagSelect(context, pull);
			} else if (TAG_CHILDREN_SIMPLE_SELECT.equals(tag)) {
				child = new SimpleSelect(context, pull);
			} else if (TAG_CHILDREN.equals(tag)) {
				onInitChildren(pull);
				count = pull.getInt(ATTR_COUNT, count);
				children = new BaseChild[count];
			}
			if (child != null) {
				if (index == count)
					break;
				children[index++] = child;
			}
		}
		return children;
	}
	
	public boolean checkChanged(){
		mReader.mTempLogs.clear();
		for (BaseChild child : mChildren){
			child.checkChanged();
		}
		return mReader.mTempLogs.size() > 0;
	}
	
	public abstract void onInitChildren(Pull pull);

	public abstract void read();
	public abstract void onCheckChangeCallback(boolean cancel);
}
