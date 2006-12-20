package edu.colorado.phet.cck.controls;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.ColorDialog;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;

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
    public ToolboxColorMenuItem( final PhetApplication application, final ICCKModule cck ) {
        super( SimStrings.get( "OptionsMenu.ToolboxcolorMenuItem" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorDialog.Listener listy = new ColorDialog.Listener() {
                    public void colorChanged( Color color ) {
                        cck.setToolboxBackgroundColor( color );
                    }

                    public void cancelled( Color orig ) {
                        cck.setToolboxBackgroundColor( orig );
                    }

                    public void ok( Color color ) {
                        cck.setToolboxBackgroundColor( color );
                    }
                };
                ColorDialog.showDialog( SimStrings.get( "OptionsMenu.ToolboxColorDialogTitle" ),
                                        application.getPhetFrame(), cck.getToolboxBackgroundColor(), listy );
            }
        } );
    }
}
