package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:46:42 AM
 */

public class ToolboxColorMenuItem extends JMenuItem {
    public ToolboxColorMenuItem( final PhetApplication application, final CCKModule cck ) {
        super( CCKResources.getString( "OptionsMenu.ToolboxcolorMenuItem" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorChooserFactory.Listener listy = new ColorChooserFactory.Listener() {
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
                ColorChooserFactory.showDialog( CCKResources.getString( "OptionsMenu.ToolboxColorDialogTitle" ),
                                                application.getPhetFrame(), cck.getToolboxBackgroundColor(), listy );
            }
        } );
    }
}
