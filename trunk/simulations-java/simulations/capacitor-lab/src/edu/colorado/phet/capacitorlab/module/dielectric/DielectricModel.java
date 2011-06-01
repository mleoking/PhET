// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.circuit.SingleCircuit;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.CapacitanceMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.PlateChargeMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.StoredEnergyMeter;
import edu.colorado.phet.capacitorlab.model.meter.EFieldDetector;
import edu.colorado.phet.capacitorlab.model.meter.Voltmeter;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Model for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricModel {

    private final WorldBounds worldBounds;
    private final DielectricMaterial[] dielectricMaterials;
    private final SingleCircuit circuit;
    private final CapacitanceMeter capacitanceMeter;
    private final PlateChargeMeter plateChargeMeter;
    private final StoredEnergyMeter storedEnergyMeter;
    private final EFieldDetector eFieldDetector;
    private final Voltmeter voltmeter;

    public DielectricModel( IClock clock, CLModelViewTransform3D mvt, double dielectricOffset, DielectricMaterial[] dielectricMaterials ) {

        worldBounds = new WorldBounds();

        this.dielectricMaterials = dielectricMaterials;

        circuit = new SingleCircuit( clock, mvt, CLConstants.BATTERY_LOCATION, CLConstants.CAPACITOR_LOCATION,
                                     CLConstants.PLATE_WIDTH_RANGE.getDefault(), CLConstants.PLATE_SEPARATION_RANGE.getDefault(),
                                     dielectricMaterials[0], dielectricOffset, CLConstants.BATTERY_CONNECTED );

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

    public SingleCircuit getCircuit() {
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

    public void reset() {
        for ( DielectricMaterial material : dielectricMaterials ) {
            material.reset();
        }
        capacitanceMeter.reset();
        plateChargeMeter.reset();
        storedEnergyMeter.reset();
        eFieldDetector.reset();
        voltmeter.reset();
        circuit.reset();
    }

    /*
     * Gets the maximum charge on the top plate (Q_total).
     * We compute this with the battery connected because this is used to determine the range of the Plate Charge slider.
     */
    public static double getMaxPlateCharge() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMax(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(), mvt );
        capacitor.setPlatesVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        return capacitor.getTotalPlateCharge();
    }

    /*
     * Gets the maximum excess charge for the dielectric area (Q_exess_dielectric).
     */
    public static double getMaxExcessDielectricPlateCharge() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMax(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(), mvt );
        capacitor.setPlatesVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        return capacitor.getExcessDielectricPlateCharge();
    }

    /*
     * Gets the maximum effective E-field between the plates (E_effective).
     * The maximum occurs when the battery is disconnected, the Plate Charge control is set to its maximum,
     * the plate area is set to its minimum, and the dielectric constant is min, and the dielectric is fully inserted.
     * And in this situation, plate separation is irrelevant.
     */
    public static double getMaxEffectiveEField() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMin() );
        SingleCircuit circuit = new SingleCircuit( new CLClock(),
                                                   mvt,
                                                   CLConstants.BATTERY_LOCATION,
                                                   CLConstants.CAPACITOR_LOCATION,
                                                   CLConstants.PLATE_WIDTH_RANGE.getMin(),
                                                   CLConstants.PLATE_SEPARATION_RANGE.getMin(),
                                                   material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(),
                                                   false /* batteryConnected */
        );
        circuit.setDisconnectedPlateCharge( getMaxPlateCharge() );
        return circuit.getCapacitor().getEffectiveEField();
    }

    /*
     * Gets the maximum field due to dielectric polarization (E_dielectric).
     */
    public static double getMaxDielectricEField() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        SingleCircuit circuit = new SingleCircuit( new CLClock(),
                                                   mvt,
                                                   CLConstants.BATTERY_LOCATION,
                                                   CLConstants.CAPACITOR_LOCATION,
                                                   CLConstants.PLATE_WIDTH_RANGE.getMin(),
                                                   CLConstants.PLATE_SEPARATION_RANGE.getMin(),
                                                   material,
                                                   CLConstants.DIELECTRIC_OFFSET_RANGE.getMax(),
                                                   false /* batteryConnected */
        );
        circuit.setDisconnectedPlateCharge( getMaxPlateCharge() );
        return circuit.getCapacitor().getDielectricEField();
    }

    /*
     * Gets the E-field reference magnitude, used to determine the initial scale of the E-Field Detector.
     * This is based on the default capacitor configuration, with maximum battery voltage.
     */
    public static double getEFieldReferenceMagnitude() {
        Capacitor capacitor = new Capacitor( new Point3D.Double(),
                                             CLConstants.PLATE_WIDTH_RANGE.getDefault(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getDefault(),
                                             new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() ),
                                             CLConstants.DIELECTRIC_OFFSET_RANGE.getDefault(),
                                             new CLModelViewTransform3D() );
        capacitor.setPlatesVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        return capacitor.getEffectiveEField();
    }
}
