// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

/**
 * Class that represents a thermometer in the model.  The thermometer has
 * only a point and a temperature in the model.  Its visual representation is
 * left entirely to the view.
 *
 * @author John Blanco
 */
public class Thermometer extends UserMovableModelElement {

    public final Property<Double> sensedTemperature = new Property<Double>( EFACConstants.ROOM_TEMPERATURE );
    public final Property<Color> sensedElementColor = new Property<Color>( PhetColorScheme.RED_COLORBLIND );
    private ElementFollower elementFollower = new ElementFollower( this.position );

    public Thermometer( final EFACIntroModel model, ImmutableVector2D initialPosition ) {
        super( initialPosition );

        // Update the sensed temperature at each clock tick.
        model.getClock().addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                sensedTemperature.set( model.getTemperatureAtLocation( position.get() ) );
                sensedElementColor.set( model.getElementColorAtLocation( position.get() ) );
            }
        } );

        // Monitor 'userControlled' in order to see when the user drops this
        // thermometer and determine whether or not it was dropped over
        // anything that it should stick to.
        userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( userControlled ) {
                    // Stop following anything.
                    elementFollower.stopFollowing();
                }
                else {
                    // The user has dropped this thermometer.  See if it was
                    // dropped over something that it should follow.
                    if ( model.getBeaker().getThermalContactArea().getBounds().contains( position.get().toPoint2D() ) ) {
                        // Stick to the beaker.
                        elementFollower.follow( model.getBeaker().position );
                    }
                    else {
                        for ( Block block : model.getBlockList() ) {
                            if ( block.getThermalContactArea().getBounds().contains( position.get().toPoint2D() ) ) {
                                // Follow this block.
                                elementFollower.follow( block.position );
                            }
                        }
                    }
                }
            }
        } );
    }

    @Override public void reset() {
        elementFollower.stopFollowing();
        super.reset();
    }

    @Override public IUserComponent getUserComponent() {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        // Can't actually be set on anything, so returns null.
        return null;
    }

    // Convenience class for sticking to model elements.
    private static class ElementFollower {
        private final Property<ImmutableVector2D> follower;
        private Property<ImmutableVector2D> locationBeingFollowed = null;
        private ImmutableVector2D offset = new ImmutableVector2D( 0, 0 );
        private final VoidFunction1<ImmutableVector2D> followerFunction = new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                follower.set( location.getAddedInstance( offset ) );
            }
        };

        private ElementFollower( Property<ImmutableVector2D> follower ) {
            this.follower = follower;
        }

        public void follow( Property<ImmutableVector2D> locationToFollow ) {
            if ( locationBeingFollowed != null ) {
                locationBeingFollowed.removeObserver( followerFunction );
            }
            offset = follower.get().getSubtractedInstance( locationToFollow.get() );
            locationToFollow.addObserver( followerFunction );
            locationBeingFollowed = locationToFollow;
        }

        public void stopFollowing() {
            if ( locationBeingFollowed != null ) {
                locationBeingFollowed.removeObserver( followerFunction );
                locationBeingFollowed = null;
            }
        }
    }
}
