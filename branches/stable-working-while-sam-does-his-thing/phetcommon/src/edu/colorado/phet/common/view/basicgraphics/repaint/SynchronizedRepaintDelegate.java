/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.basicgraphics.repaint;

import edu.colorado.phet.common.view.basicgraphics.RepaintDelegate;

/**
 * SynchronizedRepaintDelegate
 *
 * @author Sam Reid
 * @version $Revision$
 */
public interface SynchronizedRepaintDelegate extends RepaintDelegate {
    public void finishedUpdateCycle();
}
