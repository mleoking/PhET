// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

//REVIEW When I first encountered a property of this type, I had no idea what it did without examining the doc here. How about renaming to RewindableProperty?

/**
 * This is a property that can be rewound, and when rewound it goes back
 * to the value that was last set while the clock was paused.
 *
 * @author Sam Reid
 */
public class ClockRewindProperty<T> extends Property<T> {
    private final Property<Boolean> clockPaused;
    private final Property<Boolean> stepping;//if the clock is paused and the user pressed 'step', do not store a rewind point
    private final Property<Boolean> rewinding;//if the clock is paused and the user pressed 'rewind', do not store a rewind point
    private T rewindValue;
    private Property<Boolean> different; // true when the rewind point value is different than the property's value
    private ArrayList<VoidFunction0> rewindValueChangedListeners = new ArrayList<VoidFunction0>();

    public ClockRewindProperty( Property<Boolean> clockPaused,
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
    public void setValue( T value ) {
        super.setValue( value );
        //REVIEW not clear how this expression relates to the state of the clock, probably because I don't fully understand the semantics of stepping and rewinding, which are clock actions, not clock states.
        if ( clockPaused.getValue() && !stepping.getValue() && !rewinding.getValue() ) {
            storeRewindValueNoNotify();
            for ( VoidFunction0 rewindValueChangedListener : rewindValueChangedListeners ) {
                rewindValueChangedListener.apply();
            }
        }
        different.setValue( !equalsRewindPoint() );
    }

    //REVIEW doc. smells like a hack. why is this necessary?
    public void storeRewindValueNoNotify() {
        rewindValue = getValue();
    }

    //REVIEW I suspect that the need for a listener instead of a "Property<T> rewindValueProperty" is related to the need for storeRewindValueNoNotify. Please doc.
    public void addRewindValueChangeListener( VoidFunction0 listener ) {
        rewindValueChangedListeners.add( listener );
    }

    public boolean equalsRewindPoint() {
        return rewindValue.equals( getValue() );
    }

    public void rewind() {
        setValue( rewindValue );
    }

    //REVIEW why do you need both this and equalsRewindPoint? One of these is redundant.
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
