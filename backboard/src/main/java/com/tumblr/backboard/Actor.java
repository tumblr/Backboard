package com.tumblr.backboard;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.imitator.EventImitator;
import com.tumblr.backboard.imitator.Imitator;
import com.tumblr.backboard.imitator.MotionImitator;
import com.tumblr.backboard.performer.Performer;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the relationship between {@link com.tumblr.backboard.imitator.MotionImitator}s,
 * {@link com.facebook.rebound.Spring}s, and {@link com.tumblr.backboard.performer.Performer}s on a
 * single {@link android.view.View}.
 * <p>
 * This primarily exists to manage the {@link android.view.View.OnTouchListener} on the
 * {@link android.view.View}.
 * <p>
 * Created by ericleong on 5/20/14.
 */
public final class Actor {

	/**
	 * Distance in pixels that can be moved before a touch is no longer considered a "click".
	 */
	public static final int MAX_CLICK_DISTANCE = 10;

	/**
	 * Contains the imitators and listeners coupled to a single spring.
	 */
	public static final class Motion {
		@NonNull
		private final Spring spring;
		@NonNull
		private final EventImitator[] imitators;
		@NonNull
		private final Performer[] performers;
		@Nullable
		private final SpringListener[] springListeners;

		private Motion(@NonNull final Spring spring, @NonNull final EventImitator imitator, @NonNull final Performer[] performers,
		               @Nullable final SpringListener[] springListeners) {
			this(spring, new EventImitator[] { imitator }, performers, springListeners);
		}

		private Motion(@NonNull final Spring spring, @NonNull final Performer[] performers,
		               @Nullable final SpringListener[] springListeners) {
			this.imitators = new MotionImitator[0];
			this.performers = performers;
			this.spring = spring;
			this.springListeners = springListeners;
		}

		private Motion(@NonNull final Spring spring, @NonNull final EventImitator[] imitators, @NonNull final Performer[] performers,
		               @Nullable final SpringListener[] springListeners) {
			this.imitators = imitators;
			this.performers = performers;
			this.spring = spring;
			this.springListeners = springListeners;
		}

		@NonNull
		public Spring getSpring() {
			return spring;
		}

		@NonNull
		public EventImitator[] getImitators() {
			return imitators;
		}
	}

	@NonNull
	private final View mView;
	@NonNull
	private final List<Motion> mMotions;
	@NonNull
	private final MotionListener mMotionListener;
	@Nullable
	private final View.OnTouchListener mOnTouchListener;
	/**
	 * Allows the user to disable the motion listener.
	 */
	private boolean mMotionListenerEnabled;
	/**
	 * Prevent parent from intercepting touch events (useful when in lists).
	 */
	private boolean mRequestDisallowTouchEvent;

	private Actor(@NonNull final View view, @NonNull final List<Motion> motions,
	              @Nullable final View.OnTouchListener onTouchListener,
	              final boolean motionListenerEnabled, final boolean attachTouchListener,
	              final boolean requestDisallowTouchEvent) {
		mView = view;
		mMotions = motions;
		mOnTouchListener = onTouchListener;

		mMotionListener = new MotionListener();
		mMotionListenerEnabled = motionListenerEnabled;

		mRequestDisallowTouchEvent = requestDisallowTouchEvent;

		if (attachTouchListener) {
			view.setOnTouchListener(mMotionListener);
		}
	}

	@Nullable
	public View.OnTouchListener getOnTouchListener() {
		return mOnTouchListener;
	}

	@NonNull
	public View.OnTouchListener getMotionListener() {
		return mMotionListener;
	}

	@NonNull
	public View getView() {
		return mView;
	}

	@NonNull
	public List<Motion> getMotions() {
		return mMotions;
	}

	public boolean isTouchEnabled() {
		return mMotionListenerEnabled;
	}

	public void setTouchEnabled(final boolean enabled) {
		this.mMotionListenerEnabled = enabled;
	}

	/**
	 * Removes all spring listeners controlled by this {@link Actor}.
	 */
	public void removeAllListeners() {
		for (Motion motion : mMotions) {
			for (Performer performer : motion.performers) {
				motion.spring.removeListener(performer);
			}

			if (motion.springListeners != null) {
				for (SpringListener listener : motion.springListeners) {
					motion.spring.removeListener(listener);
				}
			}
		}
	}

	/**
	 * Adds all spring listeners back.
	 */
	public void addAllListeners() {
		for (Motion motion : mMotions) {
			for (Performer performer : motion.performers) {
				motion.spring.addListener(performer);
			}

			if (motion.springListeners != null) {
				for (SpringListener listener : motion.springListeners) {
					motion.spring.addListener(listener);
				}
			}
		}
	}

