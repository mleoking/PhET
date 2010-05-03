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
    public static final Color PLATE = Color.LIGHT_GRAY;
    
    //XXX should these be part of dielectric material model?
    // dielectric
    public static final Color DIELECTRIC_TOP = Color.GREEN;
    public static final Color DIELECTRIC_FRONT = DIELECTRIC_TOP.darker();
    public static final Color DIELECTRIC_SIDE = DIELECTRIC_FRONT.darker();
}
