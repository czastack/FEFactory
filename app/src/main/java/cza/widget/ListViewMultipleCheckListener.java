package cza.widget;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import java.util.Arrays;

public class ListViewMultipleCheckListener implements View.OnTouchListener {
	private ListView mListView;
	private int count;
	private int[] positions = new int[2];
	private boolean ok;
	private Callback mCallback;

	public ListViewMultipleCheckListener(ListView li, Callback callback) {
		mListView = li;
		mCallback = callback;
		serve(true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				count = 1;
				break;
			case MotionEvent.ACTION_UP:
				count = 0;
				if (ok){
					mCallback.onItemCheck(positions[0], positions[1]);
					ok = false;
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				count--;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				count++;
				if (count == 2) {
					for (int i = 0; i < 2; i++) {
						int x = (int) event.getX(i);
						int y = (int) event.getY(i);
						int p = mListView.pointToPosition(x, y);
						if (p == -1){
							return false;
						}
						positions[i] = p;
					}
					Arrays.sort(positions);
					ok = true;
				}
				return true;
		}
		return false;
	}

	void serve(boolean on){
		mListView.setOnTouchListener(on ? this : null);
	}

	public interface Callback {
		public void onItemCheck(int start, int end);
	}
}



