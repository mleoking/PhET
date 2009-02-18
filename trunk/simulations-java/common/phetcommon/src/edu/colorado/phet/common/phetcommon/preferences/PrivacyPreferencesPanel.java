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
import edu.colorado.phet.common.phetcommon.statistics.SessionMessage;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.HTMLEditorPane;

/**
 * Panel for displaying preferences related to privacy.
 */
public class PrivacyPreferencesPanel extends JPanel {

    private static final String STATISTICS_ENABLED = PhetCommonResources.getString( "Common.statistics.enabled" );
    
    private final PhetPreferences preferences;
    private final JCheckBox statisticsEnabledCheckBox;
    private final JCheckBox alwaysShowSoftwareAgreementCheckBox;

    public PrivacyPreferencesPanel( SessionMessage sessionMessage, PhetPreferences preferences, boolean isDev ) {
        
        this.preferences = preferences;
        
        // feature description
        JComponent description = new DescriptionPane( sessionMessage );
        
        // enable check box
        statisticsEnabledCheckBox = new JCheckBox( STATISTICS_ENABLED, preferences.isStatisticsEnabled() );
        
        // developer control to always show the software agreement dialog, not localized
        alwaysShowSoftwareAgreementCheckBox = new JCheckBox( "Always show Software Agreement (dev)", preferences.isAlwaysShowSoftwareAgreement() );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) );
        int row = 0;
        int column = 0;
        layout.addComponent( description, row++, column );
        layout.addComponent( statisticsEnabledCheckBox, row++, column );
        if ( isDev ) {
            layout.addComponent( alwaysShowSoftwareAgreementCheckBox, row++, column ); 
        }
    }
    
    /**
     * Saves the preference values in this panel.
     */
    public void save() {
        preferences.setStatisticsEnabled( statisticsEnabledCheckBox.isSelected() );
        preferences.setAlwaysShowSoftwareAgreement( alwaysShowSoftwareAgreementCheckBox.isSelected() );
    }

    /*
     * This is an HTML editor pane with interactive hyperlinks.
     * But instead of opening a web browser, it opens a Swing dialog.
     */
    private static class DescriptionPane extends HTMLEditorPane {
        
        private static final String DESCRIPTION_PATTERN = PhetCommonResources.getString( "Common.statistics.description" );

        // identifiers for hyperlink actions
        private static final String LINK_SHOW_STATISTICS_DETAILS = "showStatisticsDetails";

        public DescriptionPane( final SessionMessage sessionMessage ) {
            super( "" );
            
            // insert our own hyperlink descriptions into the message, so translators can't mess them up
            Object[] args = {LINK_SHOW_STATISTICS_DETAILS};
            String htmlFragment = MessageFormat.format( DESCRIPTION_PATTERN, args );
            setText( HTMLUtils.createStyledHTMLFromFragment( htmlFragment ) );
            
            addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        if ( e.getDescription().equals( LINK_SHOW_STATISTICS_DETAILS ) ) {
                            showStatisticsDetails( SwingUtilities.getWindowAncestor( DescriptionPane.this ), sessionMessage );
                        }
                        else {
                            System.err.println( "PrivacyPreferencesPanel.DescriptionPane.hyperlinkUpdate: unsupported hyperlink, description=" + e.getDescription() );
                        }
                    }
                }
            } );
        }
        
        private static void showStatisticsDetails( Window owner, SessionMessage sessionMessage ) {
            if ( owner instanceof Frame ) {
                new StatisticsDetailsDialog( (Frame) owner, sessionMessage ).setVisible( true );
            }
            else if ( owner instanceof Dialog ) {
                new StatisticsDetailsDialog( (Dialog) owner, sessionMessage ).setVisible( true );
            }
        }
    }
}
