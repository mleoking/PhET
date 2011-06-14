// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * This is a property that can be rewound, and when rewound it goes back
 * to the value that was last set while the clock was paused.
 *
 * @author Sam Reid
 */
public class RewindableProperty<T> extends Property<T> {
    private final Property<Boolean> clockPaused;
    private final Property<Boolean> stepping;//if the clock is paused and the user pressed 'step', do not store a rewind point
    private final Property<Boolean> rewinding;//if the clock is paused and the user pressed 'rewind', do not store a rewind point
    private T rewindValue;//the "initial condition" tha the property can be rewound to
    private Property<Boolean> different; // true when the rewind point value is different than the property's value
    private ArrayList<VoidFunction0> rewindValueChangedListeners = new ArrayList<VoidFunction0>();

    public RewindableProperty( Property<Boolean> clockPaused,
                               Property<Boolean> isStepping,
                               Property<Boolean> isRewinding,
                               T value ) {
        super( value );
        this.clockPaused = clockPaused;
        stepping = isStepping;
        rewinding = isRewinding;
        this.rewindValue = value;

        different = new Property<Boolean>( !equalsRewindPoint() );
    }

    @Override
    public void set( T value ) {
        super.set( value );
        //If the user changed the initial conditions (as opposed to the state changing through model stepping), then store the new initial conditions, which can be rewound to
        if ( clockPaused.get() && !stepping.get() && !rewinding.get() ) {
            storeRewindValueNoNotify();
            for ( VoidFunction0 rewindValueChangedListener : rewindValueChangedListeners ) {
                rewindValueChangedListener.apply();
            }
        }
        different.set( !equalsRewindPoint() );
    }

    //Store the new value as the initial condition which can be rewound to.  We have to skip notifications sometimes or the wrong initial conditions get stored.
    public void storeRewindValueNoNotify() {
        rewindValue = get();
    }

    //Adds a listener that is notified when the user changes the initial conditions, which can be rewound to
    public void addRewindValueChangeListener( VoidFunction0 listener ) {
        rewindValueChangedListeners.add( listener );
    }

    public boolean equalsRewindPoint() {
        return rewindValue.equals( get() );
    }

    public void rewind() {
        set( rewindValue );
    }

    //Convenient access to whether the value has deviated from the initial condition
    public Property<Boolean> different() {
        return different;
    }

    /*
     *Makes this public for use in gravity and orbits.
     */
    @Override
    public T getInitialValue() {
        return super.getInitialValue();
    }
}
