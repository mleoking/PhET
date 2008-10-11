package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.updates.dialogs.AbstractUpdateDialog;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

public class TrackingPreferencesPanel extends JPanel {

    private static final String ONE_LINER_PATTERN = PhetCommonResources.getString( "Common.tracking.oneLiner" );
    private static final String TRACKING_ENABLED = PhetCommonResources.getString( "Common.tracking.sendToPhET" );
    private static final String DETAILS = PhetCommonResources.getString( "Common.tracking.detailsButton" );
    
    private ITrackingInfo tracker;

    private JCheckBox trackingEnabledCheckBox;
    
    public TrackingPreferencesPanel( ITrackingInfo tracker, boolean trackingEnabled ) {
        this.tracker = tracker;
        
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        
        trackingEnabledCheckBox = new JCheckBox( TRACKING_ENABLED, trackingEnabled );

        add( createLogoPanel(), constraints );
        add( Box.createRigidArea( new Dimension( 5, 10 ) ), constraints );
        add( trackingEnabledCheckBox, constraints );
        add( Box.createRigidArea( new Dimension( 5, 10 ) ), constraints );
        add( new DetailsButton(), constraints );
    }
    
    public boolean isTrackingEnabled() {
        return trackingEnabledCheckBox.isSelected();
    }

    /*
    * Creates the panel that contains the logo and general copyright info.
    */
    private JPanel createLogoPanel() {
        
        Object[] args = { AbstractUpdateDialog.PHET_LABEL_LINK };
        String s = MessageFormat.format( ONE_LINER_PATTERN, args );
        String html = HTMLUtils.createStyledHTMLFromFragment( s );
        InteractiveHTMLPane copyrightPane = new InteractiveHTMLPane( html );

        HorizontalLayoutPanel logoPanel = new HorizontalLayoutPanel();
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( copyrightPane );

        return logoPanel;
    }

    private class DetailsButton extends JButton {
        private DetailsButton() {
            super( DETAILS );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    Window window = SwingUtilities.getWindowAncestor( DetailsButton.this );
                    if ( window instanceof Frame ) {
                        new TrackingDetailsDialog( (Frame) window, tracker ).setVisible( true );
                    }
                    else if ( window instanceof Dialog ) {
                        new TrackingDetailsDialog( (Dialog) window, tracker ).setVisible( true );
                    }
                }
            } );
        }
    }
}
