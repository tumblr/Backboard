package com.tumblr.backboard.example;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.imitator.ToggleImitator;
import com.tumblr.backboard.performer.MapPerformer;
import com.tumblr.backboard.performer.Performer;

/**
 * Demonstrates a view that shrinks when touched and bounces back when released.
 * <p/>
 * Created by ericleong on 11/28/15.
 */
public class PressFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_press, container, false);

		new Actor.Builder(SpringSystem.create(), rootView.findViewById(R.id.circle))
				.addMotion(new ToggleImitator(null, 1.0, 0.5),
						new Performer(View.SCALE_X), new Performer(View.SCALE_Y))
				.build();

		return rootView;
	}
}
