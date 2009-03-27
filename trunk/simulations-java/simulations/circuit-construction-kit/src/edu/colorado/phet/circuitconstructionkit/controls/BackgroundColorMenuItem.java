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
 * Time: 12:48:55 AM
 */

public class BackgroundColorMenuItem extends JMenuItem {
    public BackgroundColorMenuItem( final PhetApplication application, final CCKModule cck ) {
        super( CCKResources.getString( "OptionsMenu.BackgroundColorMenuItem" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorChooserFactory.Listener listy = new ColorChooserFactory.Listener() {
                    public void colorChanged( Color color ) {
                        cck.setMyBackground( color );
                    }

                    public void cancelled( Color orig ) {
                        cck.setMyBackground( orig );
                    }

                    public void ok( Color color ) {
                        cck.setMyBackground( color );
                    }
                };

                ColorChooserFactory.showDialog( CCKResources.getString( "OptionsMenu.BackgroundColorDialogTitle" ),
                                                application.getPhetFrame(), cck.getMyBackground(), listy );
            }
        } );
    }
}
