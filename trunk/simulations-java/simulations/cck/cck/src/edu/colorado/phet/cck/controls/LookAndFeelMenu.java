package edu.colorado.phet.cck.controls;

import edu.colorado.phet.cck.CCKResources;
import edu.colorado.phet.common_cck.view.plaf.PlafUtil;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 1:03:11 AM
 */

public class LookAndFeelMenu extends JMenu {
    public LookAndFeelMenu() {
        super( CCKResources.getString( "ViewMenu.Title" ) );
        setMnemonic( CCKResources.getString( "ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenuItem[] jmi = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < jmi.length; i++ ) {
            JMenuItem jMenuItem = jmi[i];
            add( jMenuItem );
        }
    }
}
