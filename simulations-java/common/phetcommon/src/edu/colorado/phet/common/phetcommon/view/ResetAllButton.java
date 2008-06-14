/* Copyright 2008, University of Colorado */ 

package edu.colorado.phet.common.phetcommon.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;

/**
 * "Reset All" button.  When it's pressed, requests confirmation.
 * It confirmation is affirmative, then all Resettables are rest.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResetAllButton extends JButton {
    
    public ResetAllButton( final Resettable resettable, final Component parent ) {
        this( new Resettable[] { resettable }, parent );
    }
    
    public ResetAllButton( final Resettable[] resettables, final Component parent ) {
        super();
        
        // set text to "Reset All"
        setText( PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_RESET_ALL ) );
        
        // When the button is pressed, request confirmation and reset all Resettables
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String message = PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.message.confirmResetAll" );
                int option = DialogUtils.showConfirmDialog( parent, message, JOptionPane.YES_NO_OPTION );
                if ( option == JOptionPane.YES_OPTION ) {
                    for ( int i = 0; i < resettables.length; i++ ) {
                        resettables[i].reset();
                    }
                }
            }
        } );
    }

}
