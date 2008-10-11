package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.updates.dialogs.AbstractUpdateDialog;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * Dialog that appears when you press the "Details" button in the Tracking preferences panel.
 */
public class TrackingDetailsDialog extends JDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.tracking.detailsTitle" );
    private static final String REPORT_LABEL = PhetCommonResources.getString( "Common.tracking.report" );
    private static final String WEB_LINK_TOOLTIP = PhetCommonResources.getString( "Common.About.WebLink" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.choice.ok" );
    private static final String ABOUT_PATTERN = PhetCommonResources.getString( "Common.tracking.about" );
    
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
        panel.add( createLogo(), constraints );
        panel.add( createDescriptionPanel(), constraints );
        panel.add( createReport(), constraints );
        panel.add( createButtonPanel(), constraints );
        getContentPane().add( panel );
        
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JComponent createLogo() {

        BufferedImage image = PhetCommonResources.getInstance().getImage( PhetLookAndFeel.PHET_LOGO_120x50 );
        JLabel logoLabel = new JLabel( new ImageIcon( image ) );
        logoLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        logoLabel.setToolTipText( WEB_LINK_TOOLTIP );
        logoLabel.addMouseListener( new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                PhetServiceManager.showPhetPage();
            }
        } );
        
        return logoLabel;
        
    }
    
    private JComponent createDescriptionPanel() {
        // fill in the PhET URL in the About HTML fragment, then add CSS and <html> tags
        Object[] args = { HTMLUtils.getPhetHomeHref( PhetCommonConstants.PHET_NAME ), HTMLUtils.getPhetHomeHref() };
        String fragment = MessageFormat.format( ABOUT_PATTERN, args );
        String html = HTMLUtils.createStyledHTMLFromFragment( fragment );
        InteractiveHTMLPane copyrightLabel = new InteractiveHTMLPane( html );
        copyrightLabel.setMargin( new Insets( 10, 10, 10, 10 ) );
        return copyrightLabel;
    }
    
    //TODO report should be in a JScrollPane to handle future reports that may be longer
    private JComponent createReport() {
        final JTextArea jt = new JTextArea( "" );
        if ( iTrackingInfo.getHumanReadableTrackingInformation() != null ) {
            jt.setText( iTrackingInfo.getHumanReadableTrackingInformation() );
        }
        jt.setBorder( BorderFactory.createTitledBorder( REPORT_LABEL ) );
        jt.setEditable( false );
        return jt;
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
