/*, 2003.*/
package edu.colorado.phet.common_cck.util;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 2:48:52 AM
 *
 */
public class SimpleObservable {
    private ArrayList observers = new ArrayList();

    public void addObserver( SimpleObserver so ) {
        if( so == null ) {
            throw new RuntimeException( "Null observer." );
        }
        observers.add( so );
    }

    public void notifyObservers() {
        for( int i = 0; i < observers.size(); i++ ) {
            SimpleObserver simpleObserver = (SimpleObserver)observers.get( i );
            simpleObserver.update();
        }
    }

    public void removeObserver( SimpleObserver obs ) {
        observers.remove( obs );
    }

    public void removeAllObservers() {
        observers.clear();
    }

    public int numObservers() {
        return observers.size();
    }

    public String toString() {
        return super.toString() + ", observers=" + observers;
    }

    public SimpleObserver[] getObservers() {
        return (SimpleObserver[])observers.toArray( new SimpleObserver[0] );
    }
}

