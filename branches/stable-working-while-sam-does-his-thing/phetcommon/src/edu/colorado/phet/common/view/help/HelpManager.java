/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeGraphic;

import java.awt.*;

/**
 * HelpManager
 *
 * @author ?
 * @version $Revision$
 */
public class HelpManager extends CompositeGraphic {
//public class HelpManager extends GraphicLayerSet {
    private static double HELP_LAYER = Double.POSITIVE_INFINITY;
    private int numHelpItems;

    public HelpManager() {
//        super( null );
    }

    public HelpManager( Component component ) {
//        super( component );
    }

//    public void setComponent( Component component ) {
//        super.setComponent( component );
//    }

    public void removeHelpItem( HelpItem helpItem ) {
        super.removeGraphic( helpItem );
        numHelpItems--;
    }

    public void addHelpItem( HelpItem item ) {
        super.addGraphic( item );
        numHelpItems++;
    }

    public void setHelpEnabled( ApparatusPanel apparatusPanel, boolean h ) {
        if( h ) {
            apparatusPanel.addGraphic( this, HELP_LAYER );
        }
        else {
            apparatusPanel.removeGraphic( this );
        }
        apparatusPanel.repaint();
    }

    public int getNumHelpItems() {
        return numHelpItems;
    }

}
