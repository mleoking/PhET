/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.util;

/**
 * Averager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

public class Averager {
    private long updatePeriod = 10000;
//    private long updatePeriod = 30000;
    private long timeOfLastUpdate = System.currentTimeMillis();
    private double aveTemp = 0;
    private double sampleTotal;
    private int sampleCnt = 0;
    private String text;

    public Averager( String text ) {
        this.text = text;
    }

    public void update( double newTemperature ) {
        //DEBUG
        sampleTotal += newTemperature;
        sampleCnt++;
        if( System.currentTimeMillis() - timeOfLastUpdate > updatePeriod ) {
            aveTemp = sampleTotal / sampleCnt;
            System.out.println( text + aveTemp );
            sampleCnt = 0;
            sampleTotal = 0;
            timeOfLastUpdate = System.currentTimeMillis();
        }

    }
}
