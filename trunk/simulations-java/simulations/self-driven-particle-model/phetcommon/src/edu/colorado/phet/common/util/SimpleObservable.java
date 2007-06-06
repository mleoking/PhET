/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/SimpleObservable.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.util;

import java.util.ArrayList;

/**
 * SimpleObservable
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class SimpleObservable {
    private ArrayList observers = new ArrayList();

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
     * @return
     */
    public ArrayList getObserverList() {
        return observers;
    }
}

