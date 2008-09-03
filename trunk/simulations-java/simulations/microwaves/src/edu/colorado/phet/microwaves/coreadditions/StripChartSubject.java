/**
 * Class: StripChartSubject
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwaves.coreadditions;

import java.util.Observable;
import java.util.Observer;

public abstract class StripChartSubject extends Observable implements Observer {

    public abstract double getDatum();

    public void update( Observable o, Object arg ) {
        setChanged();
        notifyObservers();
    }
}
