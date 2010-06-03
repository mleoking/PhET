/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Collection of constants for the Magnifying Glass prototype.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPConstants {
    
    private MGPConstants() {}
    
    // Resource loader
    private static final PhetResources RESOURCES = new PhetResources( ABSConstants.PROJECT_NAME );

    // canvas
    public static final Color CANVAS_COLOR = Color.WHITE;
    public static final Dimension2D CANVAS_SIZE = new Dimension( 1024, 768 );
    
    // beaker
    public static final IntegerRange BEAKER_WIDTH_RANGE = new IntegerRange( 400, 900, 600 );
    public static final IntegerRange BEAKER_HEIGHT_RANGE = new IntegerRange( 400, 900, 500 );
    
    // magnifying glass
    public static final IntegerRange MAGNIFYING_GLASS_DIAMETER_RANGE = new IntegerRange( 100, 900, 500 );
    
    // model constants
    public static final double Kw = 1E-14; // water equilibrium constant
    public static final double W = 55.6; // water concentration, mol/L
    public static final double AVOGADROS_NUMBER = 6.022E23;
    
    // weak acid
    public static final DoubleRange WEAK_ACID_CONCENTRATION_RANGE = new DoubleRange( 1E-3, 1 );
    public static final DoubleRange WEAK_ACID_STRENGTH_RANGE = new DoubleRange( 1E-10, 1000 );
    
    // dots
    public static final IntegerRange MAX_DOTS_RANGE = new IntegerRange( 100, 2000, 500 );
    public static final IntegerRange MAX_H2O_DOTS_RANGE = new IntegerRange( 0, 10000, 1000 );
    public static final DoubleRange DOT_DIAMETER_RANGE = new DoubleRange( 1, 40, 12 );
    public static final DoubleRange DOT_TRANSPARENCY_RANGE = new DoubleRange( 0, 1, 1 );
    
    // images
    public static final IntegerRange MAX_IMAGES_RANGE = new IntegerRange( 100, 2000, 200 );;
    public static final IntegerRange MAX_H2O_IMAGES_RANGE = new IntegerRange( 300, 10000, 2000 );;
    public static final DoubleRange IMAGE_SCALE_RANGE = new DoubleRange( 0.25, 2.5, 1 );
    public static final DoubleRange IMAGE_TRANSPARENCY_RANGE = DOT_TRANSPARENCY_RANGE;
    public static final BufferedImage HA_IMAGE = RESOURCES.getImage( "molecules/HA.png" );
    public static final BufferedImage A_MINUS_IMAGE = RESOURCES.getImage( "molecules/A_minus.png" );
    public static final BufferedImage H3O_PLUS_IMAGE = RESOURCES.getImage( "molecules/H3O_plus.png" );
    public static final BufferedImage OH_MINUS_IMAGE = RESOURCES.getImage( "molecules/OH_minus.png" );
    public static final BufferedImage H2O_IMAGE = RESOURCES.getImage( "molecules/H2O.png" );
    public static final BufferedImage ARROW_DOUBLE_IMAGE = RESOURCES.getImage( "arrow_double.png" );
    
    // colors
    public static final Color COLOR_HA = new Color( 13, 176, 47 );
    public static final Color COLOR_A_MINUS = new Color( 235, 145, 5 );
    public static final Color COLOR_H3O_PLUS = new Color( 222, 2, 0 );
    public static final Color COLOR_OH_MINUS = new Color( 102, 132, 242 );
    public static final Color COLOR_H2O = Color.LIGHT_GRAY;
    public static final Color COLOR_WEAK_ACID = new Color( 193, 222, 227 ); // light blue
    
    // Strings & HTML fragments
    public static final String UNITS_LITERS = "L";
    public static final String HA_FRAGMENT = "H<i>A</i>";
    public static final String A_MINUS_FRAGMENT = "<i>A</i><sup>-</sup>";
    public static final String H3O_PLUS_FRAGMENT = "H<sub>3</sub>O<sup>+</sup>";
    public static final String OH_MINUS_FRAGMENT = "OH<sup>-</sup>";
    public static final String H2O_FRAGMENT = "H<sub>2</sub>O";
    
    // Default settings
    public static final boolean DEFAULT_SHOW_H2O = false;
}
