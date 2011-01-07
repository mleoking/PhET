// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.util;

import java.util.ArrayList;

/**
 * Simple implementation of the Observer pattern.
 *
 * @author ?
 * @version $Revision$
 */
public class SimpleObservable implements Cloneable {
    private ArrayList observers = new ArrayList();
    private SimpleObserver observerController = new SimpleObserverController( this );

    public void addObserver( SimpleObserver so ) {
        observers.add( so );
    }

    public void notifyObservers() {
        for ( int i = 0; i < observers.size(); i++ ) {
            SimpleObserver simpleObserver = (SimpleObserver) observers.get( i );
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
        return (SimpleObserver[]) observers.toArray( new SimpleObserver[0] );
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

            clone.observers = new ArrayList( observers );
            clone.observerController = new SimpleObserverController( clone );

            return clone;
        }
        catch( CloneNotSupportedException e ) {
            e.printStackTrace();

            return null;
        }
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

    private static class SimpleObserverController implements SimpleObserver {
        private final SimpleObservable observer;

        SimpleObserverController( SimpleObservable o ) {
            this.observer = o;
        }

        public void update() {
            observer.notifyObservers();
        }
    }
}

