package com.tumblr.backboard.example;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.MotionProperty;
import com.tumblr.backboard.imitator.Imitator;
import com.tumblr.backboard.imitator.MotionImitator;
import com.tumblr.backboard.imitator.ToggleImitator;
import com.tumblr.backboard.performer.MapPerformer;

/**
 * A ring of views that bloom and then contract, with a selector that follows the finger.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class FlowerFragment extends Fragment {

	private static final int DIAMETER = 50;
	private static final int RING_DIAMETER = 7 * DIAMETER;

	private RelativeLayout mRootView;
	private View mCircle;
	private View[] mCircles;

	private static final int OPEN = 1;
	private static final int CLOSED = 0;

	private static double distSq(double x1, double y1, double x2, double y2) {
		return Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
	}

	private static View nearest(float x, float y, View[] views) {
		double minDistSq = Double.MAX_VALUE;
		View minView = null;

		for (View view : views) {
			double distSq = distSq(x, y, view.getX() + view.getMeasuredWidth() / 2,
					view.getY() + view.getMeasuredHeight() / 2);

			if (distSq < Math.pow(1.5f * view.getMeasuredWidth(), 2) && distSq < minDistSq) {
				minDistSq = distSq;
				minView = view;
			}
		}

		return minView;
	}

	/**
	 * Snaps to the nearest circle.
	 */
	private class SnapImitator extends MotionImitator {

		public SnapImitator(MotionProperty property) {
			super(property, 0, Imitator.TRACK_ABSOLUTE, Imitator.FOLLOW_SPRING);
		}

		@Override
		public void mime(float offset, float value, float delta, float dt, MotionEvent event) {
			// find the nearest view
			final View nearest = nearest(
					event.getX() + mCircle.getX(),
					event.getY() + mCircle.getY(), mCircles);

			if (nearest != null) {
				// snap to it - remember to compensate for translation
				switch (mProperty) {
				case X:
					getSpring().setEndValue(nearest.getX() + nearest.getWidth() / 2
							- mCircle.getLeft() - mCircle.getWidth() / 2);
					break;
				case Y:
					getSpring().setEndValue(nearest.getY() + nearest.getHeight() / 2
							- mCircle.getTop() - mCircle.getHeight() / 2);
					break;
				}
			} else {
				// follow finger
				super.mime(offset, value, delta, dt, event);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_flower, container, false);

		mCircles = new View[6];
		mCircle = mRootView.findViewById(R.id.circle);

		final float diameter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DIAMETER,
				getResources().getDisplayMetrics());

		final TypedArray circles = getResources().obtainTypedArray(R.array.circles);

		// layout params
		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) diameter,
				(int) diameter);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);

		// create the circle views
		int colorIndex = 0;
		for (int i = 0; i < mCircles.length; i++) {
			mCircles[i] = new View(getActivity());

			mCircles[i].setLayoutParams(params);

			mCircles[i].setBackgroundDrawable(getResources().getDrawable(
					circles.getResourceId(colorIndex, -1)));

			colorIndex++;
			if (colorIndex >= circles.length()) {
				colorIndex = 0;
			}

			mRootView.addView(mCircles[i], 0);
		}

		circles.recycle();

		/* Animations! */

		final SpringSystem springSystem = SpringSystem.create();

		// create spring
		final Spring spring = springSystem.createSpring();

		// add listeners along arc
		final double arc = 2 * Math.PI / mCircles.length;

		for (int i = 0; i < mCircles.length; i++) {
			View view = mCircles[i];

			// map spring to a line segment from the center to the edge of the ring
			spring.addListener(new MapPerformer(view, View.TRANSLATION_X, 0, 1,
					0, (float) (RING_DIAMETER * Math.cos(i * arc))));

			spring.addListener(new MapPerformer(view, View.TRANSLATION_Y, 0, 1,
					0, (float) (RING_DIAMETER * Math.sin(i * arc))));

			spring.setEndValue(CLOSED);
		}

		final ToggleImitator imitator = new ToggleImitator(spring, CLOSED, OPEN);

		// move circle using finger, snap when near another circle, and bloom when touched
		new Actor.Builder(SpringSystem.create(), mCircle)
				.addMotion(new SnapImitator(MotionProperty.X), View.TRANSLATION_X)
				.addMotion(new SnapImitator(MotionProperty.Y), View.TRANSLATION_Y)
				.onTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// bloom!
						imitator.imitate(event);

						return true;
					}
				})
				.build();

		return mRootView;
	}
}
