/**
 * Class: HelpManager
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 24, 2004
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;

public class HelpManager extends CompositeInteractiveGraphic {
    public void removeHelpItem( HelpItem helpItem ) {
        super.removeGraphic( helpItem );
    }

    public void addHelpItem( HelpItem item ) {
        super.addGraphic( item );
    }

    public void setHelpEnabled( ApparatusPanel apparatusPanel, boolean h ) {
        if( h ) {
            apparatusPanel.addGraphic( this );
        }
        else {
            apparatusPanel.removeGraphic( this );
        }
        apparatusPanel.repaint();
    }

}
