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
import com.tumblr.backboard.performer.Performer;

/**
 * A view that contracts and expands when pressed.
 * <p/>
 * Created by ericleong on 5/7/14.
 */
public class ReboundFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_rebound, container, false);

		final View rect = rootView.findViewById(R.id.rect);

		final SpringSystem springSystem = SpringSystem.create();

		final Spring spring = springSystem.createSpring();

		spring.addListener(new MapPerformer(rect, View.SCALE_X, 1f, 0.5f));
		spring.addListener(new MapPerformer(rect, View.SCALE_Y, 1f, 0.5f));

		rootView.setOnTouchListener(new ToggleImitator(spring, 0, 1));

		return rootView;
	}
}
