/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class Molecule extends CompositeBody implements Collidable {

    public interface Listener extends EventListener {
        void selected( Molecule.Event event );
        void unSelected( Molecule.Event event );
    }

    EventChannel eventChannel = new EventChannel( Molecule.Listener.class );
    Listener listenerProxy = (Listener)eventChannel.getListenerProxy();

    public class Event extends EventObject {
        public Event( Molecule source ) {
            super( source );
        }

        public Molecule getMolecule() {
            return (Molecule)getSource();
        }
    }
}