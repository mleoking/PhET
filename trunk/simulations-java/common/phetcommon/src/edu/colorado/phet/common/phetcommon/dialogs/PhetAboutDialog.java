/* Copyright 2006-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.SoftwareAgreementDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * PhetAboutDialog shows information about PhET, the simulation, copyright, and license.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class PhetAboutDialog extends JDialog {

    // Copyright notice, not translated so no one messes with it, and so that we can easily change the date.
    private static final String COPYRIGHT_HTML_FRAGMENT =
            "<b>Physics Education Technology project</b><br>" +
            "Copyright &copy; 2004-2009 University of Colorado.<br>" +
            "<a href=" + PhetCommonConstants.PHET_HOME_URL + "/about/licensing.php>Some rights reserved.</a><br>" +
            "Visit " + HTMLUtils.getPhetHomeHref();
    
    // translated strings
    private static final String TITLE = PhetCommonResources.getString( "Common.HelpMenu.AboutTitle" );
    private static final String LOGO_TOOLTIP = PhetCommonResources.getString( "Common.About.WebLink" );
    private static final String SIM_VERSION = PhetCommonResources.getString( "Common.About.Version" );
    private static final String BUILD_DATE = PhetCommonResources.getString( "Common.About.BuildDate" );
    private static final String DISTRIBUTION = PhetCommonResources.getString( "Common.About.Distribution" );
    private static final String JAVA_VERSION = PhetCommonResources.getString( "Common.About.JavaVersion" );
    private static final String OS_VERSION = PhetCommonResources.getString( "Common.About.OSVersion" );
    private static final String AGREEMENT_BUTTON = PhetCommonResources.getString( "Common.About.AgreementButton" );
    private static final String CREDITS_BUTTON = PhetCommonResources.getString( "Common.About.CreditsButton" );
    private static final String CLOSE_BUTTON = PhetCommonResources.getString( "Common.choice.close" );

    private String titleString, descriptionString, versionString, buildDate, distributionTag;
    private ISimInfo config;

    /**
     * Constructs the dialog.
     *
     * @param phetApplication
     * @throws HeadlessException
     */
    public PhetAboutDialog( PhetApplication phetApplication ) {
        this( phetApplication.getPhetFrame(), phetApplication.getSimInfo() );
    }

    /**
     * Constructs a dialog.
     *
     * @param config
     * @param owner
     */
    protected PhetAboutDialog( Frame owner, ISimInfo config ) {
        super( owner );
        setResizable( false );
        this.config=config;

        titleString = config.getName();
        descriptionString = config.getDescription();
        if ( descriptionString == null ) {
            new Exception( "null description string, continuing" ).printStackTrace();
            descriptionString = "";
        }
        versionString = config.getVersion().formatForAboutDialog();
        buildDate = config.getVersion().formatTimestamp();
        distributionTag = config.getDistributionTag();

        setTitle( TITLE + " " + titleString );

        JPanel logoPanel = createLogoPanel();
        JPanel infoPanel = createInfoPanel();
        JPanel buttonPanel = createButtonPanel( config.isStatisticsFeatureIncluded() );

        VerticalLayoutPanel contentPanel = new VerticalLayoutPanel();
        contentPanel.setFillHorizontal();
        contentPanel.add( logoPanel );
        contentPanel.add( new JSeparator() );
        contentPanel.add( infoPanel );
        contentPanel.add( new JSeparator() );
        contentPanel.add( buttonPanel );
        setContentPane( contentPanel );

        pack();
        SwingUtils.centerDialogInParent( this );
    }

    /*
     * Creates the panel that contains the logo and general copyright info.
     */
    private JPanel createLogoPanel() {

        BufferedImage image = PhetCommonResources.getInstance().getImage( PhetLookAndFeel.PHET_LOGO_120x50 );
        JLabel logoLabel = new JLabel( new ImageIcon( image ) );
        logoLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        logoLabel.setToolTipText( LOGO_TOOLTIP );
        logoLabel.addMouseListener( new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                PhetServiceManager.showPhetPage();
            }
        } );

        String html = HTMLUtils.createStyledHTMLFromFragment( COPYRIGHT_HTML_FRAGMENT );
        InteractiveHTMLPane pane = new InteractiveHTMLPane( html );

        HorizontalLayoutPanel logoPanel = new HorizontalLayoutPanel();
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( logoLabel );
        logoPanel.add( pane );

        return logoPanel;
    }

    /*
     * Creates the panel that displays info specific to the simulation.
     */
    private JPanel createInfoPanel() {

        VerticalLayoutPanel infoPanel = new VerticalLayoutPanel();

        // Simulation title
        JLabel titleLabel = new JLabel( titleString );
        Font f = titleLabel.getFont();
        titleLabel.setFont( new Font( f.getFontName(), Font.BOLD, f.getSize() ) );

        // Simulation description
        JTextArea descriptionTextArea = new JTextArea( descriptionString );
        FontMetrics fontMetrics = descriptionTextArea.getFontMetrics( descriptionTextArea.getFont() );
        final int columns = 35;
        descriptionTextArea.setColumns( columns );
        
        // Swing's notion of a text "column" is weakly defined. Short of implementing our own word wrapping,
        // using FontMetrics provides the closest approximation to the number of rows that we need.
        // Since we want a bit of space between the description and the stuff below it, having an
        // extra (blank) row is generally OK.
        int rows = ( ( fontMetrics.stringWidth( descriptionString ) / fontMetrics.charWidth( 'm' ) ) / columns ) + 2;
        descriptionTextArea.setRows( rows );
        descriptionTextArea.setBackground( infoPanel.getBackground() );
        descriptionTextArea.setEditable( false );
        descriptionTextArea.setLineWrap( true );
        descriptionTextArea.setWrapStyleWord( true );

        // Simulation version
        JLabel versionLabel = new JLabel( SIM_VERSION + " " + versionString );
        
        // Build date
        JLabel buildDateLabel = new JLabel( BUILD_DATE + " " + buildDate );
        
        // Distribution tag (optional)
        JLabel distributionTagLabel = null;
        if ( distributionTag != null && distributionTag.length() > 0 ) {
            distributionTagLabel = new JLabel( DISTRIBUTION + " " + distributionTag );
        }

        // Java runtime version
        String javaVersionString = JAVA_VERSION + " " + System.getProperty( "java.version" );
        JLabel javaVersionLabel = new JLabel( javaVersionString );

        // OS version
        String osVersion = OS_VERSION + " " + System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" );
        JLabel osVersionLabel = new JLabel( osVersion );

        // layout
        int xMargin = 10;
        int ySpacing = 10;
        infoPanel.setInsets( new Insets( 0, xMargin, 0, xMargin ) ); // top,left,bottom,right
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( titleLabel );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( descriptionTextArea );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( new JSeparator());
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( versionLabel );
        infoPanel.add( buildDateLabel );
        if ( distributionTagLabel != null ) {
            infoPanel.add( distributionTagLabel );
        }
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( javaVersionLabel );
        infoPanel.add( osVersionLabel );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );

        return infoPanel;
    }

    /*
    * Create the panel that contains the buttons.
    * The Credits button is added only if a credits file exists.
    */
    private JPanel createButtonPanel( boolean isStatisticsFeatureIncluded ) {

        // Software Use Agreement
        JButton agreementButton = new JButton( AGREEMENT_BUTTON );
        agreementButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    showAgreement();
                }
                catch ( IOException ioe ) {
                    ioe.printStackTrace();
                }
            }
        } );

        // Credits
        JButton creditsButton = new JButton( CREDITS_BUTTON );
        creditsButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showCredits();
            }
        } );

        // Close
        JButton closeButton = new JButton( CLOSE_BUTTON );
        getRootPane().setDefaultButton( closeButton );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );

        // layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new FlowLayout() );
        if ( isStatisticsFeatureIncluded ) {
            buttonPanel.add( agreementButton );
        }
        buttonPanel.add( creditsButton );
        buttonPanel.add( closeButton );

        return buttonPanel;
    }

    /*
     * Displays the Software Use Agreement in a dialog.
     */
    protected void showAgreement() throws IOException {
        new SoftwareAgreementDialog( this ).setVisible( true );
    }

    protected void showCredits() {
        new CreditsDialog( this, config.getProjectName() ).setVisible( true );
    }
}
