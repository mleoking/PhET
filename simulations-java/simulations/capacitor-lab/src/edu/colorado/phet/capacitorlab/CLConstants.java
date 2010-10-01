/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.Dimension;

import edu.colorado.phet.capacitorlab.view.DielectricNode.DielectricChargeView;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLConstants {

    /* Not intended for instantiation. */
    private CLConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "capacitor-lab";
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final Point3D BATTERY_LOCATION = new Point3D.Double( 0.005, 0.035, 0 ); // meters
    public static final DoubleRange BATTERY_VOLTAGE_RANGE = new DoubleRange( -1.5, 1.5, 0 ); // volts
    public static final boolean BATTERY_CONNECTED = true;
    
    public static final Point3D CAPACITOR_LOCATION = new Point3D.Double( BATTERY_LOCATION.getX() + 0.025, BATTERY_LOCATION.getY(), 0 ); // meters
    public static final DoubleRange PLATE_SIZE_RANGE = new DoubleRange( .01, .02 ); // meters
    public static final DoubleRange PLATE_SEPARATION_RANGE = new DoubleRange( .005, .01, .01 ); // meters
    public static final double PLATE_THICKNESS = .0005; // meters
    
    public static final DoubleRange DIELECTRIC_CONSTANT_RANGE = new DoubleRange( 1, 5 ); // dimensionless
    public static final DoubleRange DIELECTRIC_OFFSET_RANGE = new DoubleRange( 0, PLATE_SIZE_RANGE.getMax() ); // meters
    public static final double DIELECTRIC_GAP = 0.0002; // gap between dielectric and plates, meters
    
    public static final double WIRE_THICKNESS = .0005; // meters
    public static final double TOP_WIRE_EXTENT = Math.abs( .016 ); // how far the top wire extends above the capactor's origin, absolute value (meters)
    public static final double BOTTOM_WIRE_EXTENT = TOP_WIRE_EXTENT; // how far the bottom wire extends below the capactor's origin, absolute value (meters)
    
    public static final double EPSILON_0 = 8.854E-12; // vacuum permittivity, aka electric constant (Farads/meter)
    public static final double EPSILON_VACUUM = 1; // dielectric constant of a vacuum, dimensionless
    public static final double EPSILON_AIR = 1.0005896; // dielectric constant of air, dimensionless
    
    public static final Point3D PLATE_CHARGE_CONTROL_LOCATION = new Point3D.Double( CAPACITOR_LOCATION.getX() - 0.004, 0.001, 0 );
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    
    // model-view transform
    public static final double MVT_SCALE = 15000; // scale factor when going from model to view
    public static final double YAW_VIEWING_ANGLE = Math.toRadians( -45 ); // rotation about the vertical axis, right-hand rule determines sign
    public static final double PITCH_VIEWING_ANGLE = Math.toRadians( 30 ); // rotation about the horizontal axis, right-hand rule determines sign
    
    public static final double PSWING_SCALE = 1.5;
    
    public static final double DRAG_HANDLE_ARROW_LENGTH = 35; // pixels
    
    // default exponents for the meters
    public static final int CAPACITANCE_METER_VALUE_EXPONENT = -13;
    public static final int PLATE_CHARGE_METER_VALUE_EXPONENT = -10;
    public static final int STORED_ENERGY_METER_VALUE_EXPONENT = -10;
    
    // visibility of meters
    public static final boolean CAPACITANCE_METER_VISIBLE = false;
    public static final boolean CHARGE_METER_VISIBLE = false;
    public static final boolean ENERGY_METER_VISIBLE = false;
    public static final boolean VOLTMETER_VISIBLE = false;
    public static final boolean EFIELD_DETECTOR_VISIBLE = false;
    
    // plate charges
    public static final boolean PLATE_CHARGES_VISIBLE = false; 
    public static final int MAX_NUMBER_OF_PLATE_CHARGES = 625;
    public static final double MIN_NONZERO_PLATE_CHARGE = 8E-15; // Coulombs, all non-zero values <= MIN_NONZERO_PLATE_CHARGE are represented by 1 charge
    
    // dielectric charges
    public static final DielectricChargeView DIELECTRIC_CHARGE_VIEW = DielectricChargeView.NONE; //XXX default should be NONE
    
    // E-field
    public static final boolean EFIELD_VISIBLE = false;
    public static final int MAX_NUMBER_OF_EFIELD_LINES = 200;
    public static final double MIN_NONZERO_EFFECTIVE_EFIELD = 0.5; // V/m
    
    //----------------------------------------------------------------------------
    // Control
    //----------------------------------------------------------------------------
}
