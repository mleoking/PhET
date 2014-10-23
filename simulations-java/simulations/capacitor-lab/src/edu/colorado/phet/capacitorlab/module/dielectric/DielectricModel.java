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
 * </p>
 * This model is somewhat similar to  with MultipleCapacitorsModel, but was developed much earlier,
 * and is parametrized differently (with a large number of parameters).
 * I attempted to force some of the common bits into the base class, but it became messy and less readable.
 * So I decided that a bit of duplication is preferable here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricModel extends CLModel {

    //================================================================================
    // Model parameter values
    //================================================================================

    // Circuit
    private static final Point3D BATTERY_LOCATION = new Point3D.Double( 0.005, 0.034, 0 ); // meters
    private static final boolean BATTERY_CONNECTED = true;
    private static final double CAPACITOR_X_SPACING = 0.025; // meters
    private static final double CAPACITOR_Y_SPACING = 0; // meters
    private static final double PLATE_WIDTH = CLConstants.PLATE_WIDTH_RANGE.getDefault();
    private static final double PLATE_SEPARATION = CLConstants.PLATE_SEPARATION_RANGE.getDefault();
    private static final double WIRE_THICKNESS = CLConstants.WIRE_THICKNESS;
    private static final double WIRE_EXTENT = 0.016; // how far the wire extends above or below the capacitor (meters)

    // Capacitance meter
    public static final Point3D CAPACITANCE_METER_LOCATION = new Point3D.Double( 0.038, 0.0017, 0 );
    public static final boolean CAPACITANCE_METER_VISIBLE = false;

    // Plate Charge meter
    public static final Point3D PLATE_CHARGE_METER_LOCATION = new Point3D.Double( 0.049, 0.0017, 0 );
    public static final boolean PLATE_CHARGE_METER_VISIBLE = false;

    // Stored Energy meter
    public static final Point3D STORED_ENERGY_METER_LOCATION = new Point3D.Double( 0.06, 0.0017, 0 );
    public static final boolean STORED_ENERGY_METER_VISIBLE = false;

    // E-Field Detector
    public static final Point3D EFIELD_DETECTOR_BODY_LOCATION = new Point3D.Double( 0.043, 0.041, 0 );
    public static final Point3D EFIELD_DETECTOR_PROBE_LOCATION = BATTERY_LOCATION;
    public static final boolean EFIELD_DETECTOR_VISIBLE = false;
    public static final boolean EFIELD_PLATE_VECTOR_VISIBLE = true;
    public static final boolean EFIELD_DIELECTRIC_VECTOR_VISIBLE = true;
    public static final boolean EFIELD_SUM_VECTOR_VISIBLE = true;
    public static final boolean EFIELD_VALUES_VISIBLE = true;

    // Voltmeter
    public static final Point3D VOLTMETER_BODY_LOCATION = new Point3D.Double( 0.057, 0.023, 0 );
    public static final Point3D VOLTMETER_POSITIVE_PROBE_LOCATION = new Point3D.Double( BATTERY_LOCATION.getX() + 0.015, BATTERY_LOCATION.getY(), BATTERY_LOCATION.getZ() );
    public static final Point3D VOLTMETER_NEGATIVE_PROBE_LOCATION = new Point3D.Double( VOLTMETER_POSITIVE_PROBE_LOCATION.getX() + 0.005, VOLTMETER_POSITIVE_PROBE_LOCATION.getY(), VOLTMETER_POSITIVE_PROBE_LOCATION.getZ() );
    public static final boolean VOLTMETER_VISIBLE = false;

    //================================================================================

    public final DielectricMaterial[] dielectricMaterials;
    public final SingleCircuit circuit;
    public final CapacitanceMeter capacitanceMeter;
    public final PlateChargeMeter plateChargeMeter;
    public final StoredEnergyMeter storedEnergyMeter;
    public final EFieldDetector eFieldDetector;
    public final Voltmeter voltmeter;

    public DielectricModel( IClock clock, CLModelViewTransform3D mvt, double dielectricOffset, DielectricMaterial[] dielectricMaterials ) {

        this.dielectricMaterials = dielectricMaterials;

        // configuration info for the circuit
        final CircuitConfig circuitConfig = new CircuitConfig( clock,
                                                               mvt,
                                                               BATTERY_LOCATION,
                                                               CAPACITOR_X_SPACING,
                                                               CAPACITOR_Y_SPACING,
                                                               PLATE_WIDTH,
                                                               PLATE_SEPARATION,
                                                               dielectricMaterials[0],
                                                               dielectricOffset,
                                                               WIRE_THICKNESS,
                                                               WIRE_EXTENT );

        circuit = new SingleCircuit( circuitConfig, BATTERY_CONNECTED );

        capacitanceMeter = new CapacitanceMeter( circuit, getWorldBounds(), CAPACITANCE_METER_LOCATION, CAPACITANCE_METER_VISIBLE );
        plateChargeMeter = new PlateChargeMeter( circuit, getWorldBounds(), PLATE_CHARGE_METER_LOCATION, PLATE_CHARGE_METER_VISIBLE );
        storedEnergyMeter = new StoredEnergyMeter( circuit, getWorldBounds(), STORED_ENERGY_METER_LOCATION, STORED_ENERGY_METER_VISIBLE );

        eFieldDetector = new EFieldDetector( circuit, getWorldBounds(), mvt, EFIELD_DETECTOR_BODY_LOCATION, EFIELD_DETECTOR_PROBE_LOCATION,
                                             EFIELD_DETECTOR_VISIBLE, EFIELD_PLATE_VECTOR_VISIBLE, EFIELD_DIELECTRIC_VECTOR_VISIBLE,
                                             EFIELD_SUM_VECTOR_VISIBLE, EFIELD_VALUES_VISIBLE );

        voltmeter = new Voltmeter( circuit, getWorldBounds(), mvt,
                                   VOLTMETER_BODY_LOCATION, VOLTMETER_POSITIVE_PROBE_LOCATION, VOLTMETER_NEGATIVE_PROBE_LOCATION,
                                   VOLTMETER_VISIBLE );
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
        return getCapacitorWithMaxCharge().getTotalPlateCharge();
    }

    /*
     * Gets the maximum excess charge for the dielectric area (Q_excess_dielectric).
     */
    public static double getMaxExcessDielectricPlateCharge() {
        return getCapacitorWithMaxCharge().getExcessDielectricPlateCharge();
    }

    // Gets a capacitor with maximum charge.
    private static Capacitor getCapacitorWithMaxCharge() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(),
                                             CLConstants.PLATE_WIDTH_RANGE.getMax(),
                                             CLConstants.PLATE_SEPARATION_RANGE.getMin(),
                                             material,
                                             CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(),
                                             mvt );
        capacitor.setPlatesVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        return capacitor;
    }

    /*
     * Gets the maximum effective E-field between the plates (E_effective).
     * The maximum occurs when the battery is disconnected, the Plate Charge control is set to its maximum,
     * the plate area is set to its minimum, and the dielectric constant is min, and the dielectric is fully inserted.
     * And in this situation, plate separation is irrelevant.
     */
    public static double getMaxEffectiveEField() {
        CircuitConfig circuitConfig = new CircuitConfig( new CLClock(),
                                                         new CLModelViewTransform3D(),
                                                         new Point3D.Double(),
                                                         CAPACITOR_X_SPACING, CAPACITOR_Y_SPACING,
                                                         CLConstants.PLATE_WIDTH_RANGE.getMin(),
                                                         CLConstants.PLATE_SEPARATION_RANGE.getMin(),
                                                         new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMin() ),
                                                         CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(),
                                                         CLConstants.WIRE_THICKNESS, WIRE_EXTENT );
        SingleCircuit circuit = new SingleCircuit( circuitConfig, false /* batteryConnected */ );
        circuit.setDisconnectedPlateCharge( getMaxPlateCharge() );
        return circuit.getCapacitor().getEffectiveEField();
    }

    /*
     * Gets the maximum field due to dielectric polarization (E_dielectric).
     */
    public static double getMaxDielectricEField() {
        CircuitConfig circuitConfig = new CircuitConfig( new CLClock(),
                                                         new CLModelViewTransform3D(),
                                                         new Point3D.Double(),
                                                         CAPACITOR_X_SPACING, CAPACITOR_Y_SPACING,
                                                         CLConstants.PLATE_WIDTH_RANGE.getMin(),
                                                         CLConstants.PLATE_SEPARATION_RANGE.getMin(),
                                                         new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() ),
                                                         CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(),
                                                         CLConstants.WIRE_THICKNESS, WIRE_EXTENT );
        SingleCircuit circuit = new SingleCircuit( circuitConfig, false /* batteryConnected */ );
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
