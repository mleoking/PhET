package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.Date;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetInstallerVersion;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * Dialog uses to inform the user that no sim update is available.
 * This is used in situations where the user has manually requested an update check.
 */
public abstract class NoUpdateDialog extends PaintImmediateDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateToDate");
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.choice.ok" );
    private static final String MESSAGE_PATTERN = PhetCommonResources.getString( "Common.updates.youHaveCurrent" );
    private static final String PHET_INSTALLER = PhetCommonResources.getString( "Common.phetInstaller" );
    
    public static class SimNoUpdateDialog extends NoUpdateDialog {
        public SimNoUpdateDialog( Frame owner, String simName, PhetVersion currentVersion ) {
            super( owner, simName, currentVersion.formatMajorMinor() );
        }
    }
    
    public static class InstallerNoUpdateDialog extends NoUpdateDialog {
        public InstallerNoUpdateDialog( Frame owner, PhetInstallerVersion currentVersion ) {
            super( owner, PHET_INSTALLER, currentVersion.formatTimestamp() );
        }
    }
    
    protected NoUpdateDialog( Frame owner, String productName, String currentVersion ) {
        super( owner, TITLE );
        setModal( true );
        setResizable( false );
        
        // notification that there is no need to update
        String html = getUpToDateHTML( currentVersion, productName );
        JComponent htmlPane = new InteractiveHTMLPane( html );
        htmlPane.setBackground( new JPanel().getBackground() ); // see #1532
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        messagePanel.add( htmlPane );
        
        // closes the dialog
        JButton okButton = new JButton( OK_BUTTON );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( okButton );
        
        // main panel layout
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( messagePanel, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addAnchoredComponent( buttonPanel, row++, column, GridBagConstraints.CENTER );
        
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private static String getUpToDateHTML( String currentVersion, String simName ) {
        Object[] args = { currentVersion, simName };
        String htmlFragment = MessageFormat.format( MESSAGE_PATTERN, args );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }
    
    public static void main( String[] args ) {
        
        PhetVersion version = new PhetVersion( "1", "01", "02", "123456789", String.valueOf( new Date().getTime()/1000L ) );
        JDialog dialog = new SimNoUpdateDialog( null, "Glaciers", version );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.setVisible( true );
        
        PhetInstallerVersion installerVersion = new PhetInstallerVersion( new Date().getTime()/1000L );
        JDialog dialog2 = new InstallerNoUpdateDialog( null, installerVersion );
        dialog2.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        SwingUtils.centerWindowOnScreen( dialog2 );
        dialog2.setVisible( true );
    }

}
