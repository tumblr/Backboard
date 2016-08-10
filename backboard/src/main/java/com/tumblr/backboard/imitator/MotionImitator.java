package com.tumblr.backboard.imitator;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import com.facebook.rebound.Spring;
import com.tumblr.backboard.MotionProperty;

/**
 * Maps a user's motion to a {@link android.view.View} via a {@link com.facebook.rebound.Spring}.
 * <p/>
 * Created by ericleong on 5/13/14.
 */
public class MotionImitator extends EventImitator {

	private static final String TAG = MotionImitator.class.getSimpleName();

	/**
	 * The motion property to imitate.
	 */
	@NonNull
	protected MotionProperty mProperty;

	/**
	 * Used internally to keep track of the initial down position.
	 */
	protected float mDownPosition;

	/**
	 * The offset between the view left/right location and the desired "center" of the view.
	 */
	protected float mOffset;

	/**
	 * Constructor. Uses {@link #TRACK_ABSOLUTE} and {@link #FOLLOW_EXACT}.
	 *
	 * @param property
	 * 		the property to track.
	 */
	public MotionImitator(@NonNull final MotionProperty property) {
		this(null, property, 0, TRACK_ABSOLUTE, FOLLOW_EXACT);
	}

	/**
	 * Constructor. It is necessary to call {@link #setSpring(Spring)} to set the spring.
	 *
	 * @param property
	 * 		the property to track.
	 * @param trackStrategy
	 * 		the tracking strategy.
	 * @param followStrategy
	 * 		the follow strategy.
	 */
	public MotionImitator(@NonNull final MotionProperty property, final int trackStrategy, final int followStrategy) {
		this(null, property, 0, trackStrategy, followStrategy);
	}

	/**
	 * Constructor. Uses {@link #TRACK_ABSOLUTE} and {@link #FOLLOW_EXACT}.
	 *
	 * @param spring
	 * 		the spring to use.
	 * @param property
	 * 		the property to track.
	 */
	public MotionImitator(@NonNull final Spring spring, @NonNull final MotionProperty property) {
		this(spring, property, spring.getEndValue(), TRACK_ABSOLUTE, FOLLOW_EXACT);
	}

	/**
	 * Constructor. Uses {@link #TRACK_ABSOLUTE} and {@link #FOLLOW_EXACT}.
	 *
	 * @param spring
	 * 		the spring to use.
	 * @param property
	 * 		the property to track.
	 * @param restValue
	 * 		the rest value for the spring.
	 */
	public MotionImitator(@NonNull final Spring spring, @NonNull final MotionProperty property, final double restValue) {
		this(spring, property, restValue, TRACK_ABSOLUTE, FOLLOW_EXACT);
	}

	/**
	 * Constructor.
	 *
	 * @param spring
	 * 		the spring to use.
	 * @param property
	 * 		the property to track.
	 * @param restValue
	 * 		the rest value for the spring.
	 * @param trackStrategy
	 * 		the tracking strategy.
	 * @param followStrategy
	 * 		the follow strategy.
	 */
	public MotionImitator(@NonNull final Spring spring, @NonNull final MotionProperty property, final double restValue,
	                      final int trackStrategy, final int followStrategy) {
		super(spring, restValue, trackStrategy, followStrategy);
		mProperty = property;
	}

	/**
	 * Constructor.
	 *
	 * @param property
	 * 		the property to track.
	 * @param restValue
	 * 		the rest value for the spring.
	 * @param trackStrategy
	 * 		the tracking strategy.
	 * @param followStrategy
	 * 		the follow strategy.
	 */
	public MotionImitator(@NonNull final MotionProperty property, final double restValue, final int trackStrategy,
	                      final int followStrategy) {
		super(restValue, trackStrategy, followStrategy);
		mProperty = property;
	}

	public void setRestValue(final double restValue) {
		this.mRestValue = restValue;
	}

	/**
	 * Puts the spring to rest.
	 *
	 * @return this object for chaining.
	 */
	@NonNull
	public MotionImitator rest() {
		if (mSpring != null) {
			mSpring.setEndValue(mRestValue);
		}

		return this;
	}

	@Override
	public void constrain(final MotionEvent event) {
		super.constrain(event);

		mDownPosition = mProperty.getValue(event) + mOffset;
	}

	@Override
	public void imitate(final View view, @NonNull final MotionEvent event) {

		final float viewValue = mProperty.getValue(view);
		final float eventValue = mProperty.getValue(event);
		mOffset = mProperty.getOffset(view);

		if (event.getHistorySize() > 0) {
			final float historicalValue = mProperty.getOldestValue(event);

			imitate(viewValue + mOffset, eventValue, eventValue - historicalValue, event);
		} else {
			imitate(viewValue + mOffset, eventValue, 0, event);
		}
	}

	@Override
	public void mime(final float offset, final float value, final float delta, final float dt, final MotionEvent event) {
		if (mTrackStrategy == TRACK_DELTA) {
			super.mime(offset - mDownPosition, value, delta, dt, event);
		} else {
			super.mime(offset, value, delta, dt, event);
		}
	}

	@Override
	protected double mapToSpring(final float motionValue) {
		return motionValue;
	}

	@NonNull
	public MotionProperty getProperty() {
		return mProperty;
	}
}
