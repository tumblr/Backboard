package com.tumblr.backboard.example;

import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.performer.Performer;

/**
 * Demonstrates creating and removing {@link android.view.View}s and
 * {@link com.facebook.rebound.Spring}s.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class ExplosionFragment extends Fragment {

	private static final int DIAMETER = 80;

	private RelativeLayout mRootView;

	private int colorIndex;
	private TypedArray mCircles;

	private Handler mHandler;
	private Runnable mRunnable;
	private boolean mTouching;

	private SpringSystem mSpringSystem;

	private SpringConfig mCoasting;
	private SpringConfig mGravity;

	/**
	 * Destroys the attached {@link com.facebook.rebound.Spring}.
	 */
	private static class Destroyer implements SpringListener {

		public int mMin, mMax;

		protected ViewGroup mViewGroup;
		protected View mViewToRemove;

		private Destroyer(ViewGroup viewGroup, View viewToRemove, int min,
		                  int max) {
			mViewGroup = viewGroup;
			mViewToRemove = viewToRemove;

			mMin = min;
			mMax = max;
		}

		public boolean shouldClean(Spring spring) {
			// these are arbitrary values to keep the view from disappearing before it is
			// fully off the screen
			return spring.getCurrentValue() < mMin || spring.getCurrentValue() > mMax;
		}

		public void clean(Spring spring) {
			if (mViewGroup != null && mViewToRemove != null) {
				mViewGroup.removeView(mViewToRemove);
			}
			if (spring != null) {
				spring.destroy();
			}
		}

		@Override
		public void onSpringUpdate(Spring spring) {
			if (shouldClean(spring)) {
				clean(spring);
			}
		}

		@Override
		public void onSpringAtRest(Spring spring) {

		}

		@Override
		public void onSpringActivate(Spring spring) {

		}

		@Override
		public void onSpringEndStateChange(Spring spring) {

		}
	}

	private class CircleSpawn implements Runnable {

		@Override
		public void run() {
			if (mTouching) {

				colorIndex++;
				if (colorIndex >= mCircles.length()) {
					colorIndex = 0;
				}

				float diameter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DIAMETER,
						getResources().getDisplayMetrics());

				Drawable drawable = getResources().getDrawable(mCircles.getResourceId
						(colorIndex, -1));

				createCircle(getActivity(), mRootView, mSpringSystem, mCoasting, mGravity,
						(int) diameter, drawable);

				mHandler.postDelayed(this, 100);
			}
		}
	}

	private static void createCircle(Context context, ViewGroup rootView,
	                                 SpringSystem springSystem,
	                                 SpringConfig coasting,
	                                 SpringConfig gravity,
	                                 int diameter,
	                                 Drawable backgroundDrawable) {

		final Spring xSpring = springSystem.createSpring().setSpringConfig(coasting);
		final Spring ySpring = springSystem.createSpring().setSpringConfig(gravity);

		// create view
		View view = new View(context);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(diameter, diameter);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.setLayoutParams(params);
		view.setBackgroundDrawable(backgroundDrawable);

		rootView.addView(view);

		// generate random direction and magnitude
		double magnitude = Math.random() * 1000 + 3000;
		double angle = Math.random() * Math.PI / 2 + Math.PI / 4;

		xSpring.setVelocity(magnitude * Math.cos(angle));
		ySpring.setVelocity(-magnitude * Math.sin(angle));

		int maxX = rootView.getMeasuredWidth() / 2 + diameter;
		xSpring.addListener(new Destroyer(rootView, view, -maxX, maxX));

		int maxY = rootView.getMeasuredHeight() / 2 + diameter;
		ySpring.addListener(new Destroyer(rootView, view, -maxY, maxY));

		xSpring.addListener(new Performer(view, View.TRANSLATION_X));
		ySpring.addListener(new Performer(view, View.TRANSLATION_Y));

		// set a different end value to cause the animation to play
		xSpring.setEndValue(2);
		ySpring.setEndValue(9001);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler();
		mSpringSystem = SpringSystem.create();

		mCoasting = SpringConfig.fromOrigamiTensionAndFriction(0, 0);
		mCoasting.tension = 0;

		// this is very much a hack, since the end value is set to 9001 to simulate constant
		// acceleration.
		mGravity = SpringConfig.fromOrigamiTensionAndFriction(0, 0);
		mGravity.tension = 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_bloom, container, false);

		mCircles = getResources().obtainTypedArray(R.array.circles);
		mRunnable = new CircleSpawn();

		mRootView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					// create circles as long as the user is holding down
					mTouching = true;
					mHandler.post(mRunnable);

					break;
				case MotionEvent.ACTION_UP:
					mTouching = false;

					break;
				}

				return true;
			}
		});

		return mRootView;
	}
}
