/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.ion;

import java.util.EventObject;

/**
 * IonEvent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonEvent extends EventObject {
    public IonEvent( Object source ) {
        super( source );
        if( !( source instanceof Ion ) ) {
            throw new RuntimeException( "source of wrong type" );
        }
    }

    public Ion getIon() {
        return (Ion)getSource();
    }
}

