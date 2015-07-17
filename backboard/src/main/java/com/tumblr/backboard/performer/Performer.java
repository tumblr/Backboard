package com.tumblr.backboard.performer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Property;
import android.view.View;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;

/**
 * Directly maps the motion of a {@link com.facebook.rebound.Spring} to a
 * {@link android.util.Property} on a {@link android.view.View}.
 * <p/>
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
	public Performer(@NonNull Property<View, Float> property) {
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
	public Performer(@Nullable View target, @NonNull Property<View, Float> property) {
		this.mTarget = target;
		this.mProperty = property;
	}

	@Nullable
	public View getTarget() {
		return mTarget;
	}

	public void setTarget(@Nullable View target) {
		this.mTarget = target;
	}

	@NonNull
	public Property getProperty() {
		return mProperty;
	}

	public void setProperty(@NonNull Property<View, Float> property) {
		this.mProperty = property;
	}

	@Override
	public void onSpringUpdate(@NonNull Spring spring) {
		if (mProperty != null && mTarget != null) {
			mProperty.set(mTarget, (float) spring.getCurrentValue());
		}
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