	/**
	 * Implements the builder pattern for {@link Actor}.
	 */
	public static class Builder {

		@NonNull
		private final View mView;
		@NonNull
		private final List<Motion> mMotions = new ArrayList<Motion>();
		@Nullable
		private View.OnTouchListener mOnTouchListener;
		@NonNull
		private final SpringSystem mSpringSystem;
		private boolean mMotionListenerEnabled = true;
		private boolean mAttachMotionListener = true;
		private boolean mRequestDisallowTouchEvent;
		private boolean mAttachSpringListeners = true;

		/**
		 * Animates the given view with the default {@link com.facebook.rebound.SpringConfig} and
		 * automatically creates a {@link com.facebook.rebound.SpringSystem}.
		 *
		 * @param springSystem
		 * 		the spring system to use
		 * @param view
		 * 		the view to animate
		 */
		public Builder(@NonNull final SpringSystem springSystem, @NonNull final View view) {
			mView = view;
			mSpringSystem = springSystem;
		}

		/**
		 * @param onTouchListener
		 * 		a touch listener to pass touch events to
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder onTouchListener(final View.OnTouchListener onTouchListener) {
			mOnTouchListener = onTouchListener;
			return this;
		}

		/**
		 * Uses the default {@link com.facebook.rebound.SpringConfig} to animate the view.
		 *
		 * @param properties
		 * 		the event fields to imitate and the view properties to animate.
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addTranslateMotion(final MotionProperty... properties) {
			return addMotion(mSpringSystem.createSpring(), properties);
		}

		/**
		 * Uses the default {@link com.facebook.rebound.SpringConfig} to animate the view.
		 *
		 * @param property
		 * 		the event field to imitate and the view property to animate.
		 * @param listener
		 * 		a listener to call
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addTranslateMotion(final MotionProperty property, final SpringListener listener) {
			return addMotion(mSpringSystem.createSpring(), Imitator.TRACK_ABSOLUTE,
					Imitator.FOLLOW_EXACT, new MotionProperty[] { property },
					new SpringListener[] { listener });
		}

		/**
		 * Uses the default {@link com.facebook.rebound.SpringConfig} to animate the view.
		 *
		 * @param trackStrategy
		 * 		the tracking behavior
		 * @param followStrategy
		 * 		the follow behavior
		 * @param properties
		 * 		the event fields to imitate and the view properties to animate.
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addTranslateMotion(final int trackStrategy, final int followStrategy,
		                                  final MotionProperty... properties) {
			return addMotion(mSpringSystem.createSpring(), trackStrategy, followStrategy,
					properties);
		}

		/**
		 * Uses the default {@link com.facebook.rebound.SpringConfig} to animate the view.
		 *
		 * @param trackStrategy
		 * 		the tracking behavior
		 * @param followStrategy
		 * 		the follow behavior
		 * @param restValue
		 * 		the rest value of the spring
		 * @param properties
		 * 		the event fields to imitate and the view properties to animate.
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addTranslateMotion(final int trackStrategy, final int followStrategy,
		                                  final int restValue,
		                                  final MotionProperty... properties) {
			return addMotion(mSpringSystem.createSpring(), trackStrategy, followStrategy,
					restValue, properties);
		}

		/**
		 * @param spring
		 * 		the underlying {@link com.facebook.rebound.Spring}.
		 * @param properties
		 * 		the event fields to imitate and the view properties to animate.
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final Spring spring, final MotionProperty... properties) {
			return addMotion(spring, Imitator.TRACK_ABSOLUTE, Imitator.FOLLOW_EXACT, properties);
		}

		/**
		 * @param spring
		 * 		the underlying {@link com.facebook.rebound.Spring}.
		 * @param trackStrategy
		 * 		the tracking behavior
		 * @param followStrategy
		 * 		the follow behavior
		 * @param properties
		 * 		the event fields to imitate and the view properties to animate.
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final Spring spring, final int trackStrategy, final int followStrategy,
		                         @NonNull final MotionProperty... properties) {

			mMotions.add(createMotionFromProperties(spring, properties, null, trackStrategy, followStrategy, 0));

			return this;
		}

		/**
		 * @param spring
		 * 		the underlying {@link com.facebook.rebound.Spring}.
		 * @param trackStrategy
		 * 		the tracking behavior
		 * @param followStrategy
		 * 		the follow behavior
		 * @param restValue
		 * 		the rest value
		 * @param properties
		 * 		the event fields to imitate and the view properties to animate.
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final Spring spring, final int trackStrategy, final int followStrategy,
		                         final int restValue, @NonNull final MotionProperty... properties) {

			mMotions.add(
					createMotionFromProperties(spring, properties, null, trackStrategy, followStrategy, restValue));

			return this;
		}

		/**
		 * @param spring
		 * 		the underlying {@link com.facebook.rebound.Spring}.
		 * @param trackStrategy
		 * 		the tracking behavior
		 * @param followStrategy
		 * 		the follow behavior
		 * @param restValue
		 * 		the rest value
		 * @param property
		 * 		the event fields to imitate and the view property to animate.
		 * @param springListener
		 * 		a spring listener to attach to the spring
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final Spring spring, final int trackStrategy, final int followStrategy,
		                         final int restValue, final MotionProperty property, @Nullable final SpringListener springListener) {

			mMotions.add(
					createMotionFromProperties(spring, new MotionProperty[] { property },
							new SpringListener[] { springListener }, trackStrategy, followStrategy, restValue));

			return this;
		}

		/**
		 * @param spring
		 * 		the underlying {@link com.facebook.rebound.Spring}.
		 * @param trackStrategy
		 * 		the tracking behavior
		 * @param followStrategy
		 * 		the follow behavior
		 * @param properties
		 * 		the event fields to imitate and the view properties to animate.
		 * @param springListeners
		 * 		an array of spring listeners to attach to the spring
		 * @return this builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final Spring spring, final int trackStrategy, final int followStrategy,
		                         @NonNull final MotionProperty[] properties, final SpringListener[] springListeners) {

			mMotions.add(
					createMotionFromProperties(spring, properties, springListeners, trackStrategy, followStrategy, 0));

			return this;
		}

		/**
		 * Uses a default {@link com.facebook.rebound.SpringConfig}.
		 *
		 * @param eventImitator
		 * 		maps an event to a {@link com.facebook.rebound.Spring}
		 * @param viewProperties
		 * 		the {@link android.view.View} property to animate
		 * @return the builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final EventImitator eventImitator,
		                         @NonNull final Property<View, Float>... viewProperties) {
			final Performer[] performers = new Performer[viewProperties.length];

			for (int i = 0; i < viewProperties.length; i++) {
				performers[i] = new Performer(viewProperties[i]);
			}

			return addMotion(mSpringSystem.createSpring(), eventImitator, performers);
		}

		/**
		 * Uses a default {@link com.facebook.rebound.SpringConfig}.
		 *
		 * @param eventImitator
		 * 		maps an event to a {@link com.facebook.rebound.Spring}
		 * @param performers
		 * 		map the {@link com.facebook.rebound.Spring} to a
		 * 		{@link android.view.View}
		 * @return the builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final EventImitator eventImitator, final Performer... performers) {
			return addMotion(mSpringSystem.createSpring(), eventImitator, performers);
		}

		/**
		 * @param spring
		 * 		the underlying {@link com.facebook.rebound.Spring}.
		 * @param eventImitator
		 * 		maps an event to a {@link com.facebook.rebound.Spring}
		 * @param performers
		 * 		map the {@link com.facebook.rebound.Spring} to a
		 * 		{@link android.view.View}
		 * @return the builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final Spring spring, @NonNull final EventImitator eventImitator,
		                         @NonNull final Performer... performers) {

			final Motion motion = new Motion(spring, eventImitator, performers, null);

			// connect actors
			motion.imitators[0].setSpring(motion.spring);

			for (Performer performer : motion.performers) {
				performer.setTarget(mView);
			}

			mMotions.add(motion);

			return this;
		}

		/**
		 * @param spring
		 * 		the underlying {@link com.facebook.rebound.Spring}.
		 * @param eventImitator
		 * 		maps an event to a {@link com.facebook.rebound.Spring}
		 * @param performers
		 * 		map the {@link com.facebook.rebound.Spring} to a
		 * 		{@link android.view.View}
		 * @param springListeners
		 * 		additional listeners to attach
		 * @return the builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final Spring spring, @NonNull final EventImitator eventImitator,
		                         @NonNull final Performer[] performers, final SpringListener[] springListeners) {

			// create struct
			final Motion motion = new Motion(spring, eventImitator, performers, springListeners);

			// connect actors
			motion.imitators[0].setSpring(motion.spring);

			for (Performer performer : motion.performers) {
				performer.setTarget(mView);
			}

			mMotions.add(motion);

			return this;
		}

		/**
		 * @param motionImitator
		 * 		maps an event to a {@link com.facebook.rebound.Spring}
		 * @param viewProperty
		 * 		the {@link android.view.View} property to animate
		 * @param springListener
		 * 		additional listener to attach
		 * @return the builder for chaining
		 */
		@NonNull
		public Builder addMotion(@NonNull final MotionImitator motionImitator,
		                         @NonNull final Property<View, Float> viewProperty,
		                         final SpringListener springListener) {

			return addMotion(mSpringSystem.createSpring(), motionImitator,
					new Performer[] { new Performer(viewProperty) },
					new SpringListener[] { springListener });
		}

