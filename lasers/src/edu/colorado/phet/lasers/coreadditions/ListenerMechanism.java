/**
 * Class: ListenerMechanism
 * Package: edu.colorado.phet.lasers.coreadditions
 * Original Author: Ron LeMaster
 * Creation Date: Oct 24, 2004
 * Creation Time: 12:39:14 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.coreadditions;

import java.util.LinkedList;

public class ListenerMechanism {
    private LinkedList listeners = new LinkedList( );

    public interface Notifier {
        void doNotify( Object obj );
    }

    public void addListener( Object obj ) {
        listeners.add( obj );
    }

    public void removeListener( Object obj ) {
        listeners.remove( obj );
    }

    public void notifyListeners( Notifier notifier ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            Object obj = listeners.get( i );
            notifier.doNotify( obj );
        }
    }
}
