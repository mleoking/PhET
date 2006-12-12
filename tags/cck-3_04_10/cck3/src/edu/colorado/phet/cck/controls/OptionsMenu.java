package edu.colorado.phet.cck.controls;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 1:05:36 AM
 * Copyright (c) Jul 11, 2006 by Sam Reid
 */

public class OptionsMenu extends JMenu {
    public OptionsMenu( PhetApplication application, ICCKModule cck ) {
        super( SimStrings.get( "OptionsMenu.Title" ) );
        setMnemonic( SimStrings.get( "OptionsMenu.TitleMnemonic" ).charAt( 0 ) );
//        cck.setFrame( application.getApplicationView().getPhetFrame() );
        add( new BackgroundColorMenuItem( application, cck ) );
        add( new ToolboxColorMenuItem( application, cck ) );
    }
}
