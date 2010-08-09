/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.Color;

/**
 * Colors used throughout the simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSColors {
    
    /* not intended for instantiation */
    private ABSColors() {}
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 230, 230, 230 );
    
    // Color of main control panel
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( 204, 204, 255 );
    
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
    
    // solution color
    public static final Color AQUEOUS_SOLUTION = new Color( 193, 222, 227, 180 ); // transparent light blue
    
    // pH colors
    public static final Color PH_0 = new Color( 182, 70, 72 );
    public static final Color PH_1 = new Color( 196, 80, 86 );
    public static final Color PH_2 = new Color( 213, 83, 71 );
    public static final Color PH_3 = new Color( 237, 123, 83 );
    public static final Color PH_4 = new Color( 246, 152, 86 );
    public static final Color PH_5 = new Color( 244, 158, 79 ); 
    public static final Color PH_6 = new Color( 243, 160, 78 );
    public static final Color PH_7 = new Color( 244, 182, 67 );
    public static final Color PH_8 = new Color( 231, 201, 75 );
    public static final Color PH_9 = new Color( 93, 118, 88);
    public static final Color PH_10 = new Color( 30, 92, 89 );
    public static final Color PH_11 = new Color( 34, 90, 105 );
    public static final Color PH_12 = new Color( 39, 87, 111 );
    public static final Color PH_13 = new Color( 27, 67, 90 );
    public static final Color PH_14 = new Color( 0, 34, 52 );
}
