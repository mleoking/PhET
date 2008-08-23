/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.help;

import java.awt.*;

import edu.colorado.phet.forces1d.common_force1d.view.ApparatusPanel;
import edu.colorado.phet.forces1d.common_force1d.view.phetgraphics.GraphicLayerSet;

/**
 * HelpManager
 *
 * @author ?
 * @version $Revision$
 */
public class HelpManager extends GraphicLayerSet {
    private static double HELP_LAYER = Double.POSITIVE_INFINITY;

    public HelpManager() {
        super( null );
    }

    public HelpManager( Component component ) {
        super( component );
    }

    public void setComponent( Component component ) {
        super.setComponent( component );
    }

    public void setHelpEnabled( ApparatusPanel apparatusPanel, boolean h ) {
        if ( h ) {
            apparatusPanel.addGraphic( this, HELP_LAYER );
        }
        else {
            apparatusPanel.removeGraphic( this );
        }
        apparatusPanel.repaint();
    }

    public int getNumHelpItems() {
        return super.getNumGraphics();
    }

}
