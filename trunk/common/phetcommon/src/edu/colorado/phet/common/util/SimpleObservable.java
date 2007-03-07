/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import java.util.ArrayList;

/**
 * Simple implementation of the Observer pattern.
 *
 * @author ?
 * @version $Revision$
 */
public class SimpleObservable {
    private ArrayList observers = new ArrayList();
    private SimpleObserver observerController = new SimpleObserverController();

    public void addObserver( SimpleObserver so ) {
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

    //////////////////////////////////////////////////
    // Persistence support
    //

    /**
     * No-arg contructor for Java Bean conformance
     */
    public SimpleObservable() {
    }

    /**
     * Setter for Java Bean conformance
     *
     * @param observers
     */
    public void setObserverList( ArrayList observers ) {
        this.observers = observers;
    }

    /**
     * Getter for Java Bean conformance
     *
     * @return a reference to the list of observers.
     */
    public ArrayList getObserverList() {
        return observers;
    }

    public SimpleObserver getController() {
        return observerController;
    }

    private class SimpleObserverController implements SimpleObserver {
        public void update() {
            notifyObservers();
        }
    }
}

