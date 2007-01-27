/**
 * Class: EnergyLevelsDialog
 * Package: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 10:41:27 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EnergyLevelsDialog extends JDialog {

    public EnergyLevelsDialog( Frame parent, JPanel energyLevelsPanel ) {
        super( parent, SimStrings.get( "EnergyLevelsDialog.Title" ) );

        // Make the window plain, with no way to close it
        this.setUndecorated( true );
        this.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );

        addWindowListener( new WindowAdapter() {
            public void windowClosed( WindowEvent e ) {
            }
        } );

        this.setResizable( false );
        this.setContentPane( energyLevelsPanel );
        this.pack();
    }
}
