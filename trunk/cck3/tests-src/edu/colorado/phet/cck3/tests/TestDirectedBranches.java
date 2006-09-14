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
public class TestDirectedBranches {
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
            Path.DirectedBranch[] x = path.getDirectedBranches();
            for( int j = 0; j < x.length; j++ ) {
                Path.DirectedBranch directedBranch = x[j];
                System.out.println( "db[" + j + "]=" + directedBranch );
            }
        }

        /*

        Correct output.
path = 4 <d> 2 <c> 1 <b> 3 <e> 4
db[0]=Branch_d[Junction_2[1.0,0.0],Junction_4[1.0,0.0]], forward=false
db[1]=Branch_c[Junction_1[1.0,0.0],Junction_2[1.0,0.0]], forward=false
db[2]=Branch_b[Junction_1[1.0,0.0],Junction_3[1.0,0.0]], forward=true
db[3]=Branch_e[Junction_3[1.0,0.0],Junction_4[1.0,0.0]], forward=true
path = 4 <d> 2 <f> 3 <e> 4
db[0]=Branch_d[Junction_2[1.0,0.0],Junction_4[1.0,0.0]], forward=false
db[1]=Branch_f[Junction_2[1.0,0.0],Junction_3[1.0,0.0]], forward=true
db[2]=Branch_e[Junction_3[1.0,0.0],Junction_4[1.0,0.0]], forward=true
path = 1 <b> 3 <f> 2 <c> 1
db[0]=Branch_b[Junction_1[1.0,0.0],Junction_3[1.0,0.0]], forward=true
db[1]=Branch_f[Junction_2[1.0,0.0],Junction_3[1.0,0.0]], forward=false
db[2]=Branch_c[Junction_1[1.0,0.0],Junction_2[1.0,0.0]], forward=false

*/
    }
}
