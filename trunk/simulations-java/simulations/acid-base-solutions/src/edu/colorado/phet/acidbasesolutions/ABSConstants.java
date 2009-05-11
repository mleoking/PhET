/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.util.PDimension;


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
    
    // how much to scale Swing components that are wrapped in PSwing
    public static final double PSWING_SCALE = 1.25;
    
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
    public static final Color MOH_COLOR = new Color( 147, 44, 187 );
    public static final Color M_COLOR = new Color( 236, 216, 42 );
    public static final Color K_COLOR = Color.GRAY;
    
    // particle colors may be tweaked so that they stand out better
    public static final Color H3O_PARTICLES_COLOR = H3O_COLOR;
    public static final Color OH_PARTICLES_COLOR = OH_COLOR;
    public static final Color HA_PARTICLES_COLOR = HA_COLOR;
    public static final Color A_PARTICLES_COLOR = A_COLOR;
    public static final Color B_PARTICLES_COLOR = B_COLOR;
    public static final Color BH_PARTICLES_COLOR = BH_COLOR;
    public static final Color MOH_PARTICLES_COLOR = MOH_COLOR;
    public static final Color M_PARTICLES_COLOR = M_COLOR;
    
    //----------------------------------------------------------------------------
    // Dimensions
    //----------------------------------------------------------------------------
    
    public static final PDimension MIN_BEAKER_LABEL_SIZE = new PDimension( 275, 50 );
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( 1E-3, 1 );
    public static final DoubleRange WEAK_STRENGTH_RANGE = new DoubleRange( 10E-10, 1 );
    public static final DoubleRange STRONG_STRENGTH_RANGE = new DoubleRange( 20, 10E7 );
    public static final DoubleRange INTERMEDIATE_STRENGTH_RANGE = new DoubleRange( WEAK_STRENGTH_RANGE.getMax(), STRONG_STRENGTH_RANGE.getMin() ); // exclusive
    
}
