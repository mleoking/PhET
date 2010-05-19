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
    
    // charges
    public static final Color POSITIVE_CHARGE = Color.RED;
    public static final Color NEGATIVE_CHARGE = Color.BLUE;
    
    // energy
    public static final Color ENERGY = Color.YELLOW;
    
    // draggable things
    public static final Color DRAGGABLE_NORMAL = Color.GREEN;
    public static final Color DRAGGABLE_HIGHLIGHT = Color.YELLOW;
    
    public static final Color INVISIBLE = new Color( 0, 0, 0, 0 );
}
