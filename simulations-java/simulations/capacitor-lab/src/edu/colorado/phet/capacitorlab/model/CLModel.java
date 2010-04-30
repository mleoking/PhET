/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;


/**
 * Model for the "Capacitor Lab" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLModel {
    
    private static final Point2D BATTERY_LOCATION = new Point2D.Double( -100, 0 ); // mm
    private static final Point2D CAPACITOR_LOCATION = new Point2D.Double( 0, 0 ); // mm
    private static final double BATTERY_VOLTAGE = CLConstants.BATTERY_VOLTAGE_RANGE.getDefault(); // volts
    private static final double PLATE_WIDTH = CLConstants.PLATE_SIZE_RANGE.getDefault(); // mm
    private static final double PLATE_SEPARATION = CLConstants.PLATE_SEPARATION_RANGE.getDefault(); // mm
    private static final double DIELECTRIC_OFFSET = 0; // mm
    
    public final Battery battery;
    public final Capacitor capacitor;

    public CLModel() {
        battery = new Battery( BATTERY_LOCATION, BATTERY_VOLTAGE );
        capacitor = new Capacitor( CAPACITOR_LOCATION, PLATE_WIDTH, PLATE_SEPARATION, DIELECTRIC_OFFSET );
    }
    
    public Battery getBattery() {
        return battery;
    }
    
    public Capacitor getCapacitor() {
        return capacitor;
    }
    
    public void reset() {
        battery.setVoltage( BATTERY_VOLTAGE );
        capacitor.setPlateWidth( PLATE_WIDTH );
        capacitor.setPlateSeparation( PLATE_SEPARATION );
        capacitor.setDielectricOffset( DIELECTRIC_OFFSET );
    }
}
