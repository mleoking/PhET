package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.text.MessageFormat;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.HTMLEditorPane;

/**
 * Panel for displaying preferences related to privacy.
 */
public class PrivacyPreferencesPanel extends JPanel {

    private static final String TRACKING_ENABLED = PhetCommonResources.getString( "Common.tracking.enabled" );
    
    private final PhetPreferences preferences;
    private final JCheckBox trackingEnabledCheckBox;
    private final JCheckBox alwaysShowSoftwareAgreementCheckBox;
    
    public PrivacyPreferencesPanel( ITrackingInfo trackingInfo, PhetPreferences preferences, boolean isDev ) {
        
        this.preferences = preferences;
        
        // feature description
        JComponent description = new DescriptionPane( trackingInfo );
        
        // enable check box
        trackingEnabledCheckBox = new JCheckBox( TRACKING_ENABLED, preferences.isTrackingEnabled() );
        
        // developer control to always show the software agreement dialog, not localized
        alwaysShowSoftwareAgreementCheckBox = new JCheckBox( "Always show Software Agreement (dev)", preferences.isAlwaysShowSoftwareAgreement() );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) );
        int row = 0;
        int column = 0;
        layout.addComponent( description, row++, column );
        layout.addComponent( trackingEnabledCheckBox, row++, column );
        if ( isDev ) {
            layout.addComponent( alwaysShowSoftwareAgreementCheckBox, row++, column ); 
        }
    }
    
    /**
     * Saves the preference values in this panel.
     */
    public void save() {
        preferences.setTrackingEnabled( trackingEnabledCheckBox.isSelected() );
        preferences.setAlwaysShowSoftwareAgreement( alwaysShowSoftwareAgreementCheckBox.isSelected() );
    }

    /*
     * This is an HTML editor pane with interactive hyperlinks.
     * But instead of opening a web browser, it opens a Swing dialog.
     */
    private static class DescriptionPane extends HTMLEditorPane {
        
        private static final String DESCRIPTION_PATTERN = PhetCommonResources.getString( "Common.tracking.description" );

        // identifiers for hyperlink actions
        private static final String LINK_SHOW_TRACKING_DETAILS = "showTrackingDetails";

        public DescriptionPane( final ITrackingInfo trackingInfo ) {
            super( "" );
            
            // insert our own hyperlink descriptions into the message, so translators can't mess them up
            Object[] args = { LINK_SHOW_TRACKING_DETAILS };
            String htmlFragment = MessageFormat.format( DESCRIPTION_PATTERN, args );
            setText( HTMLUtils.createStyledHTMLFromFragment( htmlFragment ) );
            
            addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        if ( e.getDescription().equals( LINK_SHOW_TRACKING_DETAILS ) ) {
                            showTrackingDetails( SwingUtilities.getWindowAncestor( DescriptionPane.this ), trackingInfo );
                        }
                        else {
                            System.err.println( "PrivacyPreferencesPanel.DescriptionPane.hyperlinkUpdate: unsupported hyperlink, description=" + e.getDescription() );
                        }
                    }
                }
            } );
        }
        
        private static void showTrackingDetails( Window owner, ITrackingInfo trackingInfo ) {
            if ( owner instanceof Frame ) {
                new TrackingDetailsDialog( (Frame) owner, trackingInfo ).setVisible( true );
            }
            else if ( owner instanceof Dialog ) {
                new TrackingDetailsDialog( (Dialog) owner, trackingInfo ).setVisible( true );
            }
        }
    }
}
