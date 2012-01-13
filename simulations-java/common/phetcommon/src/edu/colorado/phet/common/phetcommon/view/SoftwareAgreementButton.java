// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.application.SoftwareAgreementDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJButton;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents.aboutDialogSoftwareAgreementButton;

/**
 * Pressing this button displays the Software Agreement in a dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoftwareAgreementButton extends SimSharingJButton {

    private static final String LABEL = PhetCommonResources.getString( "Common.About.AgreementButton" );

    public SoftwareAgreementButton( final Dialog parent ) {
        super( aboutDialogSoftwareAgreementButton );
        setText( LABEL );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new SoftwareAgreementDialog( parent ).setVisible( true );
            }
        } );
    }
}