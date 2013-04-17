// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab;

import java.awt.*;

/**
 * Paint constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLPaints {

    /* not intended for instantiation */
    private CLPaints() {
    }

    public static final Color CANVAS_BACKGROUND = new Color( 167, 222, 243 );

    // capacitor plates
    public static final Color PLATE = new Color( 245, 245, 245 );

    // dielectric materials
    public static final Color CUSTOM_DIELECTRIC = new Color( 255, 255, 125 ); // pale yellow
    public static final Color GLASS = new Color( 100, 100, 100, 64 ); // transparent gray
    public static final Color PAPER = new Color( 255, 255, 225 ); // off white
    public static final Color TEFLON = new Color( 0, 225, 255 ); // light blue
    public static final Color AIR = Color.RED; // should never be displayed, so pick something obviously wrong

    // charges
    public static final Color POSITIVE_CHARGE = Color.RED;
    public static final Color NEGATIVE_CHARGE = Color.BLUE;

    // energy
    public static final Color STORED_ENERGY = Color.YELLOW;

    // capacitance
    public static final Color CAPACITANCE = Color.GREEN;

    // field lines
    public static final Color EFIELD = Color.BLACK;

    // draggable things
    public static final Color DRAGGABLE_NORMAL = Color.GREEN;
    public static final Color DRAGGABLE_HIGHLIGHT = Color.YELLOW;

    // vectors
    public static final Color PLATE_EFIELD_VECTOR = Color.LIGHT_GRAY;
    public static final Color DIELECTRIC_EFIELD_VECTOR = Color.YELLOW;
    public static final Color SUM_EFIELD_VECTOR = Color.GREEN;

    // wires
    public static final Color EFIELD_DETECTOR_WIRE = new Color( 129, 129, 129 );
    public static final Color VOLTMETER_POSITIVE_WIRE = Color.RED;
    public static final Color VOLTMETER_NEGATIVE_WIRE = Color.BLACK;

    // shapes
    public static final Color EFIELD_DEBUG_SHAPES = Color.BLUE;
    public static final Color VOLTAGE_DEBUG_SHAPES = Color.RED;
}
