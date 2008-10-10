package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.updates.dialogs.AbstractUpdateDialog;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that appears when you press the "Details" button in the Tracking preferences panel.
 */
public class TrackingDetailsDialog extends JDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.tracking.detailsTitle" );
    private static final String REPORT_LABEL = PhetCommonResources.getString( "Common.tracking.report" );
    private static final String WEB_LINK_TOOLTIP = PhetCommonResources.getString( "Common.About.WebLink" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.choice.ok" );
    private static final String ABOUT_PATTERN = PhetCommonResources.getString( "Common.tracking.about" ) + "</html>";
    
    private ITrackingInfo iTrackingInfo;

    public TrackingDetailsDialog( Dialog owner, ITrackingInfo iTrackingInfo ) {
        super( owner, TITLE );
        init( iTrackingInfo );
    }

    public TrackingDetailsDialog( Frame owner, ITrackingInfo iTrackingInfo ) {
        super( owner );
        init( iTrackingInfo );
    }

    private void init( ITrackingInfo tracker ) {
        setResizable( false );
        this.iTrackingInfo = tracker;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 8, 2, 8, 2 ) );
        panel.add( createLogoPanel(), constraints );
        panel.add( createReportPanel(), constraints );
        panel.add( createButtonPanel(), constraints );
        getContentPane().add( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    //TODO report should be in a JScrollPane to handle future reports that may be longer
    private JComponent createReportPanel() {
        final JTextArea jt = new JTextArea( "" );
        if ( iTrackingInfo.getHumanReadableTrackingInformation() != null ) {
            jt.setText( iTrackingInfo.getHumanReadableTrackingInformation() );
        }
        jt.setBorder( BorderFactory.createTitledBorder( REPORT_LABEL ) );
        jt.setEditable( false );
        return jt;
    }

    private JPanel createLogoPanel() {

        BufferedImage image = PhetCommonResources.getInstance().getImage( PhetLookAndFeel.PHET_LOGO_120x50 );
        JLabel logoLabel = new JLabel( new ImageIcon( image ) );
        logoLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        logoLabel.setToolTipText( WEB_LINK_TOOLTIP );
        logoLabel.addMouseListener( new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                PhetServiceManager.showPhetPage();
            }
        } );

        // fill in the PhET URL in the About HTML fragment, then add CSS and <html> tags
        Object[] args = { AbstractUpdateDialog.PHET_HOME_URL };
        String html = "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE + MessageFormat.format( ABOUT_PATTERN, args ) + "</html>";
        html = html.replaceAll( "@FONT_SIZE@", new PhetFont().getSize() + "pt" );
        html = html.replaceAll( "@FONT_FAMILY@", new PhetFont().getFamily() );
        PhetAboutDialog.HTMLPane copyrightLabel = new PhetAboutDialog.HTMLPane( html );

        VerticalLayoutPanel logoPanel = new VerticalLayoutPanel();
        logoPanel.add( logoLabel );
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( copyrightLabel );

        return logoPanel;
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
