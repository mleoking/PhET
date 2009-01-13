package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that appears when you press the "Details" button in the Tracking preferences panel.
 */
public class TrackingDetailsDialog extends JDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.tracking.details.title" );
    private static final String DESCRIPTION = PhetCommonResources.getString( "Common.tracking.details.description" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.choice.ok" );
    
    public TrackingDetailsDialog( Dialog owner, ITrackingInfo trackingInfo ) {
        super( owner );
        init( trackingInfo );
    }

    public TrackingDetailsDialog( Frame owner, ITrackingInfo trackingInfo ) {
        super( owner );
        init( trackingInfo );
    }

    private void init( ITrackingInfo trackingInfo ) {
        setTitle( TITLE );
        setModal( true );
        setResizable( false );
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 8, 2, 8, 2 ) );
        panel.add( createDescription(), constraints );
        panel.add( createReport( trackingInfo ), constraints );
        panel.add( createButtonPanel(), constraints );
        getContentPane().add( panel );
        
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private static JComponent createDescription() {
        return new JLabel( DESCRIPTION );
    }
    
    //TODO report should be in a JScrollPane to handle future reports that may be longer
    private static JComponent createReport( ITrackingInfo trackingInfo ) {
        final JTextArea jt = new JTextArea( "" );
        if ( trackingInfo.getHumanReadableTrackingInformation() != null ) {
            jt.setText( trackingInfo.getHumanReadableTrackingInformation() );
        }
        jt.setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
        jt.setEditable( false );
        return jt;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton okButton = new JButton( OK_BUTTON );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        });
        panel.add( okButton );
        return panel;
    }
}
