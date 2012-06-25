// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
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

    private final EFACIntroModel model;

    public Thermometer( final EFACIntroModel model, ImmutableVector2D initialPosition ) {
        super( initialPosition );
        this.model = model;

        model.getClock().addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                sensedTemperature.set( model.getTemperatureAtLocation( position.get() ) );
            }
        } );
    }

    @Override public IUserComponent getUserComponent() {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        // Can't actually be set on anything, so returns null.
        return null;
    }
}
