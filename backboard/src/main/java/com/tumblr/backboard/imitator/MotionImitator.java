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
	public MotionImitator(@NonNull MotionProperty property) {
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
	public MotionImitator(@NonNull MotionProperty property, int trackStrategy, int followStrategy) {
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
	public MotionImitator(@NonNull Spring spring, @NonNull MotionProperty property) {
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
	public MotionImitator(@NonNull Spring spring, @NonNull MotionProperty property, double restValue) {
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
	public MotionImitator(@NonNull Spring spring, @NonNull MotionProperty property, double restValue,
	                      int trackStrategy, int followStrategy) {
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
	public MotionImitator(@NonNull MotionProperty property, double restValue, int trackStrategy,
	                      int followStrategy) {
		super(restValue, trackStrategy, followStrategy);
		mProperty = property;
	}

	public void setRestValue(double restValue) {
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
	public void constrain(MotionEvent event) {
		super.constrain(event);

		mDownPosition = mProperty.getValue(event) + mOffset;
	}

	/**
	 * Maps a user's motion to {@link android.view.View} via a {@link com.facebook.rebound.Spring}.
	 *
	 * @param view
	 * 		the view to perturb.
	 * @param event
	 * 		the motion to imitate.
	 */
	public void imitate(final View view, @NonNull MotionEvent event) {

		final float viewValue = mProperty.getValue(view);
		final float eventValue = mProperty.getValue(event);
		mOffset = mProperty.getOffset(view);

		if (event.getHistorySize() > 0) {
			float historicalValue = mProperty.getOldestValue(event);

			imitate(viewValue + mOffset, eventValue, eventValue - historicalValue, event);
		} else {
			imitate(viewValue + mOffset, eventValue, 0, event);
		}
	}

	@Override
	public void mime(float offset, float value, float delta, float dt, MotionEvent event) {
		if (mTrackStrategy == TRACK_DELTA) {
			super.mime(offset - mDownPosition, value, delta, dt, event);
		} else {
			super.mime(offset, value, delta, dt, event);
		}
	}

	@Override
	protected double mapToSpring(float motionValue) {
		return motionValue;
	}

	@NonNull
	public MotionProperty getProperty() {
		return mProperty;
	}
}
