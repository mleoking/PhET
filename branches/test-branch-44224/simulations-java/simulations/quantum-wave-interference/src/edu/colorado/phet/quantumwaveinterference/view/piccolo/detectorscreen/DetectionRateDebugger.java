/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen;

import edu.colorado.phet.quantumwaveinterference.QWIResources;

import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Jul 29, 2005
 * Time: 9:50:41 AM
 */

public class DetectionRateDebugger {
    long startTime = -1;
    int numCounts = 0;

    public void addDetectionEvent() {
        if( startTime == -1 ) {
            startTime = System.currentTimeMillis();
        }
        numCounts++;

        if( numCounts % 1000 == 0 ) {
            printout();
        }
    }

    private void printout() {
        double elapsedSeconds = ( System.currentTimeMillis() - startTime ) / 1000.0;
        double intensity = numCounts / elapsedSeconds;
        System.out.println( MessageFormat.format( QWIResources.getString( "counts.0.time.1" ), new Object[]{new Integer( numCounts ), new Double( elapsedSeconds )} ) );
        System.out.println( MessageFormat.format( QWIResources.getString( "total.average.intensity.0.particles.second" ), new Object[]{new Double( intensity )} ) );
    }

}
