/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Simple model of a DC battery.
 * Origin is at the geometric center of the battery's body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Battery {
    
    // sizes determined by visual inspection of the associated image files
    private static final PDimension BODY_SIZE = new PDimension( 0.0065, 0.01225 ); // meters
    private static final PDimension POSITIVE_TERMINAL_SIZE = new PDimension( 0.0022, 0.00163 ); // meters
    private static final PDimension NEGATIVE_TERMINAL_SIZE = new PDimension( 0.0035, 0.0009 ); // meters

    // immutable properties
    private final Point3D location;
    
    // mutable properties
    private double voltage;
    private Polarity polarity;
    
    private final EventListenerList listeners;
    
    public Battery( Point3D location, double voltage ) {
        this.location = new Point3D.Double( location.getX(), location.getY(), location.getZ() );
        this.voltage = voltage;
        this.polarity = getPolarity( voltage );
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
    
    public boolean topTerminalContains( Point3D p ) {
        return createTopTerminalShapeWorld().contains( p.getX(), p.getY() );
    }
    
    /**
     * Gets the offset of the top terminal from the battery's origin.
     * This offset depends on the polarity.
     * @return
     */
    public double getTopTerminalYOffset() {
        double terminalHeight = ( polarity == Polarity.POSITIVE ) ? POSITIVE_TERMINAL_SIZE.getHeight() : 0;
        return -( BODY_SIZE.getHeight() / 2 ) - ( terminalHeight / 2 );
    }
    
    /**
     * Gets the offset of the bottom terminal from the battery's origin.
     * We don't need to account for the polarity since the bottom terminal is never visible.
     * @return
     */
    public double getBottomTerminalYOffset() {
        return ( BODY_SIZE.getHeight() / 2 );
    }
    
    /**
     * Gets the shape of the battery's body in the battery's local coordinate frame.
     * @return
     */
    public Shape createBodyShapeLocal() {
        return createBodyShape( new Point3D.Double() );
    }
    
    /*
     * Gets the shape of the battery's body relative to some specific origin.
     */
    private Shape createBodyShape( Point3D origin ) {
        double x = origin.getX() - ( BODY_SIZE.getWidth() / 2 );
        double y = origin.getY() - ( BODY_SIZE.getHeight() / 2 );
        return new Rectangle2D.Double( x, y, BODY_SIZE.getWidth(), BODY_SIZE.getHeight() );
    }

    /**
     * Gets the shape of the top terminal in the battery's local coordinate frame.
     * @return
     */
    public Shape createTopTerminalShapeLocal() {
        return createTopTerminalShape( new Point3D.Double() );
    }
    
    /**
     * Gets the shape of the top terminal in the world's coordinate frame.
     * @return
     */
    public Shape createTopTerminalShapeWorld() {
        return createTopTerminalShape( location );
    }
    
    /*
     * Creates the shape of the top terminal relative to some specified origin.
     * Which terminal is on top depends on the polarity.
     */
    private Shape createTopTerminalShape( Point3D origin ) {
        if ( polarity == Polarity.POSITIVE ) {
            return createPositiveTerminalShape( origin );
        }
        else {
            return createNegativeTerminalShape( origin );
        }
    }
    
    /*
     * Creates the shape of the positive terminal relative to some specified origin.
     */
    private Shape createPositiveTerminalShape( Point3D origin ) {
        final double terminalWidth = POSITIVE_TERMINAL_SIZE.getWidth();
        final double terminalHeight = POSITIVE_TERMINAL_SIZE.getHeight();
        double x = origin.getX() - ( terminalWidth / 2 );
        double y = origin.getY() - ( BODY_SIZE.getHeight() / 2 ) - ( terminalHeight / 2 );
        return new Rectangle2D.Double( x, y, terminalWidth, terminalHeight );
    }
    
    /*
     * Creates the shape of the negative terminal relative to some specified origin.
     */
    private Shape createNegativeTerminalShape( Point3D origin ) {
        final double terminalWidth = NEGATIVE_TERMINAL_SIZE.getWidth();
        final double terminalHeight = NEGATIVE_TERMINAL_SIZE.getHeight();
        double x = origin.getX() - ( terminalWidth / 2 );
        double y = origin.getY() - ( BODY_SIZE.getHeight()/2 ) - ( terminalHeight / 2 );
        return new Ellipse2D.Double( x, y, terminalWidth, terminalHeight );
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
