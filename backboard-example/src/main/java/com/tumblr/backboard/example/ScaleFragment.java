package com.tumblr.backboard.example;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.performer.Performer;

/**
 * Scale a view with a finger.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class ScaleFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_scale, container, false);

		final View rect = rootView.findViewById(R.id.rect);

		final SpringSystem springSystem = SpringSystem.create();

		final Spring spring = springSystem.createSpring();

		spring.addListener(new Performer(rect, View.SCALE_X));
		spring.addListener(new Performer(rect, View.SCALE_Y));

		rootView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					spring.setVelocity(0);

				case MotionEvent.ACTION_MOVE:

					// can't use Imitation here because there is no nice mapping from
					// an event property to a Spring
					float scaleX, scaleY;

					float delta = event.getX() - (rect.getX() + rect.getMeasuredWidth() / 2);
					scaleX = Math.abs(delta) / (rect.getMeasuredWidth() / 2);

					delta = event.getY() - (rect.getY() + rect.getMeasuredHeight() / 2);
					scaleY = Math.abs(delta) / (rect.getMeasuredHeight() / 2);

					float scale = Math.max(scaleX, scaleY);

					spring.setEndValue(scale);

					break;
				case MotionEvent.ACTION_UP:
					spring.setEndValue(1f);

					break;
				}

				return true;
			}
		});

		return rootView;
	}
}
