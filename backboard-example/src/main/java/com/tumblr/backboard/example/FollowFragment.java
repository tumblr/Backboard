package com.tumblr.backboard.example;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.MotionProperty;
import com.tumblr.backboard.imitator.SpringImitator;
import com.tumblr.backboard.performer.Performer;

/**
 * Demonstrates a draggable view that bounces back when released.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class FollowFragment extends Fragment {

	private static final String TAG = FollowFragment.class.getSimpleName();

	private static final int DIAMETER = 80;

	private ViewGroup mRootView;
	private View mCircle;
	private View[] mFollowers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_follow, container, false);

		mCircle = mRootView.findViewById(R.id.circle);

		FrameLayout.LayoutParams leaderParams = (FrameLayout.LayoutParams) mCircle
				.getLayoutParams();

		mFollowers = new View[4];

		float diameter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DIAMETER,
				getResources().getDisplayMetrics());

		TypedArray circles = getResources().obtainTypedArray(R.array.circles);

		// create the circle views
		int colorIndex = 1;
		for (int i = 0; i < mFollowers.length; i++) {
			mFollowers[i] = new View(getActivity());

			FrameLayout.LayoutParams params =
					new FrameLayout.LayoutParams((int) diameter, (int) diameter);
			params.gravity = leaderParams.gravity;
			mFollowers[i].setLayoutParams(params);

			mFollowers[i].setBackgroundDrawable(getResources().getDrawable(
					circles.getResourceId(colorIndex, -1)));

			colorIndex++;
			if (colorIndex >= circles.length()) {
				colorIndex = 0;
			}

			mRootView.addView(mFollowers[i]);
		}

		circles.recycle();

		/* Animation code */

		final SpringSystem springSystem = SpringSystem.create();

		// create the springs that control movement
		final Spring springX = springSystem.createSpring();
		final Spring springY = springSystem.createSpring();

		// bind circle movement to events
		new Actor.Builder(springSystem, mCircle).addMotion(springX, MotionProperty.X)
				.addMotion(springY, MotionProperty.Y).build();

		// add springs to connect between the views
		final Spring[] followsX = new Spring[mFollowers.length];
		final Spring[] followsY = new Spring[mFollowers.length];

		for (int i = 0; i < mFollowers.length; i++) {

			// create spring to bind views
			followsX[i] = springSystem.createSpring();
			followsY[i] = springSystem.createSpring();
			followsX[i].addListener(new Performer(mFollowers[i], View.TRANSLATION_X));
			followsY[i].addListener(new Performer(mFollowers[i], View.TRANSLATION_Y));

			// imitates another character
			final SpringImitator followX = new SpringImitator(followsX[i]);
			final SpringImitator followY = new SpringImitator(followsY[i]);

			//  imitate the previous character
			if (i == 0) {
				springX.addListener(followX);
				springY.addListener(followY);
			} else {
				followsX[i - 1].addListener(followX);
				followsY[i - 1].addListener(followY);
			}
		}

		return mRootView;
	}
}
