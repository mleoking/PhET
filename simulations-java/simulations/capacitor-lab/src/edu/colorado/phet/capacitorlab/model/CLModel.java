/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.CLConstants;


/**
 * Model for the "Capacitor Lab" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLModel {
    
    private static final double BATTERY_VOLTAGE = CLConstants.BATTERY_VOLTAGE_RANGE.getDefault();
    private static final double PLATE_WIDTH = CLConstants.PLATE_SIZE_RANGE.getDefault();
    private static final double PLATE_SEPARATION = CLConstants.PLATE_SEPARATION_RANGE.getDefault();
    private static final double DIELECTRIC_OFFSET = 0;
    
    public final Battery battery;
    public final Capacitor capacitor;

    public CLModel() {
        battery = new Battery( BATTERY_VOLTAGE );
        capacitor = new Capacitor( PLATE_WIDTH, PLATE_SEPARATION, DIELECTRIC_OFFSET );
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
