/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.util.SimpleObservable;

import java.util.ArrayList;

/**
 * CompositeModelElement
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class CompositeModelElement extends SimpleObservable implements ModelElement {
    private ArrayList modelElements = new ArrayList();

    public void addModelElement( ModelElement aps ) {
        modelElements.add( aps );
    }

    public ModelElement modelElementAt( int i ) {
        return (ModelElement)modelElements.get( i );
    }

    public int numModelElements() {
        return modelElements.size();
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < numModelElements(); i++ ) {
            modelElementAt( i ).stepInTime( dt );
        }
        notifyObservers();
    }

    public void removeModelElement( ModelElement m ) {
        modelElements.remove( m );
    }

    public void removeAllModelElements() {
        modelElements.clear();
    }
}
