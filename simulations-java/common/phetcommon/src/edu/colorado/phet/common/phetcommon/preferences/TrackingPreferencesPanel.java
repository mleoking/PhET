package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * Panel for displaying preferences for the tracking feature.
 */
public class TrackingPreferencesPanel extends JPanel {

    private static final String ONE_LINER_PATTERN = PhetCommonResources.getString( "Common.tracking.oneLiner" );
    private static final String TRACKING_ENABLED = PhetCommonResources.getString( "Common.tracking.sendToPhET" );
    private static final String DETAILS = PhetCommonResources.getString( "Common.tracking.detailsButton" );
    
    private final JCheckBox trackingEnabledCheckBox;
    
    public TrackingPreferencesPanel( ITrackingInfo trackingInfo, boolean trackingEnabled ) {
        
        // feature description
        JComponent oneLiner = createOneLiner();
        
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
    private static JComponent createOneLiner() {
        Object[] args = { HTMLUtils.getPhetHomeHref( PhetCommonConstants.PHET_NAME ) };
        String s = MessageFormat.format( ONE_LINER_PATTERN, args );
        String html = HTMLUtils.createStyledHTMLFromFragment( s );
        InteractiveHTMLPane pane = new InteractiveHTMLPane( html );
        pane.setMargin( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        return pane;
    }

    /*
     * Details button opens a dialog that allows the user to view 
     * the tracking info that is sent to PhET.
     */
    private static class DetailsButton extends JButton {
        private DetailsButton( final ITrackingInfo trackingInfo ) {
            super( DETAILS );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    TrackingManager.postActionPerformedMessage( TrackingMessage.TRACKING_DETAILS_PRESSED );
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
