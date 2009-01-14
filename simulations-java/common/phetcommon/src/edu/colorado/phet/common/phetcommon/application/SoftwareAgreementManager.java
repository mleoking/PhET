
package edu.colorado.phet.common.phetcommon.application;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.PhetExit;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * Manages policy related to PhET's software and privacy agreements.
 */
public class SoftwareAgreementManager {

    private static final int SOFTWARE_AGREEMENT_VERSION = 1;
    
    /* not intended for instantiation */
    private SoftwareAgreementManager() {}

    /**
     * Ensures that the user has accepted the agreements that pertain to this software.
     */
    public static void validate() {
        int acceptedVersion = PhetPreferences.getInstance().getSoftwareAgreementVersion();
        if ( acceptedVersion < SOFTWARE_AGREEMENT_VERSION ) {
            negotiate();
        }
    }
    
    /*
     * Negotiates the agreement with the user.
     */
    private static void negotiate() {
        new SoftwareAgreementDialog().setVisible( true );
    }
    
    /*
     * Dialog that displays the software agreement and provides options to accept or decline.
     */
    private static class SoftwareAgreementDialog extends JDialog {

        private static final String TITLE = PhetCommonResources.getString( "Common.softwareAgreement.title" );
        private static final String ACCEPT_BUTTON = PhetCommonResources.getString( "Common.softwareAgreement.accept" );
        private static final String CANCEL_BUTTON = PhetCommonResources.getString( "Common.softwareAgreement.cancel" );
        private static final String MESSAGE = PhetCommonResources.getString( "Common.softwareAgreement.message" );
        
        public SoftwareAgreementDialog() {
            super();
            setTitle( TITLE );
            setModal( true );
            setResizable( false );

            JComponent message = createMessagePanel();
            JComponent buttonPanel = createButtonPanel();

            JPanel panel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            panel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( message, row++, column );
            layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( buttonPanel, row++, column, GridBagConstraints.HORIZONTAL );

            // close button in window dressing is identical to Cancel button
            addWindowListener( new WindowAdapter() {

                public void windowClosing( WindowEvent e ) {
                    cancel();
                }
            } );

            setContentPane( panel );
            pack();
            SwingUtils.centerWindowOnScreen( this );
        }

        private JComponent createMessagePanel() {
            String html = HTMLUtils.createStyledHTMLFromFragment( MESSAGE );
            JComponent htmlPane = new InteractiveHTMLPane( html );
            JPanel panel = new JPanel();
            panel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
            panel.add( htmlPane );
            return panel;
        }

        private JComponent createButtonPanel() {

            JButton acceptButton = new JButton( ACCEPT_BUTTON );
            acceptButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    accept();
                }
            } );

            JButton cancelButton = new JButton( CANCEL_BUTTON );
            cancelButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    cancel();
                }
            } );
            
            // layout
            JPanel panel = new JPanel( new FlowLayout() );
            panel.add( acceptButton );
            panel.add( cancelButton );
            return panel;
        }

        /*
         * If the agreement is accepted, write the agreement version number to the preferences file.
         */
        private void accept() {
            PhetPreferences.getInstance().setSoftwareAgreementVersion( SOFTWARE_AGREEMENT_VERSION );
            dispose();
        }

        /*
         * If the agreement is declined, exit, do not allow the software to run.
         */
        private void cancel() {
            PhetExit.exit();
        }
    }

    public static void main( String[] args ) {
        new SoftwareAgreementDialog().setVisible( true );
        System.out.println( "continuing" );
    }
}
