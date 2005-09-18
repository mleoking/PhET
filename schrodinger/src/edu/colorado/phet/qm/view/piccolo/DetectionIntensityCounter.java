/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

/**
 * User: Sam Reid
 * Date: Jul 29, 2005
 * Time: 9:50:41 AM
 * Copyright (c) Jul 29, 2005 by Sam Reid
 */

public class DetectionIntensityCounter {
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
        System.out.println( "counts=" + numCounts + ", time=" + elapsedSeconds );
        System.out.println( "Total average Intensity=" + intensity + " particles/second" );
    }

}
