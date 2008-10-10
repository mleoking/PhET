package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class TrackingPreferencesPanel extends JPanel {

    private static final String INFO =
            "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE +
            "<b><a href=http://phet.colorado.edu>PhET</a></b> " +
            "is made possible by grants that require<br>us to track anonymous usage statistics.</html>";
    private ITrackingInfo tracker;

    private JCheckBox trackingEnabledCheckBox;
    
    public TrackingPreferencesPanel( ITrackingInfo tracker, boolean trackingEnabled ) {
        this.tracker = tracker;
        
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        
        trackingEnabledCheckBox = new JCheckBox( "Send tracking info to PhET", trackingEnabled );

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
        String html = INFO;
        html = html.replaceAll( "@FONT_SIZE@", new PhetFont().getSize() + "pt" );
        html = html.replaceAll( "@FONT_FAMILY@", new PhetFont().getFamily() );
        PhetAboutDialog.HTMLPane copyrightLabel = new PhetAboutDialog.HTMLPane( html );

        HorizontalLayoutPanel logoPanel = new HorizontalLayoutPanel();
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( copyrightLabel );

        return logoPanel;
    }

    private class DetailsButton extends JButton {
        private DetailsButton() {
            super( "Details..." );
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
