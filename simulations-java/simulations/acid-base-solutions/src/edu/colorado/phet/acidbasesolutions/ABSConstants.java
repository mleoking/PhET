/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSConstants {

    /* Not intended for instantiation. */
    private ABSConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "acid-base-solutions";

    // Model-view transform
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final int CONTROL_FONT_SIZE = 18;
    public static final Font CONTROL_FONT = new PhetFont( Font.PLAIN, CONTROL_FONT_SIZE );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    public static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 230, 230, 230 );
    
    public static final Color H2O_COLOR = new Color( 193, 222, 227 );
    public static final Color H3O_COLOR = new Color( 242, 102, 101 );
    public static final Color OH_COLOR = new Color( 102, 132, 242 );
    public static final Color HA_COLOR = new Color( 13, 176, 47 );
    public static final Color A_COLOR = new Color( 235, 145, 5 );
    public static final Color B_COLOR = new Color( 136, 40, 99 );
    public static final Color BH_COLOR = new Color( 169, 155, 23 );
    
    // particle colors may be tweaked so that they stand out better
    public static final Color H3O_PARTICLES_COLOR = H3O_COLOR;
    public static final Color OH_PARTICLES_COLOR = OH_COLOR;
    public static final Color HA_PARTICLES_COLOR = HA_COLOR;
    public static final Color A_PARTICLES_COLOR = A_COLOR;
    public static final Color B_PARTICLES_COLOR = B_COLOR;
    public static final Color BH_PARTICLES_COLOR = BH_COLOR;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final double WATER_CONCENTRATION = 55; // mol/L
    
    public static final double MIN_CONCENTRATION = 1E-3;
    public static final double MAX_CONCENTRATION = 1;
    public static final double DEFAULT_CONCENTRATION = 1E-1;
    
    public static final double MIN_WEAK_STRENGTH = 1E-12;
    public static final double MAX_WEAK_STRENGTH = 1E2;
    public static final double DEFAULT_WEAK_STRENGTH = 1E-5;
    
}
