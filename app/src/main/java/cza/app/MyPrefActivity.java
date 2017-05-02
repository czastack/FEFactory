package cza.app;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MyPrefActivity extends PreferenceActivity implements View.OnClickListener
{
    public void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		Dialog dialog;
		if (preference instanceof PreferenceScreen){
			dialog = ((PreferenceScreen) preference).getDialog();
			if (dialog != null){
				dialog.getActionBar().setDisplayHomeAsUpEnabled(true);
				View homeBtn = dialog.findViewById(android.R.id.home);
				if (homeBtn != null) {
					View view = (View) homeBtn.getParent();
					if (view instanceof FrameLayout) {
						if (view.getParent() instanceof LinearLayout) {
							view = (View) view.getParent();
						}
					} else {
						view = homeBtn;
					}
					view.setTag(dialog);
					view.setOnClickListener(this);
				}
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	@Override
	public void onClick(View v) {
		((Dialog)v.getTag()).dismiss();
	}
}
