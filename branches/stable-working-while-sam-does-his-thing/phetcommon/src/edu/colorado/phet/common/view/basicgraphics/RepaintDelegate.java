/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.basicgraphics;

import java.awt.*;

/**
 * RepaintDelegate
 *
 * @author Sam Reid
 * @version $Revision$
 */
public interface RepaintDelegate {
    public void repaint( Component component, Rectangle rect );
}
