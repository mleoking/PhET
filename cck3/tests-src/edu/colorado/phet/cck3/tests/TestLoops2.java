/** Sam Reid*/
package edu.colorado.phet.cck3.tests;

import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.CircuitChangeListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.analysis.Path;
import edu.colorado.phet.cck3.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 9:42:34 AM
 * Copyright (c) Jun 2, 2004 by Sam Reid
 */
public class TestLoops2 {
    public static void main( String[] args ) {
        CircuitChangeListener kl = new CircuitChangeListener() {
            public void circuitChanged() {
            }
        };
        Circuit circuit = new Circuit( kl );
        Junction a = new Junction( 0, 0 );
        Junction b = new Junction( 1, 0 );
        circuit.addBranch( new Branch( kl, a, b ) );
        circuit.addBranch( new Branch( kl, a, b ) );
        circuit.addBranch( new Branch( kl, a, b ) );

        Path[] p = Path.getLoops( circuit );
        for( int i = 0; i < p.length; i++ ) {
            Path path = p[i];
            System.out.println( "path = " + path );
        }
    }
}
