/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
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
 * @version $Revision$
 */
public interface SwingThreadModelListener extends EventListener {
}
