package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;

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

    private static final String DESCRIPTION_STRING = PhetCommonResources.getString( "Common.tracking.description" );
    private static final String TRACKING_ENABLED = PhetCommonResources.getString( "Common.tracking.enabled" );
    
    private final JCheckBox trackingEnabledCheckBox;
    
    public PrivacyPreferencesPanel( ITrackingInfo trackingInfo, boolean trackingEnabled ) {
        
        // feature description
        String html = HTMLUtils.createStyledHTMLFromFragment( DESCRIPTION_STRING );
        JComponent description = new DescriptionPane( html, trackingInfo );
        
        // enable check box
        trackingEnabledCheckBox = new JCheckBox( TRACKING_ENABLED, trackingEnabled );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) );
        int row = 0;
        int column = 0;
        layout.addComponent( description, row++, column );
        layout.addComponent( trackingEnabledCheckBox, row++, column );
    }
    
    public boolean isTrackingEnabled() {
        return trackingEnabledCheckBox.isSelected();
    }

    /*
     * This is an HTML editor pane interactive hyperlinks.
     * But instead of opening a web browser, it opens a Swing dialog.
     */
    private static class DescriptionPane extends HTMLEditorPane {
        public DescriptionPane( String html, final ITrackingInfo trackingInfo ) {
            super( html );
            addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        // This assumes there's only one hyperlink in the description text.
                        // If more links are added, you'll need to check the link in the event.
                        Window window = SwingUtilities.getWindowAncestor( DescriptionPane.this );
                        if ( window instanceof Frame ) {
                            new TrackingDetailsDialog( (Frame) window, trackingInfo ).setVisible( true );
                        }
                        else if ( window instanceof Dialog ) {
                            new TrackingDetailsDialog( (Dialog) window, trackingInfo ).setVisible( true );
                        }
                    }
                }
            } );
        }
    }
}
