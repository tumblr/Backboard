package com.tumblr.backboard.example;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.MotionProperty;
import com.tumblr.backboard.imitator.MotionImitator;

/**
 * Snap a view to either the lower left or lower right corner.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class SnapFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_snap, container, false);

		final View circle = rootView.findViewById(R.id.circle);

		new Actor.Builder(SpringSystem.create(), circle)
				.addTranslateMotion(MotionProperty.Y)
				.addMotion(
						new MotionImitator(MotionProperty.X) {
							@Override
							public void release(MotionEvent event) {

								// snap to left or right depending on current location
								if (mSpring.getCurrentValue() >
										rootView.getMeasuredWidth() / 2 -
												circle.getMeasuredWidth() / 2) {

									mSpring.setEndValue(rootView.getMeasuredWidth() -
											circle.getMeasuredWidth());
								} else {

									mSpring.setEndValue(0);
								}
							}
						},
						View.TRANSLATION_X
				)
				.build();

		return rootView;
	}
}
