/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/model/CompositeModelElement.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.util.SimpleObservable;

import java.util.ArrayList;

/**
 * CompositeModelElement
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class CompositeModelElement extends SimpleObservable implements ModelElement {
    private ArrayList modelElements = new ArrayList();

    public void addModelElement( ModelElement aps ) {
        modelElements.add( aps );
    }

    public ModelElement modelElementAt( int i ) {
        return (ModelElement)modelElements.get( i );
    }

    public boolean containsModelElement( ModelElement modelElement ) {
        return modelElements.contains( modelElement );
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