		/**
		 * @return flag to tell the attached {@link android.view.View.OnTouchListener} to call
		 * {@link android.view.ViewParent#requestDisallowInterceptTouchEvent(boolean)} with
		 * <code>true</code>.
		 */
		@NonNull
		public Builder requestDisallowTouchEvent() {
			mRequestDisallowTouchEvent = true;
			return this;
		}

		/**
		 * A flag to tell this {@link Actor} not to attach the touch listener to the view.
		 *
		 * @return the builder for chaining
		 */
		@NonNull
		public Builder dontAttachMotionListener() {
			mAttachMotionListener = false;
			return this;
		}

		/**
		 * A flag to tell this builder not to attach the spring listeners to the spring.
		 * They can be added with {@link Actor#addAllListeners()}.
		 *
		 * @return the builder for chaining
		 */
		@NonNull
		public Builder dontAttachSpringListeners() {
			mAttachSpringListeners = false;
			return this;
		}

		/**
		 * Creations a new motion object.
		 *
		 * @param spring
		 * 		the spring to use
		 * @param motionProperties
		 * 		the properties of the event to track
		 * @param springListeners
		 * 		additional spring listeners to add
		 * @param trackStrategy
		 * 		the tracking strategy
		 * @param followStrategy
		 * 		the follow strategy
		 * @param restValue
		 * 		the spring rest value
		 * @return a motion object
		 */
		@Nullable
		private Motion createMotionFromProperties(@NonNull final Spring spring,
		                                          @NonNull final MotionProperty[] motionProperties,
		                                          @Nullable final SpringListener[] springListeners,
		                                          final int trackStrategy, final int followStrategy,
		                                          final int restValue) {

			final MotionImitator[] motionImitators = new MotionImitator[motionProperties.length];
			final Performer[] performers = new Performer[motionProperties.length];

			for (int i = 0; i < motionProperties.length; i++) {

				final MotionProperty property = motionProperties[i];

				motionImitators[i] = new MotionImitator(spring, property, restValue, trackStrategy, followStrategy);
				performers[i] = new Performer(mView, property.getViewProperty());
			}

			return new Motion(spring, motionImitators, performers, springListeners);
		}

