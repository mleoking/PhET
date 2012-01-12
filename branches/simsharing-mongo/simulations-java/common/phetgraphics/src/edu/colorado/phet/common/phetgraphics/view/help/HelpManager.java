// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetgraphics.view.help;

import java.awt.Component;

import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;

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

    /**
     * @param helpItem
     * @deprecated use removeGraphic
     */
    public void removeHelpItem( HelpItem helpItem ) {
        super.removeGraphic( helpItem );
    }

    /**
     * @param item
     * @deprecated use addGraphic
     */
    public void addHelpItem( HelpItem item ) {
        super.addGraphic( item, HELP_LAYER );
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
