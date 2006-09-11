package edu.colorado.phet.common_cck.model;

import edu.colorado.phet.common_cck.util.SimpleObservable;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 5:38:59 PM
 * To change this template use Options | File Templates.
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
