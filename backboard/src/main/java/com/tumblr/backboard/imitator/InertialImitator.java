package com.tumblr.backboard.imitator;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.tumblr.backboard.MotionProperty;

/**
 * A {@link com.tumblr.backboard.imitator.ConstrainedMotionImitator} that moves freely when the
 * user is not dragging it. It copies the {@link com.facebook.rebound.SpringConfig} in
 * {@link #setSpring(com.facebook.rebound.Spring)} to use when the user is dragging.
 * <p/>
 * Created by ericleong on 11/6/14.
 */
public class InertialImitator extends ConstrainedMotionImitator implements SpringListener {

	/**
	 * The friction (in {@link com.facebook.rebound.SpringConfig}) to use when moving freely.
	 */
	public static final float DEFAULT_FRICTION = 1.0f;

	/**
	 * The {@link com.facebook.rebound.SpringConfig} to use when moving freely.
	 */
	public static final SpringConfig SPRING_CONFIG_FRICTION = new SpringConfig(0, DEFAULT_FRICTION);

	/**
	 * Used to convert {@link com.facebook.rebound.Spring#getVelocity()} to the units needed by
	 * {@link #calculateRestPosition()}.
	 */
	public static final int VELOCITY_RATIO = 24;

	/**
	 * The {@link SpringConfig} to use when being dragged.
	 */
	protected SpringConfig mOriginalConfig;

	/**
	 * Constructor. Uses {@link #TRACK_ABSOLUTE} and {@link #FOLLOW_EXACT}.
	 *
	 * @param property
	 * 		the desired property to imitate
	 * @param minValue
	 * 		the minimum value
	 * @param maxValue
	 * 		the maximum value
	 */
	public InertialImitator(@NonNull final MotionProperty property, final double minValue, final double maxValue) {
		super(property, minValue, maxValue);
	}

	/**
	 * Constructor.
	 *
	 * @param property
	 * 		the desired property to imitate
	 * @param trackStrategy
	 * 		the tracking strategy.
	 * @param followStrategy
	 * 		the follow strategy.
	 * @param minValue
	 * 		the desired minimum spring value.
	 * @param maxValue
	 * 		the desired maximum spring value.
	 */
	public InertialImitator(@NonNull final MotionProperty property, final int trackStrategy, final int followStrategy,
	                        final double minValue, final double maxValue) {
		super(property, trackStrategy, followStrategy, minValue, maxValue);
	}

	/**
	 * Sets the {@link com.facebook.rebound.Spring} that this imitator should use. This class
	 * attaches itself as a {@link com.facebook.rebound.SpringListener} and stores the
	 * {@link com.facebook.rebound.SpringConfig} to use when the user is dragging.
	 *
	 * @param spring
	 * 		the spring to use
	 */
	@Override
	public void setSpring(@NonNull final Spring spring) {
		super.setSpring(spring);
		spring.addListener(this);

		mOriginalConfig = spring.getSpringConfig();
	}

	@Override
	public void constrain(final MotionEvent event) {
		super.constrain(event);

		mSpring.setSpringConfig(mOriginalConfig);
	}

	@Override
	public void release(final MotionEvent event) {
		// snap to left or right depending on current location
		final double restPosition = calculateRestPosition();
		if (mSpring.getCurrentValue() > mMaxValue && restPosition > mMaxValue) {
			mSpring.setEndValue(mMaxValue);
		} else if (mSpring.getCurrentValue() < mMinValue && restPosition < mMinValue) {
			mSpring.setEndValue(mMinValue);
		} else {
			mSpring.setSpringConfig(SPRING_CONFIG_FRICTION);
			mSpring.setEndValue(Double.MAX_VALUE);
		}
	}

	/**
	 * @return the spring position when it comes to rest (given infinite time).
	 */
	private double calculateRestPosition() {
		// http://prettygoodphysics.wikispaces.com/file/view/DifferentialEquations.pdf
		return mSpring.getCurrentValue()
				+ VELOCITY_RATIO * mSpring.getVelocity() / (mSpring.getSpringConfig().friction);
	}

	public void setMinValue(final double minValue) {
		this.mMinValue = minValue;
	}

	public void setMaxValue(final double maxValue) {
		this.mMaxValue = maxValue;
	}

	@Override
	public void onSpringUpdate(final Spring spring) {
		if (mSpring != null) {
			final double restPosition = calculateRestPosition();
			if (mSpring.getSpringConfig().equals(SPRING_CONFIG_FRICTION)) {
				if (mSpring.getCurrentValue() > mMaxValue && restPosition > mMaxValue) {
					mSpring.setSpringConfig(mOriginalConfig);
					mSpring.setEndValue(mMaxValue);
				} else if (mSpring.getCurrentValue() < mMinValue && restPosition < mMinValue) {
					mSpring.setSpringConfig(mOriginalConfig);
					mSpring.setEndValue(mMinValue);
				}
			}
		}
	}

	@Override
	public void onSpringAtRest(final Spring spring) {
		// pass
	}

	@Override
	public void onSpringActivate(final Spring spring) {
		// pass
	}

	@Override
	public void onSpringEndStateChange(final Spring spring) {
		// pass
	}
}
