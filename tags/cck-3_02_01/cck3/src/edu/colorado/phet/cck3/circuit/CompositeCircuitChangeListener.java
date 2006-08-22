/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 9, 2004
 * Time: 2:45:10 PM
 * Copyright (c) Jun 9, 2004 by Sam Reid
 */
public class CompositeCircuitChangeListener implements CircuitChangeListener {
    ArrayList list = new ArrayList();

    public void addKirkhoffListener( CircuitChangeListener kl ) {
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
