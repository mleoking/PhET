package edu.colorado.phet.physics.observation;

import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 11, 2003
 * Time: 4:58:38 PM
 * To change this template use Options | File Templates.
 */
public class ObservableDecorator {

    private PublicObservable o = new PublicObservable();

    protected void notifyObservers() {
        o.notifyObservers();
    }
    protected void setChanged() {
        o.setChanged();
    }

    protected void addObserver( Observer obj ) {
        o.addObserver( obj );
    }
}
