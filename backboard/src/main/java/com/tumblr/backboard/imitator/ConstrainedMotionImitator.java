package com.tumblr.backboard.imitator;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import com.tumblr.backboard.MotionProperty;

/**
 * Constrains the motion between the minimum and maximum values.
 * <p/>
 * Created by ericleong on 10/9/14.
 */
public class ConstrainedMotionImitator extends MotionImitator {

	/**
	 * Desired minimum spring value (overshoot may still occur).
	 */
	protected double mMinValue;
	/**
	 * Desired maximum spring value (overshoot may still occur).
	 */
	protected double mMaxValue;

	/**
	 * Constructor. Uses {@link #TRACK_ABSOLUTE} and {@link #FOLLOW_EXACT}.
	 *
	 * @param property
	 * 		the property to track.
	 * @param minValue
	 * 		the desired minimum spring value.
	 * @param maxValue
	 * 		the desired maximum spring value.
	 */
	public ConstrainedMotionImitator(@NonNull MotionProperty property, double minValue, double maxValue) {
		this(property, TRACK_ABSOLUTE, FOLLOW_EXACT, minValue, maxValue);
	}

	/**
	 * Constructor.
	 *
	 * @param property
	 * 		the property to track.
	 * @param trackStrategy
	 * 		the tracking strategy.
	 * @param followStrategy
	 * 		the follow strategy.
	 * @param minValue
	 * 		the desired minimum spring value.
	 * @param maxValue
	 * 		the desired maximum spring value.
	 */
	public ConstrainedMotionImitator(@NonNull MotionProperty property, int trackStrategy,
	                                 int followStrategy, double minValue, double maxValue) {
		super(property, trackStrategy, followStrategy);

		mMinValue = minValue;
		mMaxValue = maxValue;
	}

	@Override
	public void release(MotionEvent event) {
		if (mSpring != null) {
			// snap to left or right depending on current location
			if (mSpring.getCurrentValue() > mMaxValue) {
				mSpring.setEndValue(mMaxValue);
			} else if (mSpring.getCurrentValue() < mMinValue) {
				mSpring.setEndValue(mMinValue);
			}
		}
	}

	public void setMinValue(double minValue) {
		this.mMinValue = minValue;
	}

	public void setMaxValue(double maxValue) {
		this.mMaxValue = maxValue;
	}
}
