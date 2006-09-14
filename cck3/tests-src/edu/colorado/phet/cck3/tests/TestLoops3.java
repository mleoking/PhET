/** Sam Reid*/
package edu.colorado.phet.cck3.tests;

import edu.colorado.phet.cck3.model.Branch;
import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.CircuitChangeListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.analysis.Path;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 9:42:34 AM
 * Copyright (c) Jun 2, 2004 by Sam Reid
 */
public class TestLoops3 {
    public static void main( String[] args ) {
        CircuitChangeListener kl = new CircuitChangeListener() {
            public void circuitChanged() {
            }
        };
        Circuit circuit = new Circuit( kl );
        Junction j0 = new Junction( 0, 0 );
        Junction j1 = new Junction( 1, 0 );
        Junction j2 = new Junction( 1, 0 );
        Junction j3 = new Junction( 1, 0 );
        Junction j4 = new Junction( 1, 0 );
        circuit.addBranch( new Branch( kl, j0, j4 ) );
        circuit.addBranch( new Branch( kl, j1, j3 ) );
        circuit.addBranch( new Branch( kl, j1, j2 ) );
        circuit.addBranch( new Branch( kl, j2, j4 ) );
        circuit.addBranch( new Branch( kl, j3, j4 ) );
        circuit.addBranch( new Branch( kl, j2, j3 ) );

        Path[] p = Path.getLoops( circuit );
        for( int i = 0; i < p.length; i++ ) {
            Path path = p[i];
            System.out.println( "path = " + path );
        }

        /*
        Correct output

        path = 4 <d> 2 <c> 1 <b> 3 <e> 4
path = 4 <d> 2 <f> 3 <e> 4
path = 1 <b> 3 <f> 2 <c> 1

*/
    }
}
