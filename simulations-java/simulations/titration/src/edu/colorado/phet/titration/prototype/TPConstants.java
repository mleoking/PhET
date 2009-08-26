package edu.colorado.phet.titration.prototype;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;


public class TPConstants {
    
    private TPConstants() {}
    
    public static final String VERSION = new PhetResources( "titration" ).getVersion().toString();
    
    public static final double Kw = 1E-14;
    public static final double SOLUTION_VOLUME = 25; // mL
    public static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( 0.001, 1, 0.1 ); // molar (M)
    public static final DoubleRange K_RANGE = new DoubleRange( 1E-14, 1, 0.1 );
    public static final DoubleRange TITRANT_VOLUME_RANGE = new DoubleRange( 0, 100 ); // mL
    public static final double TITRANT_VOLUME_DELTA = 0.1; // mL
    public static final DoubleRange PH_RANGE = new DoubleRange( 0, 14 );
    public static final IntegerRange ROOTS_ITERATIONS_RANGE = new IntegerRange( 3, 300, 30 );
    public static final DoubleRange ROOTS_THRESHOLD_RANGE = new DoubleRange( 1E-40, 1, 1E-4 );
}
