/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jan 1, 2006
 * Time: 5:47:15 PM
 */

public class ClearButton extends JButton {
    public ClearButton( final QWIPanel qwiPanel ) {
        super( QWIResources.getString( "controls.clear-wave" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                qwiPanel.clearWavefunction();
            }
        } );
    }
}
