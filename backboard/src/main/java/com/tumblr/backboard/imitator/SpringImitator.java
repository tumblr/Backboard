package com.tumblr.backboard.imitator;

import android.support.annotation.NonNull;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;

/**
 * Allows a {@link com.facebook.rebound.Spring} to imitate another {@link com.facebook.rebound.Spring}.
 * <p/>
 * The default implementation sets the end value of the attached Spring to be the same as the
 * current value of the couple it is imitating.
 * <p/>
 * Created by ericleong on 5/16/14.
 */
public class SpringImitator extends Imitator implements SpringListener {

	/**
	 * @param spring
	 * 		the spring to imitate
	 */
	public SpringImitator(@NonNull Spring spring) {
		super(spring);
	}

	/**
	 * Constructor.
	 *
	 * @param spring
	 * 		the spring to imitate.
	 * @param trackStrategy
	 * 		the tracking strategy.
	 * @param followStrategy
	 * 		the follow strategy.
	 */
	protected SpringImitator(@NonNull Spring spring, int trackStrategy, int followStrategy) {
		super(spring, trackStrategy, followStrategy);
	}

	/**
	 * Constructor.
	 *
	 * @param spring
	 * 		the spring to imitate.
	 * @param restValue
	 * 		the spring value when not being perturbed.
	 * @param trackStrategy
	 * 		the tracking strategy.
	 * @param followStrategy
	 * 		the follow strategy.
	 */
	protected SpringImitator(@NonNull Spring spring, double restValue, int trackStrategy, int followStrategy) {
		super(spring, restValue, trackStrategy, followStrategy);
	}

	@Override
	protected double mapToSpring(float motionValue) {
		return motionValue;
	}

	@Override
	public void onSpringUpdate(@NonNull Spring spring) {
		mSpring.setEndValue(spring.getCurrentValue());
	}

	@Override
	public void onSpringAtRest(Spring spring) {

	}

	@Override
	public void onSpringActivate(Spring spring) {

	}

	@Override
	public void onSpringEndStateChange(Spring spring) {

	}
}
