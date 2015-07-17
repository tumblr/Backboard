package com.tumblr.backboard.example;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.imitator.ToggleImitator;
import com.tumblr.backboard.performer.MapPerformer;

/**
 * A ring of views that bloom and then contract.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class BloomFragment extends Fragment {

	private static final int DIAMETER = 80;
	private static final int RING_DIAMETER = 5 * DIAMETER;

	private static final int OPEN = 1;
	private static final int CLOSED = 0;

	private RelativeLayout mRootView;
	private View[] mCircles;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_bloom, container, false);

		mCircles = new View[6];

		float diameter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DIAMETER,
				getResources().getDisplayMetrics());

		TypedArray circles = getResources().obtainTypedArray(R.array.circles);

		// layout params
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) diameter,
				(int) diameter);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);

		// create the circle views
		int colorIndex = 0;
		for (int i = 0; i < mCircles.length; i++) {
			mCircles[i] = new View(getActivity());

			mCircles[i].setLayoutParams(params);

			mCircles[i].setBackgroundDrawable(getResources().getDrawable(circles
					.getResourceId(colorIndex, -1)));

			colorIndex++;
			if (colorIndex >= circles.length()) {
				colorIndex = 0;
			}

			mRootView.addView(mCircles[i]);
		}

		/* Animations! */

		final SpringSystem springSystem = SpringSystem.create();

		// create spring
		final Spring spring = springSystem.createSpring();

		// add listeners along arc
		double arc = 2 * Math.PI / mCircles.length;

		for (int i = 0; i < mCircles.length; i++) {
			View view = mCircles[i];

			// map spring to a line segment from the center to the edge of the ring
			spring.addListener(new MapPerformer(view, View.TRANSLATION_X, 0, 1,
					0, (float) (RING_DIAMETER * Math.cos(i * arc))));

			spring.addListener(new MapPerformer(view, View.TRANSLATION_Y, 0, 1,
					0, (float) (RING_DIAMETER * Math.sin(i * arc))));

			spring.setEndValue(CLOSED);
		}

		mRootView.setOnTouchListener(new ToggleImitator(spring, CLOSED, OPEN));

		return mRootView;
	}
}
