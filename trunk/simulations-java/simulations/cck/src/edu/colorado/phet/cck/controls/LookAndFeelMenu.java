package edu.colorado.phet.cck.controls;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.plaf.PlafUtil;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 1:03:11 AM
 *
 */

public class LookAndFeelMenu extends JMenu {
    public LookAndFeelMenu() {
        super( SimStrings.getInstance().getString( "ViewMenu.Title" ) );
        setMnemonic( SimStrings.getInstance().getString( "ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenuItem[] jmi = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < jmi.length; i++ ) {
            JMenuItem jMenuItem = jmi[i];
            add( jMenuItem );
        }
    }
}
