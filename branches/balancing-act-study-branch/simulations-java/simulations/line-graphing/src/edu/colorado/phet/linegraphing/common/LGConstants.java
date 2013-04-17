// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Constants used through this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGConstants {

    public static final IntegerRange X_AXIS_RANGE = new IntegerRange( -10, 10 );
    public static final IntegerRange Y_AXIS_RANGE = X_AXIS_RANGE; // same as x-axis

    public static final int CONTROL_FONT_SIZE = 18;
    public static final PhetFont CONTROL_FONT = new PhetFont( CONTROL_FONT_SIZE );

    public static final PhetFont INTERACTIVE_EQUATION_FONT = new PhetFont( Font.BOLD, 28 );
    public static final PhetFont STATIC_EQUATION_FONT = new PhetFont( Font.PLAIN, INTERACTIVE_EQUATION_FONT.getSize() );
}
