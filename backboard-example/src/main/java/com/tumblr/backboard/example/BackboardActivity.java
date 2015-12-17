package com.tumblr.backboard.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Container activity.
 */
public class BackboardActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backboard);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new MoveFragment())
					.commit();

			setTitle(R.string.action_move);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.backboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
		case R.id.action_move:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new MoveFragment())
					.commit();
			setTitle(R.string.action_move);
			return true;

		case R.id.action_snap:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new SnapFragment())
					.commit();
			setTitle(R.string.action_snap);
			return true;

		case R.id.action_scale:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new ScaleFragment())
					.commit();
			setTitle(R.string.action_scale);
			return true;

		case R.id.action_bloom:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new BloomFragment())
					.commit();
			setTitle(R.string.action_bloom);
			return true;

		case R.id.action_flower:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new FlowerFragment())
					.commit();
			setTitle(R.string.action_flower);
			return true;

		case R.id.action_appear:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new AppearFragment())
					.commit();
			setTitle(R.string.action_appear);
			return true;

		case R.id.action_explosion:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new ExplosionFragment())
					.commit();
			setTitle(R.string.action_explosion);
			return true;

		case R.id.action_follow:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new FollowFragment())
					.commit();
			setTitle(R.string.action_follow);
			return true;

		case R.id.action_zoom:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new ZoomFragment())
					.commit();
			setTitle(R.string.action_zoom);
			return true;

		case R.id.action_constrained:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new ConstrainedFragment())
					.commit();
			setTitle(R.string.action_constrained);
			return true;

		case R.id.action_press:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new PressFragment())
					.commit();
			setTitle(R.string.action_press);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
