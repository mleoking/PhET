/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.CircuitGraphic;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 12:05:48 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class FireHandler implements CircuitSolutionListener {
    CircuitGraphic circuitGraphic;
    public static double FIRE_CURRENT = 10;

    public FireHandler( CircuitGraphic circuitGraphic ) {
        this.circuitGraphic = circuitGraphic;
    }

    public void circuitSolverFinished() {
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
