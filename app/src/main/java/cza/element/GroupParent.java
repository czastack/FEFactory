package cza.element;
import cza.MyFE.R;
import cza.util.Pull;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class GroupParent extends BaseParent {
	
	public int[] mAddrs;
	
	public GroupParent(Context context, Pull pull){
		super(context, pull);
	}

	@Override
	public void onInitChildren(Pull pull) {}

	@Override
	public View getView() {
		if (mView == null){
			ViewGroup layout = (ViewGroup)View.inflate(mContext, R.layout.element_parent, null);
			mView = layout;
			loadChildren();
			for (BaseChild child : mChildren){
				layout.addView(child.getView());
			}
			read();
		}
		return mView;
	}

	@Override
	public void onCheckChangeCallback(boolean cancel) {
		if (!cancel){
			read();
		}
	}

	@Override
	public void read() {
		if (mChildren == null)
			return;
		for (BaseChild child : mChildren){
			child.read(-1);
		}
	}
}