		/**
		 * @return Builds the {@link Actor}.
		 */
		@NonNull
		public Actor build() {
			// make connections

			final Actor actor = new Actor(mView, mMotions, mOnTouchListener, mMotionListenerEnabled, mAttachMotionListener,
					mRequestDisallowTouchEvent);

			if (mAttachSpringListeners) {
				actor.addAllListeners();
			}

			return actor;
		}
	}

	private class MotionListener implements View.OnTouchListener {
		@Override
		@SuppressLint("ClickableViewAccessibility")
		public boolean onTouch(@NonNull final View v, @NonNull final MotionEvent event) {

			final boolean retVal;

			if (!mMotionListenerEnabled || mMotions.isEmpty()) {

				if (mOnTouchListener != null) {
					retVal = mOnTouchListener.onTouch(v, event);
				} else {
					retVal = false;
				}

				return retVal;
			}

			for (Motion motion : mMotions) {
				for (EventImitator imitator : motion.imitators) {
					imitator.imitate(v, event);
				}
			}

			if (mOnTouchListener != null) {
				retVal = mOnTouchListener.onTouch(v, event);
			} else {
				retVal = true;
			}

			if (mRequestDisallowTouchEvent) {
				// prevents parent from scrolling or otherwise stealing touch events
				v.getParent().requestDisallowInterceptTouchEvent(true);
			}

			if (v.isClickable()) {
				if (event.getEventTime() - event.getDownTime()
						> ViewConfiguration.getLongPressTimeout()) {
					v.setPressed(false);

					return true;
				}

				if (event.getHistorySize() > 0) {
					final float deltaX = event.getHistoricalX(event.getHistorySize() - 1) - event.getX();
					final float deltaY = event.getHistoricalY(event.getHistorySize() - 1) - event.getY();

					// if user has moved too far, it is no longer a click
					final boolean removeClickState = Math.pow(deltaX, 2) + Math.pow(deltaY, 2)
							> Math.pow(MAX_CLICK_DISTANCE, 2);

					v.setPressed(!removeClickState);

					return removeClickState;
				} else {
					return false;
				}
			}

			return retVal;
		}
	}
}
