/*, 2003.*/
package edu.colorado.phet.greenhouse.phetcommon.model;

import java.util.Observable;

/**
 * It's automated because it automatically sets itself to be changed when notifyObserver(s) is called.
 * Sometimes this will be all we need.
 */
public class AutomatedObservable extends Observable {
    public void updateObservers() {
        super.setChanged();
        super.notifyObservers();
    }

    public void updateObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
}
