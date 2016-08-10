package com.tumblr.backboard.example;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.MotionProperty;
import com.tumblr.backboard.imitator.Imitator;
import com.tumblr.backboard.imitator.InertialImitator;

/**
 * Snap a view to either the lower left or lower right corner.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class ConstrainedFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_constrain, container, false);

		final View constraintView = rootView.findViewById(R.id.constraint);

		final View circle = rootView.findViewById(R.id.circle);

		final InertialImitator motionImitatorX =
				new InertialImitator(MotionProperty.X, Imitator.TRACK_DELTA,
						Imitator.FOLLOW_SPRING, 0, 0);

		final InertialImitator motionImitatorY =
				new InertialImitator(MotionProperty.Y, Imitator.TRACK_DELTA,
						Imitator.FOLLOW_SPRING, 0, 0);

		new Actor.Builder(SpringSystem.create(), circle)
				.addMotion(motionImitatorX, View.TRANSLATION_X)
				.addMotion(motionImitatorY, View.TRANSLATION_Y)
				.build();

		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				motionImitatorX.setMinValue(
						-constraintView.getMeasuredWidth() / 2 + circle.getMeasuredWidth() / 2);
				motionImitatorX.setMaxValue(
						constraintView.getMeasuredWidth() / 2 - circle.getMeasuredWidth() / 2);
				motionImitatorY.setMinValue(
						-constraintView.getMeasuredHeight() / 2 + circle.getMeasuredWidth() / 2);
				motionImitatorY.setMaxValue(
						constraintView.getMeasuredHeight() / 2 - circle.getMeasuredWidth() / 2);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});

		return rootView;
	}
}
