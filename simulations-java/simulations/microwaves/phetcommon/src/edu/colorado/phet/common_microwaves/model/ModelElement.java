package edu.colorado.phet.common_microwaves.model;

import java.util.Observable;


/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Feb 24, 2003
 * Time: 11:08:37 PM
 * To change this template use Options | File Templates.
 */
public abstract class ModelElement extends Observable {
    public abstract void stepInTime( double dt );
    
    public void updateObservers() {
        super.setChanged();
        super.notifyObservers();
    }

    public void updateObservers( Object arg ) {
        super.setChanged();
        super.notifyObservers( arg );
    }
}
