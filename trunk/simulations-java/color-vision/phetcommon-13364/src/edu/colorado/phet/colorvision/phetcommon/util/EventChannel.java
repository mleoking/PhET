/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.colorvision.phetcommon.util;

import java.util.EventListener;

/**
 * EventChannel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface EventChannel {
    void addListener( EventListener listener );

    void removeListener( EventListener listener );

    void removeAllListeners();
//    void fireEvent( EventObject event );
//    int getNumListeners();
}
