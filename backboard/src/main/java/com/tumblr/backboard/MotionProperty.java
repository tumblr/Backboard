package com.tumblr.backboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;

/**
 * Used by {@link com.tumblr.backboard.Actor} and
 * {@link com.tumblr.backboard.imitator.MotionImitator} to determine which axis to use when mapping
 * from a {@link android.view.MotionEvent}.
 * <p/>
 * Created by ericleong on 5/29/14.
 */
public enum MotionProperty {
	/**
	 * X direction, corresponds to {@link MotionEvent#getX()} and maps to {@link View#TRANSLATION_X}.
	 */
	X(View.TRANSLATION_X),
	/**
	 * Y direction, corresponds to {@link MotionEvent#getY()} and maps to {@link View#TRANSLATION_Y}.
	 */
	Y(View.TRANSLATION_Y);

	@NonNull
	private final Property<View, Float> mViewProperty;

	private MotionProperty(@NonNull final Property<View, Float> viewProperty) {
		mViewProperty = viewProperty;
	}

	/**
	 * @return the view property that this {@link com.tumblr.backboard.MotionProperty}
	 * corresponds to
	 */
	@NonNull
	public Property<View, Float> getViewProperty() {
		return mViewProperty;
	}

	/**
	 * @param view
	 * 		the view to inspect
	 * @return the current value that this property represents on the given <code>View</code>.
	 */
	public float getValue(@Nullable final View view) {
		if (view != null) {
			return mViewProperty.get(view);
		}

		return 0;
	}

	/**
	 * @param event
	 * 		the event to inspect
	 * @return the current value that this property represents on the given
	 * <code>MotionEvent</code>.
	 */
	public float getValue(@Nullable final MotionEvent event) {
		if (event != null) {
			switch (this) {
			case X:
				return event.getX(0);
			case Y:
				return event.getY(0);
			default:
				return event.getX(0);
			}
		}

		return 0;
	}

	/**
	 * <i>Note that this method does not check the validity of</i> <code>index</code>.
	 *
	 * @param event
	 * 		the event to inspect
	 * @param index
	 * 		the historical index (in {@link MotionEvent#getHistoricalX(int)} or {@link MotionEvent#getHistoricalY(int)})
	 * @return the historical value that this property represents on the given
	 * <code>MotionEvent</code>.
	 */
	public float getHistoricalValue(@Nullable final MotionEvent event, final int index) {
		if (event != null) {
			switch (this) {
			case X:
				return event.getHistoricalX(index);
			case Y:
				return event.getHistoricalY(index);
			default:
				return 0;
			}
		}

		return 0;
	}

	/**
	 * <i>Note that this method does not check the validity of</i> <code>index</code>.
	 *
	 * @param event
	 * 		the event to inspect
	 * @return the oldest historical value that this property represents on the given
	 * <code>MotionEvent</code>.
	 */
	public float getOldestValue(final MotionEvent event) {
		return getHistoricalValue(event, 0);
	}

	/**
	 * @param view
	 * 		the view to inspect
	 * @return the offset from the center that this property represents on the given
	 * <code>View</code>, in pixels.
	 */
	public float getOffset(@Nullable final View view) {
		if (view != null) {
			switch (this) {
			case X:
				return -view.getWidth() / 2;
			case Y:
				return -view.getHeight() / 2;
			default:
				return -view.getWidth() / 2;
			}
		}

		return 0;
	}
}