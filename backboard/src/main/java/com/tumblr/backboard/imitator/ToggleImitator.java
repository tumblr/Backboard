package com.tumblr.backboard.imitator;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import com.facebook.rebound.Spring;

/**
 * Toggle between two {@link com.facebook.rebound.Spring} states depending on whether or not the user is touching the
 * screen. When the user presses, {@link #constrain(android.view.MotionEvent)} is called and the active value is set.
 * When the user releases, {@link #release(android.view.MotionEvent)} is called and the rest value is set.
 * <p>
 * Created by ericleong on 5/21/14.
 */
public class ToggleImitator extends EventImitator implements View.OnTouchListener {

	private double mActiveValue;

	/**
	 * Constructor. It is necessary to call {@link #setSpring(Spring)} to set the spring.
	 *
	 * @param spring
	 * 		the spring to use.
	 * @param restValue
	 * 		the value when off.
	 * @param activeValue
	 * 		the value when on.
	 */
	public ToggleImitator(@NonNull final Spring spring, final double restValue, final double activeValue) {
		super(spring, restValue, TRACK_ABSOLUTE, FOLLOW_EXACT);
		mActiveValue = activeValue;
	}

	@Override
	public void constrain(final MotionEvent event) {
		mSpring.setEndValue(mActiveValue);
	}

	@Override
	protected double mapToSpring(final float motionValue) {
		// not used
		return mActiveValue;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(final View v, @NonNull final MotionEvent event) {
		imitate(v, event);

		return true;
	}

	@Override
	@SuppressLint("ClickableViewAccessibility")
	public void imitate(final View view, @NonNull final MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			constrain(event);
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			release(event);
			break;

		default:
		}
	}
}
