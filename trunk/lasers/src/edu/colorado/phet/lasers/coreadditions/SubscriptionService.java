/**
 * Class: SubscriptionService
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

public class SubscriptionService {
    private LinkedList subscribers = new LinkedList( );

    public interface Notifier {
        void doNotify( Object obj );
    }

    public void addListener( Object obj ) {
        subscribers.add( obj );
    }

    public void removeListener( Object obj ) {
        subscribers.remove( obj );
    }

    public void notifyListeners( Notifier notifier ) {
        for( int i = 0; i < subscribers.size(); i++ ) {
            Object obj = subscribers.get( i );
            notifier.doNotify( obj );
        }
    }
}
