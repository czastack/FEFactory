package cza.app;

import cza.util.ViewUtils;
import android.view.View;

public abstract class ClickActivity extends MyActivity implements View.OnClickListener {
	protected void registerClick(int...ids){
		ViewUtils.registerClick(rootLayout, this, ids);
	}

	protected void registerClick(View parent, int...ids){
		ViewUtils.registerClick(parent, this, ids);
	}
}
