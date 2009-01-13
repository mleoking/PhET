package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;

/**
 * Panel for displaying preferences related to privacy.
 */
public class PrivacyPreferencesPanel extends JPanel {

    private static final String DESCRIPTION_STRING = PhetCommonResources.getString( "Common.tracking.description" );
    private static final String TRACKING_ENABLED = PhetCommonResources.getString( "Common.tracking.enabled" );
    private static final String DETAILS_BUTTON = PhetCommonResources.getString( "Common.tracking.details.button" );
    
    private final JCheckBox trackingEnabledCheckBox;
    
    public PrivacyPreferencesPanel( ITrackingInfo trackingInfo, boolean trackingEnabled ) {
        
        // feature description
        JComponent oneLiner = createDescription();
        
        // enable
        trackingEnabledCheckBox = new JCheckBox( TRACKING_ENABLED, trackingEnabled );
        
        // details
        DetailsButton detailsButton = new DetailsButton( trackingInfo );

        // layout
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        add( oneLiner, constraints );
        add( Box.createRigidArea( new Dimension( 5, 10 ) ), constraints );
        add( trackingEnabledCheckBox, constraints );
        add( Box.createRigidArea( new Dimension( 5, 10 ) ), constraints );
        add( detailsButton, constraints );
    }
    
    public boolean isTrackingEnabled() {
        return trackingEnabledCheckBox.isSelected();
    }

    /* One-line description of tracking */
    private static JComponent createDescription() {
        return new JLabel( DESCRIPTION_STRING );
    }

    /*
     * Details button opens a dialog that allows the user to view 
     * the tracking info that is sent to PhET.
     */
    private static class DetailsButton extends JButton {
        private DetailsButton( final ITrackingInfo trackingInfo ) {
            super( DETAILS_BUTTON );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    Window window = SwingUtilities.getWindowAncestor( DetailsButton.this );
                    if ( window instanceof Frame ) {
                        new TrackingDetailsDialog( (Frame) window, trackingInfo ).setVisible( true );
                    }
                    else if ( window instanceof Dialog ) {
                        new TrackingDetailsDialog( (Dialog) window, trackingInfo ).setVisible( true );
                    }
                }
            } );
        }
    }
}
