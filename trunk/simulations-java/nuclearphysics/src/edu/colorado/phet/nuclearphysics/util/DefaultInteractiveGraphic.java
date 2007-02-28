/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.util;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;

/**
 * DefaultInteractiveGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DefaultInteractiveGraphic extends PhetGraphic {

    public DefaultInteractiveGraphic(Component component) {
        super(component);
    }

    protected Rectangle determineBounds() {
        throw new RuntimeException( "not implemented  ");
    }

    public void paint(Graphics2D g2) {
        throw new RuntimeException( "not implemented  ");
    }
}
