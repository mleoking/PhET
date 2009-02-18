package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * Dialog uses to inform the user that no sim update is available.
 * This is used in situations where the user has manually requested an update check.
 */
public class NoSimUpdateDialog extends JDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateToDate");
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.choice.ok" );
    
    public NoSimUpdateDialog( Frame owner, String currentVersion, String simName ) {
        super( owner, TITLE );
        setModal( true );
        setResizable( false );
        
        // notification that there is no need to update
        String html = getUpToDateHTML( currentVersion, simName );
        JComponent htmlPane = new InteractiveHTMLPane( html );
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
        String htmlFragment = MessageFormat.format( PhetCommonResources.getString( "Common.updates.youHaveCurrent" ), args );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }
    
    public static void main( String[] args ) {
        // dialog must have an owner if you want cursor to change over hyperlinks
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
        JDialog dialog = new NoSimUpdateDialog( frame, "1.01", "Glaciers" );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        dialog.setVisible( true );
    }

}
