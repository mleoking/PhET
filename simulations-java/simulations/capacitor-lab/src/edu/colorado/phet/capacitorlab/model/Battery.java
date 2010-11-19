/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;

import edu.colorado.phet.capacitorlab.shapes.BatteryShapeFactory;
import edu.colorado.phet.capacitorlab.util.ShapeUtils;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Simple model of a DC battery.
 * Origin is at the geometric center of the battery's body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Battery {
    
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
    
    public boolean topTerminalIntersects( Shape shape ) {
        return ShapeUtils.intersects( shapeFactory.createTopTerminalShape(), shape );
    }
    
    /**
     * Gets the offset of the top terminal from the battery's origin.
     * This offset depends on the polarity.
     * @return
     */
    public double getTopTerminalYOffset() {
        return shapeFactory.getTopTerminalYOffset();
    }
    
    /**
     * Gets the offset of the bottom terminal from the battery's origin.
     * We don't need to account for the polarity since the bottom terminal is never visible.
     * @return
     */
    public double getBottomTerminalYOffset() {
        return ( shapeFactory.getBodySizeReference().getHeight() / 2 );
    }
}
