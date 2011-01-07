// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions;

import java.awt.Color;

/**
 * Colors used throughout the simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AABSColors {
    
    /* not intended for instantiation */
    private AABSColors() {}

    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 230, 230, 230 );
    
    // control panel backgrounds
    public static final Color COLOR_PANEL_BACKGROUND = new Color( 255, 255, 220 ); // light yellow
    
    // equation backgrounds
    public static final Color REACTION_EQUATIONS_BACKGROUND = Color.WHITE;
    public static final Color EQUILIBRIUM_EXPRESSIONS_BACKGROUND = REACTION_EQUATIONS_BACKGROUND;
    
    // molecule colors
    public static final Color H2O = new Color( 193, 222, 227 );
    public static final Color H3O_PLUS = new Color( 222, 2, 0 );
    public static final Color OH_MINUS = new Color( 102, 132, 242 );
    public static final Color HA = new Color( 13, 176, 47 );
    public static final Color A_MINUS = new Color( 235, 145, 5 );
    public static final Color B = new Color( 169, 51, 216 );
    public static final Color BH_PLUS = new Color( 192, 178, 60 );
    public static final Color MOH = B;
    public static final Color M_PLUS = BH_PLUS;
    public static final Color K = Color.GRAY;
    
    // equation colors
    public static final Color H2O_EQUATION = H2O.darker();
    
    // slider thumbs
    public static final Color THUMB_ENABLED = new Color( 154, 255, 255 );
    public static final Color THUMB_HIGHLIGHTED = new Color( 46, 255, 255 );
    public static final Color THUMB_DISABLED = new Color( 245, 245, 245 );
}
