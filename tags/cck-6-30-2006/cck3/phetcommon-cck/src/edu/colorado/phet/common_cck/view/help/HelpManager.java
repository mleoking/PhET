/**
 * Class: HelpManager
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 24, 2004
 */
package edu.colorado.phet.common_cck.view.help;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.CompositeGraphic;

public class HelpManager extends CompositeGraphic {
    double helpLayer = Double.POSITIVE_INFINITY;

    public void removeHelpItem( HelpItem helpItem ) {
        super.removeGraphic( helpItem );
    }

    public void addHelpItem( HelpItem item ) {
        super.addGraphic( item );
    }

    public void setHelpEnabled( ApparatusPanel apparatusPanel, boolean h ) {
        if( h ) {
            apparatusPanel.addGraphic( this, helpLayer );
        }
        else {
            apparatusPanel.removeGraphic( this );
        }
        apparatusPanel.repaint();
    }

}
