/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.photoelectric.model.util.ScalarDataRecorder;

/**
 * Ammeter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Ammeter extends ScalarDataRecorder {

    private int clientUpdateInterval = 500;
    private int simulationTimeWindow = 1000;

    public Ammeter( IClock clock ) {
//    public Ammeter( AbstractClock clock ) {
        super( clock );
        super.setTimeWindow( simulationTimeWindow );
        super.setClientUpdateInterval( clientUpdateInterval );
    }

    public double getCurrent() {
        computeDataStatistics();
        double current = getDataTotal() / getTimeSpanOfEntries();
        if( Double.isNaN( current ) || Double.isInfinite( current )) {
            current = 0;
        }
        return current;
    }

    public void recordElectron() {
        recordElectrons( 1 );
    }

    public void recordElectrons( int numElectrons ) {
        addDataRecordEntry( numElectrons );
    }

    public int getSimulationTimeWindow() {
        return simulationTimeWindow;
    }
}
