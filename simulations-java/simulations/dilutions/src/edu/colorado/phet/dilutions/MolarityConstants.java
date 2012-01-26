// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions;

import java.awt.Font;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.dilutions.common.util.ZeroIntegerDoubleFormat;

/**
 * Constants used throughout this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityConstants {

    public static final PhetFont SLIDER_TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    public static final PhetFont SLIDER_SUBTITLE_FONT = new PhetFont( Font.PLAIN, 14 );
    public static final PhetFont SLIDER_RANGE_FONT = new PhetFont( Font.PLAIN, 14 );
    public static final PhetFont SLIDER_VALUE_FONT = new PhetFont( Font.PLAIN, 16 );

    public static final int VALUE_DECIMAL_PLACES = 2;
    public static final DecimalFormat VALUE_FORMAT = new DefaultDecimalFormat( "0.00" ); // this should match VALUE_DECIMAL_PLACES;
    public static final ZeroIntegerDoubleFormat RANGE_FORMAT = new ZeroIntegerDoubleFormat( "0.0" );
}
