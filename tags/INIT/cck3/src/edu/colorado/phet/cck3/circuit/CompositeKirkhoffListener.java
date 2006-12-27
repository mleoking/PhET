/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 9, 2004
 * Time: 2:45:10 PM
 * Copyright (c) Jun 9, 2004 by Sam Reid
 */
public class CompositeKirkhoffListener implements KirkhoffListener {
    ArrayList list = new ArrayList();

    public void addKirkhoffListener( KirkhoffListener kl ) {
        list.add( kl );
    }

    public void removeKirkhoffListener( KirkhoffListener kl ) {
        list.remove( kl );
    }

    public void circuitChanged() {
        for( int i = 0; i < list.size(); i++ ) {
            KirkhoffListener kirkhoffListener = (KirkhoffListener)list.get( i );
            kirkhoffListener.circuitChanged();
        }
    }
}
