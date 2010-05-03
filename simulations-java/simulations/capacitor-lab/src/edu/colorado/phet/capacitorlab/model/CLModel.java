/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;


/**
 * Model for the "Capacitor Lab" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLModel {
    
    private static final Point2D BATTERY_LOCATION = CLConstants.BATTERY_LOCATION;
    private static final double BATTERY_VOLTAGE = CLConstants.BATTERY_VOLTAGE_RANGE.getDefault();
    
    private static final Point2D CAPACITOR_LOCATION = CLConstants.CAPACITOR_LOCATION;
    private static final double PLATE_SIZE = CLConstants.PLATE_SIZE_RANGE.getDefault();
    private static final double PLATE_SEPARATION = CLConstants.PLATE_SEPARATION_RANGE.getDefault();
    private static final DielectricMaterial DIELECTRIC_MATERIAL = new CustomDielectricMaterial(); //XXX
    private static final double DIELECTRIC_OFFSET = CLConstants.DIELECTRIC_OFFSET_RANGE.getDefault();
    
    public final Battery battery;
    public final Capacitor capacitor;

    public CLModel() {
        battery = new Battery( BATTERY_LOCATION, BATTERY_VOLTAGE );
        capacitor = new Capacitor( CAPACITOR_LOCATION, PLATE_SIZE, PLATE_SEPARATION, DIELECTRIC_MATERIAL, DIELECTRIC_OFFSET );
    }
    
    public Battery getBattery() {
        return battery;
    }
    
    public Capacitor getCapacitor() {
        return capacitor;
    }
    
    public void reset() {
        battery.setVoltage( BATTERY_VOLTAGE );
        capacitor.setPlateSize( PLATE_SIZE );
        capacitor.setPlateSeparation( PLATE_SEPARATION );
        capacitor.setDielectricMaterial( DIELECTRIC_MATERIAL );
        capacitor.setDielectricOffset( DIELECTRIC_OFFSET );
    }
}
