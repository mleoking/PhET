// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class IntensityMeter {
    public final Property<ImmutableVector2D> sensorPosition;
    public final Property<ImmutableVector2D> bodyPosition;
    public final Property<Boolean> enabled = new Property<Boolean>( false );
    public final Property<Reading> reading = new Property<Reading>( Reading.MISS );
    private final ArrayList<Reading> rayReadings = new ArrayList<Reading>();

    public IntensityMeter( double sensorX, double sensorY, double bodyX, double bodyY ) {
        sensorPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( sensorX, sensorY ) );
        bodyPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( bodyX, bodyY ) );
    }

    public void translateSensor( Dimension2D delta ) {
        sensorPosition.setValue( sensorPosition.getValue().getAddedInstance( delta.getWidth(), delta.getHeight() ) );
    }

    public void translateBody( Dimension2D delta ) {
        bodyPosition.setValue( bodyPosition.getValue().getAddedInstance( delta.getWidth(), delta.getHeight() ) );
    }

    public Ellipse2D.Double getSensorShape() {
        double radius = 1.215E-6;//Fine tuned to match the given image
        return new Ellipse2D.Double( sensorPosition.getValue().getX() - radius, sensorPosition.getValue().getY() - radius, radius * 2, radius * 2 );
    }

    public void clearRayReadings() {
        rayReadings.clear();
        reading.setValue( Reading.MISS );
    }

    public void addRayReading( Reading r ) {
        rayReadings.add( r );
        ArrayList<Reading> hits = new ArrayList<Reading>();
        for ( Reading rayReading : rayReadings ) {
            if ( rayReading.isHit() ) {
                hits.add( rayReading );
            }
        }
        if ( hits.size() == 0 ) {
            reading.setValue( Reading.MISS );
        }
        else if ( hits.size() == 1 ) {
            reading.setValue( hits.get( 0 ) );
        }
        else {
            reading.setValue( Reading.MULTI );
        }
    }

    public void translateAll( Dimension2D dimension2D ) {
        translateBody( dimension2D );
        translateSensor( dimension2D );
    }
}
