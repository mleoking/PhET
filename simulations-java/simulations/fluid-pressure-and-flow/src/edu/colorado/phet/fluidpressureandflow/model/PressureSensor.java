package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.modules.intro.IntroModel;

/**
 * @author Sam Reid
 */
public class PressureSensor {
    private Property<Double> xProperty = new Property<Double>( 0.0 );
    private Property<Double> yProperty = new Property<Double>( 0.0 );
    private Property<Double> pressureProperty = new Property<Double>( 0.0 );
    private final IntroModel context;

    public PressureSensor( final IntroModel context ) {
        this.context = context;
        addPositionObserver( new SimpleObserver() {
            public void update() {
                pressureProperty.setValue( context.getPressureSensor( getPosition() ) );
            }
        } );
    }

    public double getPressure() {
        return pressureProperty.getValue();
    }

    public double getX() {
        return xProperty.getValue();
    }

    public double getY() {
        return yProperty.getValue();
    }

    public void setPosition( double x, double y ) {
        xProperty.setValue( x );
        yProperty.setValue( y );
    }

    public Point2D getPosition() {
        return new Point2D.Double( getX(), getY() );
    }

    public void addPositionObserver( SimpleObserver updatePosition ) {
        xProperty.addObserver( updatePosition );
        yProperty.addObserver( updatePosition );
    }

    public void addPressureObserver( SimpleObserver simpleObserver ) {
        pressureProperty.addObserver( simpleObserver );
    }
}
