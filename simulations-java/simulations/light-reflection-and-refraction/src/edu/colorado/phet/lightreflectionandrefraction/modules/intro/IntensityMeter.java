// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class IntensityMeter {
    public final Property<ImmutableVector2D> sensorPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    public final Property<ImmutableVector2D> bodyPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    public final Property<Boolean> enabled = new Property<Boolean>( false );

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
}
