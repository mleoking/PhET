package edu.colorado.phet.cck3.controls;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.common.ColorDialog;
import edu.colorado.phet.common_cck.application.PhetApplication;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:46:42 AM
 * Copyright (c) Jul 11, 2006 by Sam Reid
 */

public class ToolboxColorMenuItem extends JMenuItem {
    public ToolboxColorMenuItem( final PhetApplication application, final CCKModule cck ) {
        super( SimStrings.get( "OptionsMenu.ToolboxcolorMenuItem" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorDialog.Listener listy = new ColorDialog.Listener() {
                    public void colorChanged( Color color ) {
                        cck.getToolbox().setBackgroundColor( color );
                    }

                    public void cancelled( Color orig ) {
                        cck.getToolbox().setBackgroundColor( orig );
                    }

                    public void ok( Color color ) {
                        cck.getToolbox().setBackgroundColor( color );
                    }
                };
                ColorDialog.showDialog( SimStrings.get( "OptionsMenu.ToolboxColorDialogTitle" ),
                                        application.getApplicationView().getPhetFrame(), cck.getToolbox().getBackgroundColor(), listy );
            }
        } );
    }
}
