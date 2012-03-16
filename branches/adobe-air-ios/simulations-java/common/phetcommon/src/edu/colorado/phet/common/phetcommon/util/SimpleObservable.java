// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.util;

import java.util.ArrayList;

/**
 * Simple implementation of the Observer pattern.
 *
 * @author Sam Reid
 * @author John De Goes
 * @author Ron LeMaster
 */
public class SimpleObservable implements Cloneable {

    private ArrayList<SimpleObserver> observers = new ArrayList<SimpleObserver>();

    public void addObserver( SimpleObserver observer ) {
        observers.add( observer );
    }

    public void notifyObservers() {
        //Iterate on a copy of the observer list to avoid ConcurrentModificationException, see #2741
        for ( SimpleObserver observer : new ArrayList<SimpleObserver>( observers ) ) {
            observer.update();
        }
    }

    public void removeObserver( SimpleObserver observer ) {
        observers.remove( observer );
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

    //////////////////////////////////////////////////
    // Persistence support
    //

    /**
     * No-arg contructor for Java Bean conformance
     */
    public SimpleObservable() {
    }


    public Object clone() {
        try {
            SimpleObservable clone = (SimpleObservable) super.clone();
            clone.observers = new ArrayList<SimpleObserver>( observers );
            return clone;
        }
        catch ( CloneNotSupportedException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Setter for Java Bean conformance
     *
     * @param observers
     */
    public void setObserverList( ArrayList<SimpleObserver> observers ) {
        this.observers = observers;
    }

    /**
     * Getter for Java Bean conformance
     *
     * @return a reference to the list of observers.
     */
    public ArrayList<SimpleObserver> getObserverList() {
        return observers;
    }
}