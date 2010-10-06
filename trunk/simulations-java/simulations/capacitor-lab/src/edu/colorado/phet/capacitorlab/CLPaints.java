/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.Color;

/**
 * Paint constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLPaints {
    
    /* not intended for instantiation */
    private CLPaints() {}

    public static final Color CANVAS_BACKGROUND = new Color( 167, 222, 243 );
    
    // capacitor plates
    public static final Color PLATE =  new Color( 245, 245, 245 );
    
    // dielectric materials
    public static final Color AIR = new Color( 0, 0, 0, 0 ); // invisible
    public static final Color CUSTOM_DIELECTRIC = new Color( 255, 161, 23 ); // orange
    public static final Color PAPER = new Color( 226, 255, 155 ); // light yellow
    public static final Color POLYSTYRENE = new Color( 189, 132, 141 ); // pink
    public static final Color TEFLON = Color.GREEN;
    
    // charges
    public static final Color POSITIVE_CHARGE = Color.RED;
    public static final Color NEGATIVE_CHARGE = Color.BLUE;
    
    // energy
    public static final Color ENERGY = Color.YELLOW;
    
    // capacitance
    public static final Color CAPACITANCE = Color.GREEN;
    
    // field lines
    public static final Color EFIELD = Color.BLACK;
    
    // draggable things
    public static final Color DRAGGABLE_NORMAL = Color.GREEN;
    public static final Color DRAGGABLE_HIGHLIGHT = Color.YELLOW;
}
