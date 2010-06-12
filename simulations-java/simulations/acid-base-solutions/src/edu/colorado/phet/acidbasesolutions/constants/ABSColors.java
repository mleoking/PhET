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
}
