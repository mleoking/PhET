package edu.colorado.phet.cck3.controls;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.plaf.PlafUtil;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 1:03:11 AM
 * Copyright (c) Jul 11, 2006 by Sam Reid
 */

public class LookAndFeelMenu extends JMenu {
    public LookAndFeelMenu() {
        super( SimStrings.get( "ViewMenu.Title" ) );
        setMnemonic( SimStrings.get( "ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenuItem[] jmi = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < jmi.length; i++ ) {
            JMenuItem jMenuItem = jmi[i];
            add( jMenuItem );
        }
    }
}
