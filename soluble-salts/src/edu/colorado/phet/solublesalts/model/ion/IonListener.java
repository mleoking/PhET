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

import edu.colorado.phet.solublesalts.model.ion.IonEvent;

import java.util.EventListener;

/**
 * IonListener
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface IonListener extends EventListener {
    void ionAdded( IonEvent event );

    void ionRemoved( IonEvent event );
}

