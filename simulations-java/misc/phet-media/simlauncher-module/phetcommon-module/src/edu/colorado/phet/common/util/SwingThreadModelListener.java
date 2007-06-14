/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/util/SwingThreadModelListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/01/31 18:47:24 $
 */
package edu.colorado.phet.common.util;

import java.util.EventListener;

/**
 * SwingThreadEventListener
 * <p>
 * A marker interface used to distinguish EventListeners that run in the
 * Swing dispatch queue thread.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public interface SwingThreadModelListener extends EventListener {
}
