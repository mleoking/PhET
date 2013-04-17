// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

import java.util.EventObject;

/**
 * IonEvent
 *
 * @author Ron LeMaster
 */
public class IonEvent extends EventObject {
    public IonEvent( Object source ) {
        super( source );
        if ( !( source instanceof Ion ) ) {
            throw new RuntimeException( "source of wrong type" );
        }
    }

    public Ion getIon() {
        return (Ion) getSource();
    }
}

