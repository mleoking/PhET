// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

import java.util.EventListener;

/**
 * IonListener
 *
 * @author Ron LeMaster
 */
public interface IonListener extends EventListener {
    void ionAdded( IonEvent event );

    void ionRemoved( IonEvent event );
}

