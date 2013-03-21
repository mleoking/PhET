// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.intro.model.HorizontalSurface;
import edu.colorado.phet.energyformsandchanges.intro.model.TemperatureAndColor;
import edu.colorado.phet.energyformsandchanges.intro.model.UserMovableModelElement;

/**
 * Basic thermometer that senses temperature, has a position. The thermometer
 * has only a point and a temperature in the model.  Its visual representation
 * is left entirely to the view.
 *
 * @author John Blanco
 */
public class Thermometer extends UserMovableModelElement {

    public final Property<Double> sensedTemperature = new Property<Double>( EFACConstants.ROOM_TEMPERATURE );
    public final Property<Color> sensedElementColor = new Property<Color>( PhetColorScheme.RED_COLORBLIND );

    // Property that is used primarily to control visibility in the view.
    public final BooleanProperty active;

    /**
     * Constructor.
     */
    public Thermometer( ConstantDtClock clock, final ITemperatureModel model, Vector2D initialPosition, boolean initiallyActive ) {
        super( initialPosition );
        active = new BooleanProperty( initiallyActive );

        // Update the sensed temperature at each clock tick.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                TemperatureAndColor temperatureAndColor = model.getTemperatureAndColorAtLocation( position.get() );
                sensedTemperature.set( temperatureAndColor.temperature );
                sensedElementColor.set( temperatureAndColor.color );
            }
        } );
    }

    @Override public void reset() {
        active.reset();
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.thermometer;
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        // Doesn't have a bottom surface, and can't be set on anything.
        return null;
    }
}
