package cza.MyFE;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MyActivity extends cza.app.MyActivity {

	protected void onCreate(Bundle sIS) {
        super.onCreate(sIS);
	}
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.to_launcher, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_launcher:
				bringToFront(LauncherActivity.class);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
