// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity;

import java.awt.Font;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.molarity.util.ZeroIntegerDoubleFormat;

/**
 * Constants used throughout this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityConstants {

    // properties shared by sliders and concentration display
    public static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    public static final PhetFont SUBTITLE_FONT = new PhetFont( Font.PLAIN, 14 );
    public static final PhetFont RANGE_FONT = new PhetFont( Font.PLAIN, 14 );
    public static final PhetFont VALUE_FONT = new PhetFont( Font.PLAIN, 16 );
    public static final int VALUE_DECIMAL_PLACES = 2;
    public static final DecimalFormat VALUE_FORMAT = DefaultDecimalFormat.createFormat( VALUE_DECIMAL_PLACES );
    public static final ZeroIntegerDoubleFormat RANGE_FORMAT = new ZeroIntegerDoubleFormat( "0.0" );
}
