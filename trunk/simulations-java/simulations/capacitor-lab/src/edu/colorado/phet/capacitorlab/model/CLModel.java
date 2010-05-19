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
    private static final double MANUAL_PLATE_CHARGE = CLConstants.MANUAL_PLATE_CHARGE;
    private static final double WIRE_THICKNESS = CLConstants.WIRE_THICKNESS;
    private static final double TOP_WIRE_EXTENT = CLConstants.TOP_WIRE_EXTENT;
    private static final double BOTTOM_WIRE_EXTENT = CLConstants.BOTTOM_WIRE_EXTENT;
    
    private final DielectricMaterial[] dielectricMaterials;
    private final DielectricMaterial defaultDielectricMaterial;
    private final BatteryCapacitorCircuit circuit;
    private final Wire topWire, bottomWire;

    public CLModel() {
        
        DielectricMaterial custom = new CustomDielectricMaterial();
        DielectricMaterial teflon = new Teflon();
        DielectricMaterial polystyrene = new Polystyrene();
        DielectricMaterial paper = new Paper();
        dielectricMaterials = new DielectricMaterial[] { custom, teflon, polystyrene, paper };
        defaultDielectricMaterial = custom;
        
        Battery battery = new Battery( BATTERY_LOCATION, BATTERY_VOLTAGE );
        Capacitor capacitor = new Capacitor( CAPACITOR_LOCATION, PLATE_SIZE, PLATE_SEPARATION, defaultDielectricMaterial, DIELECTRIC_OFFSET );
        circuit = new BatteryCapacitorCircuit( battery, capacitor, BATTERY_CONNECTED, MANUAL_PLATE_CHARGE );
        
        topWire = new Wire( WIRE_THICKNESS, TOP_WIRE_EXTENT );
        bottomWire = new Wire( WIRE_THICKNESS, BOTTOM_WIRE_EXTENT );
    }
    
    public DielectricMaterial[] getDielectricMaterials() {
        return dielectricMaterials;
    }
    
    public BatteryCapacitorCircuit getCircuit() {
        return circuit;
    }
    
    public Battery getBattery() {
        return circuit.getBattery();
    }
    
    public Capacitor getCapacitor() {
        return circuit.getCapacitor();
    }
    
    public Wire getTopWire() {
        return topWire;
    }
    
    public Wire getBottomWire() {
        return bottomWire;
    }
    
    public void reset() {
        // battery
        getBattery().setVoltage( BATTERY_VOLTAGE );
        // capacitor
        getCapacitor().setPlateSideLength( PLATE_SIZE );
        getCapacitor().setPlateSeparation( PLATE_SEPARATION );
        getCapacitor().setDielectricMaterial( defaultDielectricMaterial );
        getCapacitor().setDielectricOffset( DIELECTRIC_OFFSET );
        // circuit
        getCircuit().setBatteryConnected( BATTERY_CONNECTED );
        getCircuit().setManualPlateCharge( MANUAL_PLATE_CHARGE );
    }
}
