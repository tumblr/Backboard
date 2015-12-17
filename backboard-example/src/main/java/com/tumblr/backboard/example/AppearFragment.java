package com.tumblr.backboard.example;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.MotionProperty;
import com.tumblr.backboard.imitator.EventImitator;
import com.tumblr.backboard.imitator.MotionImitator;
import com.tumblr.backboard.performer.Performer;

/**
 * A dialog-like animation.
 */
public class AppearFragment extends Fragment {

	private static final String TAG = AppearFragment.class.getSimpleName();

	private View mRootView;
	private View[] mCircles;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_appear, container, false);

		// grab the circles
		mCircles = new View[6];
		mCircles[0] = mRootView.findViewById(R.id.circle0);
		mCircles[1] = mRootView.findViewById(R.id.circle1);
		mCircles[2] = mRootView.findViewById(R.id.circle2);
		mCircles[3] = mRootView.findViewById(R.id.circle3);
		mCircles[4] = mRootView.findViewById(R.id.circle4);
		mCircles[5] = mRootView.findViewById(R.id.circle5);

		final SpringSystem springSystem = SpringSystem.create();

		final Spring[] springs = new Spring[6];
		final Actor[] actors = new Actor[6];

		// the selected view should move to heaven and the unselected ones should go to hell
		View.OnClickListener select = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Spring spring = (Spring) v.getTag();

				// get root location so we can compensate for it
				int[] rootLocation = new int[2];
				mRootView.getLocationInWindow(rootLocation);

				int[] location = new int[2];

				for (int i = 0; i < mCircles.length; i++) {
					actors[i].setTouchEnabled(false);

					for (Actor.Motion motion : actors[i].getMotions()) {
						for (EventImitator imitator : motion.getImitators()) {
							if (imitator instanceof MotionImitator) {
								final MotionImitator motionImitator = (MotionImitator) imitator;
								if (motionImitator.getProperty() == MotionProperty.Y) {
									// TODO: disable the y-motion because it is about to be animated
									// imitator.getSpring().deregister();
								} else {
									imitator.release(null);
								}
							}
						}
					}

					mCircles[i].getLocationInWindow(location);

					if (springs[i] == spring) {
						// goes to the top
						springs[i].setEndValue(-location[1] + rootLocation[1]
								- v.getMeasuredHeight());
					} else {
						// go back to the bottom
						springs[i].setEndValue(mRootView.getMeasuredHeight() - location[1] +
								rootLocation[1] +
								2 * Math.random() * mCircles[i].getMeasuredHeight());
					}
				}
			}
		};

		// attach listeners
		for (int i = 0; i < mCircles.length; i++) {
			springs[i] = springSystem.createSpring();

			springs[i].addListener(new Performer(mCircles[i], View.TRANSLATION_Y));

			mCircles[i].setTag(springs[i]);

			mCircles[i].setOnClickListener(select);

			actors[i] = new Actor.Builder(springSystem, mCircles[i])
					.addTranslateMotion(MotionProperty.X)
					.addTranslateMotion(MotionProperty.Y).build();
		}

		mRootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// grab location of root view so we can compensate for it
				int[] rootLocation = new int[2];
				v.getLocationInWindow(rootLocation);

				int[] location = new int[2];

				for (int i = 0; i < mCircles.length; i++) {

					if (springs[i].getEndValue() == 0) { // hide
						mCircles[i].getLocationInWindow(location);

						// if the end values are different, they will move at different speeds
						springs[i].setEndValue(mRootView.getMeasuredHeight() - location[1] +
								rootLocation[1] +
								2 * Math.random() * mCircles[i].getMeasuredHeight());
					} else {
						actors[i].setTouchEnabled(true);

						for (Actor.Motion motion : actors[i].getMotions()) {
							for (EventImitator imitator : motion.getImitators()) {
								if (imitator instanceof MotionImitator) {
									final MotionImitator motionImitator = (MotionImitator) imitator;
									imitator.getSpring().setCurrentValue(0);

									// TODO: re-enable the y motion.
//									if (imitator.getProperty() == MotionProperty.Y &&
//											!imitator.getSpring().isRegistered()) {
//										imitator.getSpring().register();
//									}
								}
							}
						}

						springs[i].setEndValue(0); // appear
					}
				}
			}
		});

		return mRootView;
	}
}
