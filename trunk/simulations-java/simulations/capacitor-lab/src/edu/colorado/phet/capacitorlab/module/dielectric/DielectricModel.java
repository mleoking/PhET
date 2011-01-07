// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Glass;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Paper;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Teflon;

/**
 * Model for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricModel {
    
    // static properties
    private final World world;
    private final CustomDielectricMaterial customDielectricMaterial;
    private final DielectricMaterial[] dielectricMaterials;
    private final BatteryCapacitorCircuit circuit;
    private final CapacitanceMeter capacitanceMeter;
    private final PlateChargeMeter plateChargeMeter;
    private final StoredEnergyMeter storedEnergyMeter;
    private final EFieldDetector eFieldDetector;
    private final Voltmeter voltmeter;
    
    public DielectricModel( CLClock clock, CLModelViewTransform3D mvt ) {
        
        world = new World();
        
        customDielectricMaterial = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );
        dielectricMaterials = new DielectricMaterial[] { customDielectricMaterial, new Teflon(), new Paper(), new Glass() };
        
        Battery battery = new Battery( CLConstants.BATTERY_LOCATION, CLConstants.BATTERY_VOLTAGE_RANGE.getDefault(), mvt );
        Capacitor capacitor = new Capacitor( CLConstants.CAPACITOR_LOCATION, CLConstants.PLATE_WIDTH_RANGE.getDefault(), CLConstants.PLATE_SEPARATION_RANGE.getDefault(), 
                customDielectricMaterial, CLConstants.PLATE_WIDTH_RANGE.getDefault() /* dielectricOffset */, mvt );
        circuit = new BatteryCapacitorCircuit( clock, battery, capacitor, CLConstants.BATTERY_CONNECTED, mvt );
        
        capacitanceMeter = new CapacitanceMeter( circuit, world, CLConstants.CAPACITANCE_METER_LOCATION, CLConstants.CAPACITANCE_METER_VISIBLE );
        plateChargeMeter = new PlateChargeMeter( circuit, world, CLConstants.PLATE_CHARGE_METER_LOCATION, CLConstants.PLATE_CHARGE_METER_VISIBLE );
        storedEnergyMeter = new StoredEnergyMeter( circuit, world, CLConstants.STORED_ENERGY_METER_LOCATION, CLConstants.STORED_ENERGY_METER_VISIBLE );
        
        eFieldDetector = new EFieldDetector( circuit, world, CLConstants.EFIELD_DETECTOR_BODY_LOCATION, CLConstants.EFIELD_DETECTOR_PROBE_LOCATION, 
                CLConstants.EFIELD_DETECTOR_VISIBLE, CLConstants.EFIELD_PLATE_VECTOR_VISIBLE, CLConstants.EFIELD_DIELECTRIC_VECTOR_VISIBLE, 
                CLConstants.EFIELD_SUM_VECTOR_VISIBLE, CLConstants.EFIELD_VALUES_VISIBLE );
        
        voltmeter = new Voltmeter( circuit, world, mvt, 
                CLConstants.VOLTMETER_BODY_LOCATION, CLConstants.VOLTMETER_POSITIVE_PROBE_LOCATION, CLConstants.VOLTMETER_NEGATIVE_PROBE_LOCATION,
                CLConstants.VOLTMETER_VISIBLE );
    }
    
    public World getWorld() {
        return world;
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
    
    public CapacitanceMeter getCapacitanceMeter() {
        return capacitanceMeter;
    }
    
    public PlateChargeMeter getPlateChargeMeter() {
        return plateChargeMeter;
    }
    
    public StoredEnergyMeter getStoredEnergyMeter() {
        return storedEnergyMeter;
    }
    
    public EFieldDetector getEFieldDetector() {
        return eFieldDetector;
    }
    
    public Voltmeter getVoltmeter() {
        return voltmeter;
    }
    
    public Wire getTopWire() {
        return circuit.getTopWire();
    }

    public Wire getBottomWire(){
        return circuit.getBottomWire();
    }

    public void reset() {
        getBattery().reset();
        getCapacitor().reset();
        customDielectricMaterial.reset();
        capacitanceMeter.reset();
        plateChargeMeter.reset();
        storedEnergyMeter.reset();
        eFieldDetector.reset();
        voltmeter.reset();
        getCircuit().setBatteryConnected( CLConstants.BATTERY_CONNECTED ); //XXX replace with getCircuit().reset()
    }
}
