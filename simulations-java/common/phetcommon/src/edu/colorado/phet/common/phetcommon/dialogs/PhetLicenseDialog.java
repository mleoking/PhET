/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.DefaultResourceLoader;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * Dialog that displays the PhET license.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetLicenseDialog extends JDialog {
    
    // Resource (file) that contains the PhET license, in plain text format.
    private static final String LICENSE_RESOURCE = "phet-license.txt";
    
    private static final String TITLE = PhetCommonResources.getString( "Common.About.LicenseDialog.Title" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.About.OKButton" );
    
    public PhetLicenseDialog( Dialog owner ) throws IOException {
        super( owner, TITLE, true /* modal */ );
        
        // license in a scroll pane
        String phetLicenseString = new DefaultResourceLoader().getResourceAsString( LICENSE_RESOURCE );
        String phetLicenseHTML = HTMLUtils.setFontInStyledHTML( phetLicenseString, new PhetFont() );
        InteractiveHTMLPane htmlPane = new InteractiveHTMLPane( phetLicenseHTML );
        JScrollPane scrollPane = new JScrollPane( htmlPane );

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

        // layout
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        panel.add( scrollPane, BorderLayout.CENTER );
        panel.add( buttonPanel, BorderLayout.SOUTH );
        setContentPane( panel );
        setSize( 440,400 );//todo: this shouldn't be hard coded, but I had trouble getting Swing to do something reasonable
        SwingUtils.centerDialogInParent( this );
    }

}
