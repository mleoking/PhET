/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
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
    
    public static final String FLAVOR_MAGNIFYING_GLASS_PROTOTYPE = "magnifying-glass-prototype";

    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final double AVOGADROS_NUMBER = 6.022E23;
    
    public static final PDimension BEAKER_SIZE = new PDimension( 600, 500 );
    public static final Point2D BEAKER_LOCATION = new Point2D.Double( ( BEAKER_SIZE.getWidth() / 2 ) + 150, BEAKER_SIZE.getHeight() + 300 );
    public static final double MAGNIFYING_GLASS_DIAMETER = 500;
    public static final double PH_METER_HEIGHT = 200;
    
    public static final double WATER_CONCENTRATION = 55.6; // water concentration when it's used as a solvent, mol/L
    public static final double WATER_EQUILIBRIUM_CONSTANT = 1E-14;
    
    public static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( 1E-3, 1, 1E-2 );
    public static final DoubleRange WEAK_STRENGTH_RANGE = new DoubleRange( 1E-7, 1, 1E-4 );
    public static final double STRONG_STRENGTH = WEAK_STRENGTH_RANGE.getMax() + 1;
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    
    public static final IntegerRange MAX_IMAGES_RANGE = new IntegerRange( 100, 2000, 200 );;
    public static final IntegerRange MAX_H2O_IMAGES_RANGE = new IntegerRange( 300, 10000, 2000 );;
    public static final DoubleRange IMAGE_SCALE_RANGE = new DoubleRange( 0.25, 2.5, 1 );
    public static final boolean WATER_VISIBLE = false;
    
    //----------------------------------------------------------------------------
    // Control
    //----------------------------------------------------------------------------
    
    public static final Font TITLED_BORDER_FONT = new PhetFont( PhetFont.getDefaultFontSize() + 4 );
}
