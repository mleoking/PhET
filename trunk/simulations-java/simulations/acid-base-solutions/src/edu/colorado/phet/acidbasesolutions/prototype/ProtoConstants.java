package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;


public class ProtoConstants {

    public static final Color CANVAS_COLOR = Color.WHITE;
    public static final IntegerRange BEAKER_WIDTH_RANGE = new IntegerRange( 50, 500, 250 );
    public static final IntegerRange BEAKER_HEIGHT_RANGE = new IntegerRange( 50, 500, 300 );
    public static final IntegerRange MAGNIFYING_GLASS_DIAMETER_RANGE = new IntegerRange( 100, 700, 400 );
    public static final DoubleRange WEAK_ACID_CONCENTRATION_RANGE = new DoubleRange( 1E-3, 1 );
    public static final DoubleRange WEAK_ACID_STRENGTH_RANGE = new DoubleRange( 1E-10, 1 );
}
