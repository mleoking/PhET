// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * This is a property that can be rewound, and when rewound it goes back
 * to the value that was last set while the clock was paused.
 *
 * @author Sam Reid
 */
public class ClockRewindProperty<T> extends Property<T> {
    private final Property<Boolean> clockPaused;
    private T rewindValue;
    private Property<Boolean> different; // true when the rewind point value is different than the property's value

    public ClockRewindProperty( Property<Boolean> clockPaused, T value ) {
        super( value );
        this.clockPaused = clockPaused;
        this.rewindValue = value;

        different = new Property<Boolean>( !equalsRewindPoint() );
    }

    @Override
    public void setValue( T value ) {
        super.setValue( value );
        if ( clockPaused.getValue() ) {
            rewindValue = value;
        }
        different.setValue( !equalsRewindPoint() );
    }

    public boolean equalsRewindPoint() {
        return rewindValue.equals( getValue() );
    }

    public void rewind() {
        setValue( rewindValue );
    }

    public Property<Boolean> different() {
        return different;
    }

    public T getRewindValue() {
        return rewindValue;
    }

    /**
     * Makes this public for use in gravity and orbits.
     *
     * @return
     */
    @Override
    public T getInitialValue() {
        return super.getInitialValue();
    }
}
