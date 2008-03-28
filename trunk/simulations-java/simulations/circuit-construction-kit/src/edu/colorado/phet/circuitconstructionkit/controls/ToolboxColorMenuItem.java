package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.ICCKModule;
import edu.colorado.phet.circuitconstructionkit.common.ColorDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:46:42 AM
 */

public class ToolboxColorMenuItem extends JMenuItem {
    public ToolboxColorMenuItem( final PhetApplication application, final ICCKModule cck ) {
        super( CCKResources.getString( "OptionsMenu.ToolboxcolorMenuItem" ) );
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
                ColorDialog.showDialog( CCKResources.getString( "OptionsMenu.ToolboxColorDialogTitle" ),
                                        application.getPhetFrame(), cck.getToolboxBackgroundColor(), listy );
            }
        } );
    }
}
