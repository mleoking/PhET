package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.tracking.Tracker;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class TrackingPreferencesPanel extends JPanel {

    private static final String INFO =
            "<html><head><style type=\"text/css\">body { font-size: @FONT_SIZE@; font-family: @FONT_FAMILY@ }</style></head>" +
            "<b><a href=http://phet.colorado.edu>PhET</a></b> " +
            "is made possible by grants that require us to report anonymous usage statistics.</html>";
    private ITrackingInfo tracker;

    public TrackingPreferencesPanel( ITrackingInfo tracker ) {
        this.tracker = tracker;
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        add( createLogoPanel(), constraints );
        add( Box.createRigidArea( new Dimension( 5, 2 ) ), constraints );
        add( new TrackingCheckBox(), constraints );
        add( Box.createRigidArea( new Dimension( 5, 10 ) ), constraints );
        add( new PreferencesScopePanel(), constraints );
        add( Box.createRigidArea( new Dimension( 5, 10 ) ), constraints );
        add( new DetailsButton(), constraints );
//        add( createReportPanel(), constraints );
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
//        logoPanel.add( logoLabel );
        logoPanel.add( copyrightLabel );

        return logoPanel;
    }

    private class TrackingCheckBox extends JCheckBox {
        private TrackingCheckBox() {
            super( "Send tracking info to PhET", true );
        }
    }

    private class DetailsButton extends JButton {
        private DetailsButton() {
            super( "Details..." );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    Window window = SwingUtilities.getWindowAncestor( DetailsButton.this );
                    if ( window instanceof Frame ) {
                        new FullTrackingInfoDialog( (Frame) window, tracker ).setVisible( true );
                    }
                    else if ( window instanceof Dialog ) {
                        new FullTrackingInfoDialog( (Dialog) window, tracker ).setVisible( true );
                    }
                }
            } );
        }
    }
}
