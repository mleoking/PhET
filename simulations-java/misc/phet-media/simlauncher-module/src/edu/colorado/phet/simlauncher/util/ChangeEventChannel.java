/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/util/ChangeEventChannel.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.3 $
 * Date modified : $Date: 2006/06/09 00:31:40 $
 */
package edu.colorado.phet.simlauncher.util;

import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.EventObject;

/**
 * ChangeEventChannel
 *
 * @author Ron LeMaster
 * @version $Revision: 1.3 $
 */
public class ChangeEventChannel extends EventChannel {

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }
    }

    public interface ChangeEventSource {
        public void addChangeListener( ChangeEventChannel.ChangeListener listener );

        public void removeChangeListener( ChangeEventChannel.ChangeListener listener );
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

    public ChangeEventChannel() {
        super( ChangeListener.class );
    }

    public void notifyChangeListeners( Object source ) {
        ( (ChangeListener)getListenerProxy() ).stateChanged( new ChangeEvent( source ) );
    }
}
