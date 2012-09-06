// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

/**
 * Class that represents the sun (as an energy source) in the model.  This
 * includes the clouds that can block the sun's rays.
 *
 * @author John Blanco
 */
public class Sun extends EnergySource {

    public static final double RADIUS = 0.02; // In meters, apparent size, not (obviously) actual size.
    public static final Vector2D OFFSET_TO_CENTER_OF_SUN = new Vector2D( -0.05, 0.12 );

    // Clouds that can potentially block the sun's rays.  The positions are
    // set so that they appear between the sun and the solar panel, and must
    // not overlap with one another.
    public final List<Cloud> clouds = new ArrayList<Cloud>() {{
//        add( new Cloud( new Vector2D( 0.01, 0.12 ) ) );  // TODO: For testing, immediately to right of sun.
//        add( new Cloud( new Vector2D( -0.04, 0.07 ) ) );   // TODO: For testing, immediately below the sun.
        add( new Cloud( new Vector2D( 0.01, 0.11 ) ) );
        add( new Cloud( new Vector2D( 0.0, 0.09 ) ) );
        add( new Cloud( new Vector2D( -0.01, 0.07 ) ) );
    }};

    public final Property<Double> cloudiness = new Property<Double>( 0.0 );

    // Energy production per square meter of the Earth's surface.
    private static final double ENERGY_PRODUCTION_RATE = 1000; // In joules/second per square meter of Earth.

    //TODO: This, and all image lists, should be removed once the prototypes have all been replaced.
    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
    }};

    protected Sun() {
        super( EnergyFormsAndChangesResources.Images.SUN_ICON, IMAGE_LIST );
        cloudiness.addObserver( new VoidFunction1<Double>() {
            public void apply( Double cloudiness ) {
                for ( Cloud cloud : clouds ) {
                    cloud.existenceStrength.set( cloudiness );
                }
            }
        } );
    }

    @Override public Energy stepInTime( double dt ) {
        double energyProduced = active ? ENERGY_PRODUCTION_RATE * ( 1 - cloudiness.get() ) * dt : 0;
        return new Energy( Energy.Type.SOLAR, energyProduced );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSunButton;
    }
}
