// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab;

import java.awt.*;

import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLConstants {

    /* Not intended for instantiation. */
    private CLConstants() {
    }

    public static final String PROJECT_NAME = "capacitor-lab";

    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------

    public static final double EPSILON_0 = 8.854E-12; // vacuum permittivity, aka electric constant (Farads/meter)

    // world
    public static final double WORLD_DRAG_MARGIN = 0.001; // meters

    // battery
    public static final DoubleRange BATTERY_VOLTAGE_RANGE = new DoubleRange( -1.5, 1.5, 0 ); // Volts
    public static final double BATTERY_VOLTAGE_SNAP_TO_ZERO_THRESHOLD = 0.1; // Volts

    // capacitor
    public static final DoubleRange PLATE_WIDTH_RANGE = new DoubleRange( 0.01, 0.02, 0.01 ); // meters
    public static final double PLATE_HEIGHT = 0.0005; // meters
    public static final DoubleRange PLATE_SEPARATION_RANGE = new DoubleRange( 0.005, 0.01, 0.01 ); // meters
    public static final DoubleRange CAPACITANCE_RANGE = new DoubleRange( 1E-13, 3E-13 ); // Farads

    // dielectric
    public static final DoubleRange DIELECTRIC_CONSTANT_RANGE = new DoubleRange( 1, 5, 5 ); // dimensionless
    public static final DoubleRange DIELECTRIC_OFFSET_RANGE = new DoubleRange( 0, PLATE_WIDTH_RANGE.getMax(), PLATE_WIDTH_RANGE.getDefault() ); // meters

    // dielectric constants (dimensionless)
    public static final double EPSILON_VACUUM = 1;
    public static final double EPSILON_GLASS = 4.7;
    public static final double EPSILON_PAPER = 3.5;
    public static final double EPSILON_TEFLON = 2.1;

    /*
     * The dielectric constant of air is actually 1.0005896. The model for this sim specified that
     * the circuit was in air.  But we discovered late in the development of this sim that we should
     * have modeled the circuit in a vacuum, because we want the E-Field component due to the
     * environment to be zero.  With air, we have a small Dielectric vector of up to 4 V/m shown
     * on the E-Field Detector when the Plate Charge control is set to its maximum.
     *
     * Rather than change "air" to "vacuum" in numerous places throughout the code and design doc,
     * it was suggested that we simply set the dielectric constant of air to 1.0.  I was hesitant to
     * do this, since I think it's going to cause future problems.  But Kathy P. bet me a 6-pack of
     * beer that it will never be a problem.  Any developer who needs to change this in the future
     * is hereby bound by the Developer Code of Ethics to inform me, so that I can collect on
     * this wager.
     */
    public static final double EPSILON_AIR = 1.0;

    // Wire
    public static final double WIRE_THICKNESS = 0.0005; // meters

    // Plate Charge control
    public static final double PLATE_CHARGE_CONTROL_SNAP_TO_ZERO_THRESHOLD = 1.5E-13;

    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------

    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 864 );

    // model-view transform
    public static final double MVT_SCALE = 15000; // scale factor when going from model to view
    public static final double MVT_YAW = Math.toRadians( -45 ); // rotation about the vertical axis, right-hand rule determines sign
    public static final double MVT_PITCH = Math.toRadians( 30 ); // rotation about the horizontal axis, right-hand rule determines sign

    public static final double DRAG_HANDLE_ARROW_LENGTH = 35; // pixels

    // default exponents for the meters
    public static final int CAPACITANCE_METER_VALUE_EXPONENT = -12;
    public static final int PLATE_CHARGE_METER_VALUE_EXPONENT = -13;
    public static final int STORED_ENERGY_METER_VALUE_EXPONENT = -13;

    // plate charges
    public static final IntegerRange NUMBER_OF_PLATE_CHARGES = new IntegerRange( 1, 625 );
    public static final Dimension NEGATIVE_CHARGE_SIZE = new Dimension( 7, 2 );
    public static final boolean PLATE_CHARGES_VISIBLE = true;

    // dielectric charges
    public static final DielectricChargeView DIELECTRIC_CHARGE_VIEW = DielectricChargeView.TOTAL;

    // E-field
    public static final IntegerRange NUMBER_OF_EFIELD_LINES = new IntegerRange( 4, 900 ); // number of lines on smallest plate
    public static final boolean EFIELD_VISIBLE = false;

    // capacitance control
    public static final int CAPACITANCE_CONTROL_EXPONENT = -13;
}
