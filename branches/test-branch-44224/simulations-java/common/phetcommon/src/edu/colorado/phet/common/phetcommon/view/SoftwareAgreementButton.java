package edu.colorado.phet.common.phetcommon.view;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.application.SoftwareAgreementDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Pressing this button displays the Software Agreement in a dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoftwareAgreementButton extends JButton {

    private static final String LABEL = PhetCommonResources.getString( "Common.About.AgreementButton" );
    
    public SoftwareAgreementButton( final Dialog parent ) {
        setText( LABEL );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new SoftwareAgreementDialog( parent ).setVisible( true );
            }
        } );
    }
}
