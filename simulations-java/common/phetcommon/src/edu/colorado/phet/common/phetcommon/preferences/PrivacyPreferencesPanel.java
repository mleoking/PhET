package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

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
        
        // enable check box
        trackingEnabledCheckBox = new JCheckBox( TRACKING_ENABLED, trackingEnabled );
        
        // details button
        DetailsButton detailsButton = new DetailsButton( trackingInfo );

        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) );
        int row = 0;
        int column = 0;
        layout.addAnchoredComponent( oneLiner, row++, column, GridBagConstraints.WEST);
        layout.addAnchoredComponent( trackingEnabledCheckBox, row++, column, GridBagConstraints.WEST );
        layout.addAnchoredComponent( detailsButton, row++, column, GridBagConstraints.EAST );
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
