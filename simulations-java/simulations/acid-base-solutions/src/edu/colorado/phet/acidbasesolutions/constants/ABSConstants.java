/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.Dimension;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
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
    
    public static final String FLAVOR_MAGNIFYING_GLASS_PROTOTYPE = "magnifying-glass-prototype";

    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final double AVOGADROS_NUMBER = 6.022E23;
    
    public static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( 1E-3, 1 );
    public static final DoubleRange WEAK_STRENGTH_RANGE = new DoubleRange( 1E-7, 1 );
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Control
    //----------------------------------------------------------------------------
    
    public static final Font TITLED_BORDER_FONT = new PhetFont( PhetFont.getDefaultFontSize() + 4 );
}
