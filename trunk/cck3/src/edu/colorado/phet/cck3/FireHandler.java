/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.CircuitGraphic;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolutionListener;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 12:05:48 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class FireHandler implements KirkhoffSolutionListener {
    CircuitGraphic circuitGraphic;
    public static double FIRE_CURRENT = 10;

    public FireHandler( CircuitGraphic circuitGraphic ) {
        this.circuitGraphic = circuitGraphic;
    }

    public void finishedKirkhoff() {
        addFires();
    }

    private void addFires() {
        Circuit c = circuitGraphic.getCircuit();
        for( int i = 0; i < c.numBranches(); i++ ) {
            Branch b = c.branchAt( i );
            if( b.getCurrent() > FIRE_CURRENT ) {
                circuitGraphic.setOnFire( b, true );
            }
            else {
                circuitGraphic.setOnFire( b, false );
            }
        }
    }
}
