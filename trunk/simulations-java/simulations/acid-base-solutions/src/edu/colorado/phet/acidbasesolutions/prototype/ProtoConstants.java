/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Collection of constants for the Magnifying Glass View prototype.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ProtoConstants {
    
    private ProtoConstants() {}

    // canvas
    public static final Color CANVAS_COLOR = Color.WHITE;
    public static final Dimension2D CANVAS_SIZE = new Dimension( 1024, 768 );
    
    // beaker
    public static final IntegerRange BEAKER_WIDTH_RANGE = new IntegerRange( 400, 1000, 600 );
    public static final IntegerRange BEAKER_HEIGHT_RANGE = new IntegerRange( 400, 1000, 800 );
    public static final Point2D BEAKER_CENTER = new Point2D.Double( 450, 600 );
    
    // magnifying glass
    public static final IntegerRange MAGNIFYING_GLASS_DIAMETER_RANGE = new IntegerRange( 100, 1200, 500 );
    public static final Point2D MAGNIFYING_GLASS_CENTER = BEAKER_CENTER;
    
    // weak acid
    public static final DoubleRange WEAK_ACID_CONCENTRATION_RANGE = new DoubleRange( 1E-3, 1 );
    public static final DoubleRange WEAK_ACID_STRENGTH_RANGE = new DoubleRange( 1E-10, 1 );
    public static final Color WEAK_ACID_COLOR = new Color( 193, 222, 227 ); // light blue
    
    // dots
    public static final IntegerRange MAX_DOTS_RANGE = new IntegerRange( 1000, 10000, 5000 );
    public static final DoubleRange DOT_DIAMETER_RANGE = new DoubleRange( 1, 40, 6 );
    public static final DoubleRange DOT_TRANSPARENCY_RANGE = new DoubleRange( 0, 1, 0.5 );
    
    // colors
    public static final Color COLOR_HA = new Color( 13, 176, 47 );
    public static final Color COLOR_A_MINUS = new Color( 235, 145, 5 );
    public static final Color COLOR_H3O_PLUS = new Color( 222, 2, 0 );
    public static final Color COLOR_OH_MINUS = new Color( 102, 132, 242 );
    public static final Color COLOR_H2O = new Color( 193, 222, 227 );

}
