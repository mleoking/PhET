
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.CircuitChangeListener;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 9, 2004
 * Time: 2:45:10 PM
 *
 */
public class CompositeCircuitChangeListener implements CircuitChangeListener {
    ArrayList list = new ArrayList();

    public void addCircuitChangeListener( CircuitChangeListener kl ) {
        list.add( kl );
    }

    public void removeKirkhoffListener( CircuitChangeListener kl ) {
        list.remove( kl );
    }

    public void circuitChanged() {
        for( int i = 0; i < list.size(); i++ ) {
            CircuitChangeListener circuitChangeListener = (CircuitChangeListener)list.get( i );
            circuitChangeListener.circuitChanged();
        }
    }
}
