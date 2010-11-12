/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.shapes.BatteryShapeFactory;
import edu.colorado.phet.capacitorlab.util.ShapeUtils;
import edu.colorado.phet.common.phetcommon.math.Point3D;

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
    
    // mutable properties
    private double voltage;
    private Polarity polarity;
    
    private final EventListenerList listeners;
    
    public Battery( Point3D location, double voltage ) {
        this.location = new Point3D.Double( location.getX(), location.getY(), location.getZ() );
        this.voltage = voltage;
        this.polarity = getPolarity( voltage );
        this.shapeFactory = new BatteryShapeFactory( this );
        listeners = new EventListenerList();
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
        if ( voltage != this.voltage ) {
            this.voltage = voltage;
            fireVoltageChanged();
            setPolarity( getPolarity( voltage ) );
        }
    }
    
    public double getVoltage() {
        return voltage;
    }
    
    private void setPolarity( Polarity polarity ) {
        if ( polarity != this.polarity ) {
            this.polarity = polarity;
            firePolarityChanged();
        }
    }
    
    public Polarity getPolarity() {
        return polarity;
    }
    
    private static Polarity getPolarity( double voltage ) {
        return ( voltage >= 0 ) ? Polarity.POSITIVE : Polarity.NEGATIVE;
    }
    
    public boolean topTerminalIntersects( Shape shape ) {
        return ShapeUtils.intersects( shapeFactory.createTopTerminalShapeWorld(), shape );
    }
    
    /**
     * Gets the offset of the top terminal from the battery's origin.
     * This offset depends on the polarity.
     * @return
     */
    public double getTopTerminalYOffset() {
        double terminalHeight = ( polarity == Polarity.POSITIVE ) ? shapeFactory.getPositiveProbeSizeReference().getHeight() : 0;
        return -( shapeFactory.getBodySizeReference().getHeight() / 2 ) - ( terminalHeight / 2 );
    }
    
    /**
     * Gets the offset of the bottom terminal from the battery's origin.
     * We don't need to account for the polarity since the bottom terminal is never visible.
     * @return
     */
    public double getBottomTerminalYOffset() {
        return ( shapeFactory.getBodySizeReference().getHeight() / 2 );
    }
    
    public interface BatteryChangeListener extends EventListener {
        public void voltageChanged();
        public void polarityChanged();
    }
    
    public static class BatteryChangeAdapter implements BatteryChangeListener {
        public void voltageChanged() {}
        public void polarityChanged() {}
    }
    
    public void addBatteryChangeListener( BatteryChangeListener listener ) {
        listeners.add( BatteryChangeListener.class, listener );
    }
    
    public void removeBatteryChangeListener( BatteryChangeListener listener ) {
        listeners.remove( BatteryChangeListener.class, listener );
    }
    
    private void fireVoltageChanged() {
        for ( BatteryChangeListener listener : listeners.getListeners( BatteryChangeListener.class ) ) {
            listener.voltageChanged();
        }
    }
    
    private void firePolarityChanged() {
        for ( BatteryChangeListener listener : listeners.getListeners( BatteryChangeListener.class ) ) {
            listener.polarityChanged();
        }
    }
}
