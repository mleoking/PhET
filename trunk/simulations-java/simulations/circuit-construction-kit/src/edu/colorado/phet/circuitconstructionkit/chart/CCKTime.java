/*  */
package edu.colorado.phet.circuitconstructionkit.chart;

import edu.colorado.phet.circuitconstructionkit.model.CCKDefaults;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 2:30:01 PM
 */

public class CCKTime {
    public static final double viewTimeScale = CCKDefaults.modelTimeScale / 100.0;

    public static double getDisplayTime( double simulationTime ) {
        return simulationTime * viewTimeScale;
    }
}
