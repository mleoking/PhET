// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Class that represents a chunk of energy in the view.
 *
 * @author John Blanco
 */
public class EnergyChunk {

    public static final double FADE_RATE = 1; // Proportion per second.

    public final Property<ImmutableVector2D> position;
    public final BooleanProperty visible;

    // Strength of existence, used for fading in and out.  Range is from 0 to 1.
    private final Property<Double> existenceStrength = new Property<Double>( 1.0 );

    // Fade state, so that we know which way it is going.
    private FadeState fadeState = FadeState.FULLY_FADED_IN;

    public EnergyChunk( ConstantDtClock clock, double x, double y, BooleanProperty visibilityControl, boolean fadeIn ) {
        this( clock, new ImmutableVector2D( x, y ), visibilityControl, fadeIn );
    }

    public EnergyChunk( ConstantDtClock clock, ImmutableVector2D initialPosition, BooleanProperty visibilityControl, boolean fadeIn ) {
        this.position = new Property<ImmutableVector2D>( initialPosition );
        this.visible = visibilityControl;
        if ( fadeIn ) {
            fadeState = FadeState.FADING_IN;
            existenceStrength.set( 0.0 );
        }

        // Hook up the clock.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    private void stepInTime( double dt ) {
        switch( fadeState ) {
            case FADING_IN:
                existenceStrength.set( Math.max( existenceStrength.get() + FADE_RATE * dt, 1 ) );
                if ( existenceStrength.get() == 1 ) {
                    fadeState = FadeState.FULLY_FADED_IN;
                }
                break;
            case FADING_OUT:
                existenceStrength.set( Math.min( existenceStrength.get() - FADE_RATE * dt, 0 ) );
                if ( existenceStrength.get() == 0 ) {
                    fadeState = FadeState.FULLY_FADED_OUT;
                }
                break;
            case FULLY_FADED_IN:
                // State consistency checking.
                assert existenceStrength.get() == 1;
                break;
            case FULLY_FADED_OUT:
                // State consistency checking.
                assert existenceStrength.get() == 0;
                break;
        }
    }

    public void translate( ImmutableVector2D movement ) {
        position.set( position.get().getAddedInstance( movement ) );
    }

    public ObservableProperty<Double> getExistenceStrength() {
        return existenceStrength;
    }
}
