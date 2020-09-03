package com.tumblr.backboard.performer;

import android.util.Property;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;

/**
 * Directly maps the motion of a {@link com.facebook.rebound.Spring} to a
 * {@link android.util.Property} on a {@link android.view.View}.
 * <p>
 * Created by ericleong on 5/19/14.
 */
public class Performer implements SpringListener {

	/**
	 * The view to modify.
	 */
	@Nullable
	protected View mTarget;
	/**
	 * The property of the view to modify.
	 */
	@NonNull
	protected Property<View, Float> mProperty;

	/**
	 * Constructor. Note that a {@link View} must be specified by {@link #setTarget(View)}.
	 *
	 * @param property
	 * 		the view property to modify.
	 */
	public Performer(@NonNull final Property<View, Float> property) {
		this(null, property);
	}

	/**
	 * Constructor.
	 *
	 * @param target
	 * 		the view to modify.
	 * @param property
	 * 		the view property to modify.
	 */
	public Performer(@Nullable final View target, @NonNull final Property<View, Float> property) {
		this.mTarget = target;
		this.mProperty = property;
	}

	@Nullable
	public View getTarget() {
		return mTarget;
	}

	public void setTarget(@Nullable final View target) {
		this.mTarget = target;
	}

	@NonNull
	public Property getProperty() {
		return mProperty;
	}

	public void setProperty(@NonNull final Property<View, Float> property) {
		this.mProperty = property;
	}

	@Override
	public void onSpringUpdate(@NonNull final Spring spring) {
		if (mProperty != null && mTarget != null) {
			mProperty.set(mTarget, (float) spring.getCurrentValue());
		}
	}

	@Override
	public void onSpringAtRest(final Spring spring) {

	}

	@Override
	public void onSpringActivate(final Spring spring) {

	}

	@Override
	public void onSpringEndStateChange(final Spring spring) {

	}
}
