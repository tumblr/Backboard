![the backboard icon](backboard-example/src/main/res/mipmap-xxhdpi/ic_launcher.png?raw=true)

# Backboard

[![CircleCI](https://circleci.com/gh/tumblr/Backboard/tree/master.svg?style=svg)](https://circleci.com/gh/tumblr/Backboard/tree/master)

A motion-driven animation framework for Android.

`backboard` is a framework on top of [rebound](http://facebook.github.io/rebound/) that makes it easier to use by coupling it to views and motions.

`backboard-example` is an Android app with a few demos of animations made possible by Backboard.

[Javadoc](http://tumblr.github.io/Backboard/javadoc/)

![follow animation](../screenshots/screenshots/follow.gif?raw=true)
![bloom animation](../screenshots/screenshots/bloom.gif?raw=true)
![scale animation](../screenshots/screenshots/scale.gif?raw=true)

## Table of Contents

* [Usage](#usage)
* [Getting Started](#getting-started)
    * [Performers](#performers)
    * [Imitators](#imitators)
    * [Actors](#actors)
* [Dependencies](#dependencies)
* [Contact](#contact)
* [License](#license)

## Usage

Update your `build.gradle` with

```groovy
dependencies {
   compile 'com.facebook.rebound:rebound:0.3.8'
   compile 'com.tumblr:backboard:0.1.0'
}
```

## Getting Started

Backboard is a framework on top of [rebound](http://facebook.github.io/rebound/) that manages how `Springs` are used and simplifies the
most common use cases:

- Actions, such as `MotionEvents`, are mapped to `Springs` via `Imitators`.
- `Springs` are mapped to `Views` and view properties via `Performers`.

In addition, an `Actor` wraps the above objects and provides a simple interface for mapping touch motion to a view's position - dragging.

### Performers

A `Performer` takes the current value of a `Spring` and sets it as the value of a view property.
```Java
Spring bounce = SpringSystem.create().createSpring();
Performer xMotion = new Performer(view, View.TRANSLATION_X);
bounce.addListener(xMotion);
```
for those saving screen space, a [fluent interface](http://en.wikipedia.org/wiki/Fluent_interface) is available:
```Java
Spring bounce = SpringSystem.create().createSpring().addListener(new Performer(view, View.TRANSLATION_X));
```

### Imitators

An `Imitator` constantly perturbs the `Spring` it is attached to. This perturbation can originate a variety of sources:

1. A `MotionEvent`, where the `Spring` can change based on the action (`ACTION_DOWN`, `ACTION_UP`), or imitate a property (`x`, `y`, etc.). These are called `EventImitators`.
2. Another `Spring`, which leads to results similar to springs being chained together. These are called `SpringImitators`.


#### Imitating Touch

An `EventImitator` primarily operates with `OnTouchListeners`. The simplest example is a `ToggleImitator`, which toggles between two different values depending on the touch state:
```Java
view.setOnTouchListener(new ToggleImitator(spring, 0, 1));
```
when the user touches the view, a value of `1` is set on the spring, and when the user releases, a value of `0` is set.

#### Imitating Motion

A `MotionImitator` is a special type of `EventImitator` that maps _x_ and _y_ movement to a spring. This is done with `MotionProperty` enums, which specifies which methods to call in a `MotionEvent` object. For example, `MotionProperty.X.getValue(MotionEvent)` calls `event.getX()`. It also specifies the view property to animate - `MotionEvent.X.getViewProperty()` corresponds to `View.TRANSLATION_X`. This is useful for the `Actor` builder later on. In addition, tracking and following strategies allow for customization of how the event value is mapped to the spring.

##### Tracking Strategies

Two tracking strategies are available to configure how an imitator tracks its imitatee.

* `TRACK_ABSOLUTE` maps the imitatee value directly to the spring.
* `TRACK_DELTA` maps the change in the imitatee value (relative to the initial touch) to the spring.

##### Follow Strategies

Two follow strategies are available to configure how the spring is updated.

* `FOLLOW_EXACT` maps the imitatee value directly to the current and end value of the spring.
* `FOLLOW_SPRING` maps the imitatee value to the end value of the spring (which allows the spring
 to overshoot the current position)

#### Imitating Springs

A `SpringImitator` is also a `SpringListener`. When the `Spring` it is imitating updates, it updates the end value of the `Spring` it is controlling. Usage is simple:
```Java
SpringSystem springSystem = SpringSystem.create();

Spring leader = springSystem.createSpring();
Spring follower = springSystem.createSpring();

SpringImitator follow = new SpringImitator(follower);
leader.addListener(follow);
```

### Actors

Even though backboard reduces a significant amount of boilerplate code, the `Actor` class further simplifes view motion by connecting each component together. It also manages a `View.onTouchListener()` (a `MotionListener`), which it attaches to the `View` automatically (this can be disabled). Here is how to create one:
```Java
Actor actor = new Actor.Builder(SpringSystem.create(), view)
  .addTranslateMotion(MotionProperty.X)
  .build();
```
in two dimensions:
```Java
Actor actor = new Actor.Builder(SpringSystem.create(), view)
  .addTranslateMotion(MotionProperty.X)
  .addTranslateMotion(MotionProperty.Y)
  .build();
```
Two calls to `addTranslateMotion(MotionProperty)` are needed because each axis is independent of the other. The builder will create an `OnTouchListener` and attach it to the `View`, as well as a `Spring` with the default settings. A `Performer` is also created and attached to the `Spring`. When there is a touch event, it is passed to the `MotionImitator`, which perturbs the spring, which moves the view.

It is also possible to supply your own `SpringSystem`, `Spring`, `MotionImitator` and `Performer`, and the builder will properly connect them. The first example above can also be expressed as:
```Java
SpringSystem springSystem = SpringSystem.create();
Spring spring = springSystem.createSpring();

Actor verbose = new Actor.Builder(springSystem, view)
 .addMotion(spring, new MotionImitator(spring, MotionProperty.X),
                    new Performer(view, View.TRANSLATION_X)
 .build();
```

The `View` can be also left out of the constructor of the `Performer` and the `Spring` out of the `MotionImitator` (using the default `SpringConfig`), since the builder will connect them.
```Java
Actor walk = new Actor.Builder(SpringSystem.create(), walker)
  .addMotion(
    new MotionImitator(MotionProperty.X),
    new Performer(View.TRANSLATION_X))
  .build();
```
which can be further simplified to
```Java
Actor run = new Actor.Builder(SpringSystem.create(), runner).addMotion(MotionProperty.X, View.TRANSLATION_X).build();
```
and for more sugar, the previous case:
```Java
Actor bolt = new Actor.Builder(SpringSystem.create(), bolter).addTranslateMotion(MotionProperty.X).build();
```

#### Actor Options

- `requestDisallowTouchEvent()` causes the `Actor` to call `ViewParent.requestDisallowTouchEvent(true)` which is helpful when the view is inside a `ListView` or another view that captures touch events.
- `dontAttachMotionListener()` tells the builder to not attach the `MotionListener` to the `View`, which is useful when you want to attach your own `OnTouchListener` to the view.

## Dependencies

* [rebound](http://facebook.github.io/rebound/)

## Contact

* [Eric Leong](mailto:ericleong@tumblr.com)

## License

Copyright 2015-2016 Tumblr, Inc.

Licensed under the Apache License, Version 2.0 (the “License”); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0).

> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an “AS IS” BASIS, WITHOUT
> WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
> License for the specific language governing permissions and limitations under
> the License.
