package edu.colorado.phet.common_1200.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 5:38:59 PM
 * To change this template use Options | File Templates.
 */
public class CompositeModelElement extends SimpleObservable implements edu.colorado.phet.common.phetcommon.model.ModelElement {
    private ArrayList modelElements = new ArrayList();

    public void addModelElement( edu.colorado.phet.common.phetcommon.model.ModelElement aps ) {
        modelElements.add( aps );
    }

    public edu.colorado.phet.common.phetcommon.model.ModelElement modelElementAt( int i ) {
        return (edu.colorado.phet.common.phetcommon.model.ModelElement)modelElements.get( i );
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

    public void removeModelElement( edu.colorado.phet.common.phetcommon.model.ModelElement m ) {
        modelElements.remove( m );
    }

    public void removeAllModelElements() {
        modelElements.clear();
    }
}
