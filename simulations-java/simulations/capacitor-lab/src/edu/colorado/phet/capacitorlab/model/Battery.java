// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.shapes.BatteryShapeFactory;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Simple model of a DC battery.
 * Origin is at the geometric center of the battery's body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Battery {

    // size of the associated image file, determined by visual inspection
    private static final PDimension BODY_SIZE = new PDimension( 0.0065, 0.01425 ); // dimensions of the rectangle that bounds the battery image

    /*
     * Positive terminal is part of the image file.
     * The terminal is a cylinder, whose dimensions were determined by visual inspection.
     * The origin of the terminal is at the center of the cylinder's top.
     */
    private static final PDimension POSITIVE_TERMINAL_ELLIPSE_SIZE = new PDimension( 0.0025, 0.0005 );
    private static final double POSITIVE_TERMINAL_CYLINDER_HEIGHT = 0.0009;
    private static final double POSITIVE_TERMINAL_Y_OFFSET = -( BODY_SIZE.getHeight() / 2 ) + 0.000505;

    /*
     * Negative terminal is part of the image file.
     * The terminal is an ellipse, whose dimension were determined by visual inspection.
     * The origin of the terminal is at the center of the ellipse.
     */
    private static final PDimension NEGATIVE_TERMINAL_ELLIPSE_SIZE = new PDimension( 0.0035, 0.0009 ); // dimension of the ellipse that defines the negative terminal
    private static final double NEGATIVE_TERMINAL_Y_OFFSET = -( BODY_SIZE.getHeight() / 2 ) + 0.00105; // center of the negative terminal, when it's the top terminal

    // immutable properties
    private final Point3D location;
    private final BatteryShapeFactory shapeFactory;

    // settable properties
    private final Property<Double> voltageProperty;

    // derived properties
    private final Property<Polarity> polarityProperty;

    public Battery( Point3D location, double voltage, CLModelViewTransform3D mvt ) {

        this.location = new Point3D.Double( location.getX(), location.getY(), location.getZ() );
        this.voltageProperty = new Property<Double>( voltage );
        this.polarityProperty = new Property<Polarity>( getPolarity( voltage ) );
        this.shapeFactory = new BatteryShapeFactory( this, mvt );

        voltageProperty.addObserver( new SimpleObserver() {
            public void update() {
                setPolarity( getPolarity( getVoltage() ) );
            }
        } );
    }

    public void reset() {
        voltageProperty.reset();
    }

    public Point3D getLocationReference() {
        return location;
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public double getZ() {
        return location.getZ();
    }

    /**
     * Gets the battery voltage.
     * (design doc symbol: V_battery)
     *
     * @param voltage
     */
    public void setVoltage( double voltage ) {
        voltageProperty.setValue( voltage );
    }

    public double getVoltage() {
        return voltageProperty.getValue();
    }

    public void addVoltageObserver( SimpleObserver o ) {
        voltageProperty.addObserver( o );
    }

    private void setPolarity( Polarity polarity ) {
        polarityProperty.setValue( polarity );
    }

    public Polarity getPolarity() {
        return polarityProperty.getValue();
    }

    private static Polarity getPolarity( double voltage ) {
        return ( voltage >= 0 ) ? Polarity.POSITIVE : Polarity.NEGATIVE;
    }

    public void addPolarityObserver( SimpleObserver o ) {
        polarityProperty.addObserver( o );
    }

    public boolean intersectsTopTerminal( Shape shape ) {
        return ShapeUtils.intersects( shapeFactory.createTopTerminalShape(), shape );
    }

    public BatteryShapeFactory getShapeFactory() {
        return shapeFactory;
    }

    public Dimension2D getBodySizeReference() {
        return BODY_SIZE;
    }

    public double getPositiveTerminalCylinderHeight() {
        return POSITIVE_TERMINAL_CYLINDER_HEIGHT;
    }

    public Dimension2D getPositiveTerminalEllipseSize() {
        return POSITIVE_TERMINAL_ELLIPSE_SIZE;
    }

    public Dimension2D getNegativeTerminalSizeReference() {
        return NEGATIVE_TERMINAL_ELLIPSE_SIZE;
    }

    /**
     * Gets the offset of the top terminal from the battery's origin, in model coordinates (meters).
     * This offset depends on the polarity.
     * @return
     */
    public double getTopTerminalYOffset() {
        if ( getPolarity() == Polarity.POSITIVE ) {
            return POSITIVE_TERMINAL_Y_OFFSET;
        }
        else {
            return NEGATIVE_TERMINAL_Y_OFFSET;
        }
    }

    /**
     * Gets the offset of the bottom terminal from the battery's origin, in model coordinates (meters).
     * We don't need to account for the polarity since the bottom terminal is never visible.
     * @return
     */
    public double getBottomTerminalYOffset() {
        return BODY_SIZE.getHeight() / 2;
    }
}
