// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.BarMeter.CapacitanceMeter;
import edu.colorado.phet.capacitorlab.model.BarMeter.PlateChargeMeter;
import edu.colorado.phet.capacitorlab.model.BarMeter.StoredEnergyMeter;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Glass;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Paper;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Teflon;
import edu.colorado.phet.capacitorlab.model.multicaps.ICapacitor;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Model for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricModel {

    private final WorldBounds worldBounds;
    private final CustomDielectricMaterial customDielectricMaterial;
    private final DielectricMaterial[] dielectricMaterials;
    private final BatteryCapacitorCircuit circuit;
    private final CapacitanceMeter capacitanceMeter;
    private final PlateChargeMeter plateChargeMeter;
    private final StoredEnergyMeter storedEnergyMeter;
    private final EFieldDetector eFieldDetector;
    private final Voltmeter voltmeter;

    public DielectricModel( IClock clock, CLModelViewTransform3D mvt ) {

        worldBounds = new WorldBounds();

        customDielectricMaterial = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );
        dielectricMaterials = new DielectricMaterial[] { customDielectricMaterial, new Teflon(), new Paper(), new Glass() };

        Battery battery = new Battery( CLConstants.BATTERY_LOCATION, CLConstants.BATTERY_VOLTAGE_RANGE.getDefault(), mvt );
        Capacitor capacitor = new Capacitor( CLConstants.CAPACITOR_LOCATION, CLConstants.PLATE_WIDTH_RANGE.getDefault(), CLConstants.PLATE_SEPARATION_RANGE.getDefault(),
                                             customDielectricMaterial, CLConstants.PLATE_WIDTH_RANGE.getDefault() /* dielectricOffset */, mvt );
        circuit = new BatteryCapacitorCircuit( clock, battery, capacitor, CLConstants.BATTERY_CONNECTED, mvt );

        capacitanceMeter = new CapacitanceMeter( circuit, worldBounds, CLConstants.CAPACITANCE_METER_LOCATION, CLConstants.CAPACITANCE_METER_VISIBLE );
        plateChargeMeter = new PlateChargeMeter( circuit, worldBounds, CLConstants.PLATE_CHARGE_METER_LOCATION, CLConstants.PLATE_CHARGE_METER_VISIBLE );
        storedEnergyMeter = new StoredEnergyMeter( circuit, worldBounds, CLConstants.STORED_ENERGY_METER_LOCATION, CLConstants.STORED_ENERGY_METER_VISIBLE );

        eFieldDetector = new EFieldDetector( circuit, worldBounds, CLConstants.EFIELD_DETECTOR_BODY_LOCATION, CLConstants.EFIELD_DETECTOR_PROBE_LOCATION,
                                             CLConstants.EFIELD_DETECTOR_VISIBLE, CLConstants.EFIELD_PLATE_VECTOR_VISIBLE, CLConstants.EFIELD_DIELECTRIC_VECTOR_VISIBLE,
                                             CLConstants.EFIELD_SUM_VECTOR_VISIBLE, CLConstants.EFIELD_VALUES_VISIBLE );

        voltmeter = new Voltmeter( circuit, worldBounds, mvt,
                                   CLConstants.VOLTMETER_BODY_LOCATION, CLConstants.VOLTMETER_POSITIVE_PROBE_LOCATION, CLConstants.VOLTMETER_NEGATIVE_PROBE_LOCATION,
                                   CLConstants.VOLTMETER_VISIBLE );
    }

    public WorldBounds getWorldBounds() {
        return worldBounds;
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

    public ICapacitor getCapacitor() {
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

    public Wire getBottomWire() {
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
        circuit.setBatteryConnected( CLConstants.BATTERY_CONNECTED ); //XXX replace with getCircuit().reset()
        circuit.reset();
    }

    /**
     * Gets the maximum charge on the top plate (Q_total).
     * We compute this with the battery connected because this is used to determine the range of the Plate Charge slider.
     *
     * @return charge, in Coulombs
     */
    public static double getMaxPlateCharge() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMax(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(), mvt );
        capacitor.setPlatesVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        return capacitor.getTotalPlateCharge();
    }

    /**
     * Gets the maximum excess charge for the dielectric area (Q_exess_dielectric).
     *
     * @return
     */
    public static double getMaxExcessDielectricPlateCharge() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMax(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(), mvt );
        capacitor.setPlatesVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        return capacitor.getExcessDielectricPlateCharge();
    }

    /**
     * Gets the maximum effective E-field between the plates (E_effective).
     * The maximum occurs when the battery is disconnected, the Plate Charge control is set to its maximum,
     * the plate area is set to its minimum, and the dielectric constant is min, and the dielectric is fully inserted.
     * And in this situation, plate separation is irrelevant.
     *
     * @return
     */
    public static double getMaxEffectiveEfield() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        Battery battery = new Battery( new Point3D.Double(), 0, mvt );
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMin() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMin(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(), mvt );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( new CLClock(), battery, capacitor, false /* batteryConnected */, mvt );
        circuit.setDisconnectedPlateCharge( getMaxPlateCharge() );
        return capacitor.getEffectiveEfield();
    }

    /**
     * Gets the maximum field due to dielectric polarization (E_dielectric).
     *
     * @return
     */
    public static double getMaxDielectricEField() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        Battery battery = new Battery( new Point3D.Double(), 0, mvt );
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMin(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMax(), mvt );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( new CLClock(), battery, capacitor, false /* batteryConnected */, mvt );
        circuit.setDisconnectedPlateCharge( getMaxPlateCharge() );
        return capacitor.getDielectricEField();
    }

    /**
     * Gets the maximum E-field due to the plates in the capacitor volume that
     * contains the dielectric (E_plates_dielectric), with the battery connected.
     *
     * @return
     */
    public static double getMaxPlatesDielectricEFieldWithBattery() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMin(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMax(), mvt );
        capacitor.setPlatesVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        return capacitor.getPlatesDielectricEField();
    }
}
