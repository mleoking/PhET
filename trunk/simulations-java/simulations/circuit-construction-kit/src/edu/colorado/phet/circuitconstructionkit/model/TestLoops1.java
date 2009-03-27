package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.circuitconstructionkit.model.analysis.Path;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 9:42:34 AM
 */
public class TestLoops1 {
    public static void main( String[] args ) {
        CircuitChangeListener kl = new CircuitChangeListener() {
            public void circuitChanged() {
            }
        };
        Circuit circuit = new Circuit( kl );
        Junction a = new Junction( 0, 0 );
        Junction b = new Junction( 1, 0 );
        circuit.addBranch( new Wire( kl, a, b ) );
        circuit.addBranch( new Wire( kl, a, b ) );
        Path[] p = Path.getLoops( circuit );
        for ( int i = 0; i < p.length; i++ ) {
            Path path = p[i];
            System.out.println( "path = " + path );
        }
    }
}
