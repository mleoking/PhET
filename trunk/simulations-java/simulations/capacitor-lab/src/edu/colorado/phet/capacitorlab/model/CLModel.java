/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Paper;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Polystyrene;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Teflon;


/**
 * Model for the "Capacitor Lab" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLModel {
    
    private static final Point2D BATTERY_LOCATION = CLConstants.BATTERY_LOCATION;
    private static final double BATTERY_VOLTAGE = CLConstants.BATTERY_VOLTAGE_RANGE.getDefault();
    private static final boolean BATTERY_CONNECTED = CLConstants.BATTERY_CONNECTED;
    
    private static final Point2D CAPACITOR_LOCATION = CLConstants.CAPACITOR_LOCATION;
    private static final double PLATE_SIZE = CLConstants.PLATE_SIZE_RANGE.getDefault();
    private static final double PLATE_SEPARATION = CLConstants.PLATE_SEPARATION_RANGE.getDefault();
    private static final double DIELECTRIC_OFFSET = CLConstants.DIELECTRIC_OFFSET_RANGE.getDefault();
    
    private final DielectricMaterial[] dielectricMaterials;
    private final DielectricMaterial defaultDielectricMaterial;
    private final Battery battery;
    private final Capacitor capacitor;
    private final Wire topWire, bottomWire;

    public CLModel() {
        
        DielectricMaterial custom = new CustomDielectricMaterial();
        DielectricMaterial teflon = new Teflon();
        DielectricMaterial polystyrene = new Polystyrene();
        DielectricMaterial paper = new Paper();
        dielectricMaterials = new DielectricMaterial[] { custom, teflon, polystyrene, paper };
        defaultDielectricMaterial = custom;
        
        battery = new Battery( BATTERY_LOCATION, BATTERY_VOLTAGE, BATTERY_CONNECTED );
        
        capacitor = new Capacitor( CAPACITOR_LOCATION, PLATE_SIZE, PLATE_SEPARATION, defaultDielectricMaterial, DIELECTRIC_OFFSET );
        
        topWire = new Wire( CLConstants.WIRE_THICKNESS, CLConstants.TOP_WIRE_EXTENT );
        bottomWire = new Wire( CLConstants.WIRE_THICKNESS, CLConstants.BOTTOM_WIRE_EXTENT );
    }
    
    public DielectricMaterial[] getDielectricMaterials() {
        return dielectricMaterials;
    }
    
    public Battery getBattery() {
        return battery;
    }
    
    public Capacitor getCapacitor() {
        return capacitor;
    }
    
    public Wire getTopWire() {
        return topWire;
    }
    
    public Wire getBottomWire() {
        return bottomWire;
    }
    
    public void reset() {
        battery.setVoltage( BATTERY_VOLTAGE );
        battery.setConnected( BATTERY_CONNECTED );
        capacitor.setPlateSize( PLATE_SIZE );
        capacitor.setPlateSeparation( PLATE_SEPARATION );
        capacitor.setDielectricMaterial( defaultDielectricMaterial );
        capacitor.setDielectricOffset( DIELECTRIC_OFFSET );
    }
}
