/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that displays simulation credits.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CreditsDialog extends JDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.About.CreditsDialog.Title" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.About.OKButton" );

    public CreditsDialog( Dialog parent, String creditsString ) {
        super( parent, TITLE, true /* modal */ );
        setResizable( false );
        
        // Credits
        JLabel creditsLabel = new JLabel( creditsString );
        
        // OK button
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton( OK_BUTTON );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
                dispose();
            }
        } );
        buttonPanel.add( okButton );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        layout.addComponent( creditsLabel, row++, 0 );
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addAnchoredComponent( buttonPanel, row++, 0, GridBagConstraints.CENTER );
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
}
