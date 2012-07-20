// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

import static edu.colorado.phet.common.phetcommon.math.Vector2D.createPolar;

/**
 * Model of the hose which can be attached to the water tower so that the user can spray water from the ground
 *
 * @author Sam Reid
 */
public class Hose {

    //Angle in radians where 0 radians is to the right and PI/2 is straight up, should be between 0 and PI/2
    public final DoubleProperty angle = new DoubleProperty( Math.PI / 2 );

    //Location of the output point of the hose, starts on the ground
    public final Property<Double> y = new Property<Double>( 0.0 );

    //Flag to indicate whether the hose has been enabled by the user
    public final BooleanProperty enabled = new BooleanProperty( false );

    //Place where the hose attaches to the water tower
    public final ObservableProperty<Vector2D> attachmentPoint;

    //The place where the water comes out, tuned based on the curves in the HoseNode so the path is smooth and natural-looking
    public final CompositeProperty<Vector2D> outputPoint;

    //Width of the hole for the attachment point in meters
    public final double holeSize;

    //Height of the nozzle in meters, determined by setting up the view as desired then doing an inverse delta transform
    public static final double NOZZLE_HEIGHT = 2.85;

    public Hose( ObservableProperty<Vector2D> attachmentPoint, double holeSize ) {
        this.attachmentPoint = attachmentPoint;
        this.holeSize = holeSize;

        //The output point is at an arbitrary fixed x location, and the y-value is specified by another property
        //Determined value for x-position of output point through trial and error to find something far enough from the water tower that it is easy to control the hose
        //but close enough that you can still see and measure the parabolic flow of water
        outputPoint = new CompositeProperty<Vector2D>( new Function0<Vector2D>() {
            public Vector2D apply() {
                return new Vector2D( 17.275, y.get() );
            }
        }, y );
    }

    public void reset() {
        angle.reset();
        enabled.reset();
    }

    public Vector2D getNozzleInputPoint() {
        return createPolar( NOZZLE_HEIGHT, angle.get() + Math.PI ).plus( outputPoint.get() );
    }

    //Gets a unit vector pointing from the nozzle input point to the output point, used to place the arrow drag handles
    public Vector2D getUnitDirectionVector() {
        return outputPoint.get().minus( getNozzleInputPoint() ).getNormalizedInstance();
    }
}