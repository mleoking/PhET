/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.model.simpleobservable;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 2:48:52 AM
 */
public class SimpleObservable {
    ArrayList observers = new ArrayList();

    public void addObserver( SimpleObserver so ) {
        observers.add( so );
    }

    public void updateObservers() {
        for( int i = 0; i < observers.size(); i++ ) {
            SimpleObserver simpleObserver = (SimpleObserver)observers.get( i );
            simpleObserver.update();
        }
    }

    public void removeObserver( SimpleObserver obs ) {
        observers.remove( obs );
    }
}

