/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model.util;

import edu.colorado.phet.common.model.clock.IClock;

/**
 * BeamIntensityMeter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BeamIntensityMeter extends ScalarDataRecorder {

    private int clientUpdateInterval = 500;
    private int simulationTimeWindow = 1000;

    public BeamIntensityMeter( IClock clock ) {
        super( clock );
        super.setTimeWindow( simulationTimeWindow );
        super.setClientUpdateInterval( clientUpdateInterval );
    }

    public void recordPhoton() {
        recordPhotons( 1 );
    }

    public void recordPhotons( int numPhotons ) {
        super.addDataRecordEntry( numPhotons );
    }

    public double getIntesity() {
        double intensity = getDataTotal() / getTimeSpanOfEntries();
        if( Double.isNaN( intensity ) || Double.isInfinite( intensity )) {
            intensity = 0;
        }
        return intensity;
    }
}
