package com.tumblr.backboard.example;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.performer.Performer;

/**
 * Scale a view with pinch zoom.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class ZoomFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_scale, container, false);

		final View rect = rootView.findViewById(R.id.rect);

		final SpringSystem springSystem = SpringSystem.create();
		final Spring spring = springSystem.createSpring();

		spring.addListener(new Performer(rect, View.SCALE_X));
		spring.addListener(new Performer(rect, View.SCALE_Y));

		spring.setCurrentValue(1.0f, true);

		final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getActivity(),
				new ScaleGestureDetector.SimpleOnScaleGestureListener() {
					@Override
					public boolean onScale(ScaleGestureDetector detector) {
						spring.setCurrentValue(
								spring.getCurrentValue() * detector.getScaleFactor(), true);

						return true;
					}

					@Override
					public void onScaleEnd(ScaleGestureDetector detector) {
						spring.setEndValue(1.0f);
					}
				});

		rootView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {
				return scaleGestureDetector.onTouchEvent(event);
			}
		});

		return rootView;
	}
}
