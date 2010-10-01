/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

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
    
    private final DielectricMaterial[] dielectricMaterials;
    private final DielectricMaterial defaultDielectricMaterial;
    private final BatteryCapacitorCircuit circuit;
    private final Wire topWire, bottomWire;

    public CLModel( CLClock clock ) {
        
        DielectricMaterial custom = new CustomDielectricMaterial();
        DielectricMaterial teflon = new Teflon();
        DielectricMaterial polystyrene = new Polystyrene();
        DielectricMaterial paper = new Paper();
        dielectricMaterials = new DielectricMaterial[] { custom, teflon, polystyrene, paper };
        defaultDielectricMaterial = custom;
        
        Battery battery = new Battery( CLConstants.BATTERY_LOCATION, CLConstants.BATTERY_VOLTAGE_RANGE.getDefault() );
        Capacitor capacitor = new Capacitor( CLConstants.CAPACITOR_LOCATION, CLConstants.PLATE_SIZE_RANGE.getDefault(), CLConstants.PLATE_SEPARATION_RANGE.getDefault(), 
                defaultDielectricMaterial, CLConstants.DIELECTRIC_OFFSET_RANGE.getDefault() );
        circuit = new BatteryCapacitorCircuit( clock, battery, capacitor, CLConstants.BATTERY_CONNECTED );
        
        topWire = new Wire( CLConstants.WIRE_THICKNESS, CLConstants.TOP_WIRE_EXTENT );
        bottomWire = new Wire( CLConstants.WIRE_THICKNESS, CLConstants.BOTTOM_WIRE_EXTENT );
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
        getBattery().setVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getDefault() );
        // capacitor
        getCapacitor().setPlateSideLength( CLConstants.PLATE_SIZE_RANGE.getDefault() );
        getCapacitor().setPlateSeparation( CLConstants.PLATE_SEPARATION_RANGE.getDefault() );
        getCapacitor().setDielectricMaterial( defaultDielectricMaterial );
        getCapacitor().setDielectricOffset( CLConstants.DIELECTRIC_OFFSET_RANGE.getDefault() );
        // circuit
        getCircuit().setBatteryConnected( CLConstants.BATTERY_CONNECTED );
    }
}
