/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * PhetAboutDialog shows information about PhET, the simulation, copyright, and license.
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class PhetAboutDialog extends JDialog {

    // Resource (file) that contains the PhET license, in plain text format.
    private static final String LICENSE_RESOURCE = "phet-license.txt";
    // Copyright notice, not translated so no one messes with it, and so that we can easily change the date.
    private static final String COPYRIGHT_HTML_FRAGMENT =
            "<b>Physics Education Technology project</b><br>" +
            "Copyright &copy; 2004-2008 University of Colorado.<br>" +
            "<a href=http://phet.colorado.edu/about/licensing.php>Some rights reserved.</a><br>" +
            "Visit <a href=http://phet.colorado.edu>http://phet.colorado.edu</a>";

    private JPanel logoPanel;
    private String titleString, descriptionString, versionString, creditsString;

    /**
     * Constructs the dialog.
     *
     * @param phetApplication
     * @throws HeadlessException
     */
    public PhetAboutDialog( PhetApplication phetApplication ) {
        this( phetApplication.getPhetFrame(), getDialogConfig( phetApplication ) );
    }

    private static DialogConfig getDialogConfig( PhetApplication phetApplication ) {
        if ( phetApplication.getApplicationConfig() != null ) {
            return new PhetApplicationConfigDialogConfig( phetApplication.getApplicationConfig() );
        }
        else {
            return new SimpleDialogConfig( phetApplication.getTitle(), phetApplication.getDescription(), phetApplication.getVersion(), phetApplication.getCredits() );
        }
    }

    /**
     * Constructs a PhetAboutDialog given the short-name of a simulation, e.g. "moving-man"
     *
     * @param ownwer
     * @param simulationShortName
     * @deprecated use the constructor that takes as PhetApplicationConfig
     */
    public PhetAboutDialog( Frame ownwer, String simulationShortName ) {
        this( ownwer, new PhetAboutDialog.PhetApplicationConfigDialogConfig( new PhetApplicationConfig( new String[0], new PhetApplicationConfig.ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return null;
            }
        },  simulationShortName  ) ) );
    }

    /**
     * Constructs a dialog.
     *
     * @param config
     * @param owner
     */
    public PhetAboutDialog( Frame owner, DialogConfig config ) {
        super( owner );
        setResizable( false );

        titleString = config.getName();
        descriptionString = config.getDescription();
        if (descriptionString==null){
            new Exception("Null description string, continuing").printStackTrace(  );
            descriptionString="";
        }
        versionString = config.getVersionForAboutDialog();
        creditsString = config.getCredits();

        setTitle( getLocalizedString( "Common.HelpMenu.AboutTitle" ) + " " + titleString );

        logoPanel = createLogoPanel();
        JPanel infoPanel = createInfoPanel();
        JPanel buttonPanel = createButtonPanel();

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
        logoLabel.setToolTipText( getLocalizedString( "Common.About.WebLink" ) );
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
        String versionHeader = getLocalizedString( "Common.About.Version" ) + " ";
        JLabel versionLabel = new JLabel( versionHeader + versionString );

        // Java runtime version
        String javaVersionString = getLocalizedString( "Common.About.JavaVersion" ) + " " + System.getProperty( "java.version" );
        JLabel javaVersionLabel = new JLabel( javaVersionString );

        // OS version
        String osVersion = getLocalizedString( "Common.About.OSVersion" ) + " " + System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" );
        JLabel osVersionLabel = new JLabel( osVersion );

        int xMargin = 10;
        int ySpacing = 10;
        infoPanel.setInsets( new Insets( 0, xMargin, 0, xMargin ) ); // top,left,bottom,right
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( titleLabel );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( descriptionTextArea );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( versionLabel );
        infoPanel.add( javaVersionLabel );
        infoPanel.add( osVersionLabel );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );

        return infoPanel;
    }

    /*
    * Create the panel that contains the buttons.
    * The Credits button is added only if a credits file exists.
    */
    private JPanel createButtonPanel() {

        JButton licenseButton = new JButton( getLocalizedString( "Common.About.LicenseButton" ) );
        licenseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showLicenseInfo();
            }
        } );

        JButton creditsButton = new JButton( getLocalizedString( "Common.About.CreditsButton" ) );
        creditsButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showCredits();
            }
        } );

        JButton okButton = new JButton( getLocalizedString( "Common.About.OKButton" ) );
        getRootPane().setDefaultButton( okButton );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new FlowLayout() );
        buttonPanel.add( licenseButton );
        if ( creditsString != null ) {
            buttonPanel.add( creditsButton );
        }
        buttonPanel.add( okButton );

        return buttonPanel;
    }

    /*
     * Displays license information in a message dialog.
     */
    protected void showLicenseInfo() {

        final JDialog dialog = new JDialog( this, getLocalizedString( "Common.About.LicenseDialog.Title" ), true );
        
        String phetLicenseString = HTMLUtils.setFontInStyledHTML( readFile( LICENSE_RESOURCE ), new PhetFont() );
        InteractiveHTMLPane htmlPane = new InteractiveHTMLPane( phetLicenseString );
        JScrollPane scrollPane = new JScrollPane( htmlPane );
        
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton( getLocalizedString( "Common.About.OKButton" ) );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.setVisible( false );
                dialog.dispose();
            }
        } );
        buttonPanel.add( okButton );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        panel.add( scrollPane, BorderLayout.CENTER );
        panel.add( buttonPanel, BorderLayout.SOUTH );
        
        dialog.setContentPane( panel );
        dialog.setSize( 440,400 );//todo: this shouldn't be hard coded, but I had trouble getting Swing to do something reasonable
        SwingUtils.centerDialogInParent( dialog );
        dialog.setVisible( true );

//        JTextArea phetLicense = new JTextArea( phetLicenseString );
//        phetLicense.setColumns( 45 );
//        phetLicense.setLineWrap( true );
//        phetLicense.setWrapStyleWord( true );
//        phetLicense.setEditable( false );
//        phetLicense.setOpaque( false );
//        showMessageDialog( phetLicense, getLocalizedString( "Common.About.LicenseDialog.Title" ) );
    }

    /*
     * Displays credits in a message dialog.
     */
    protected void showCredits() {
        JLabel creditsLabel = new JLabel( creditsString );
        showMessageDialog( creditsLabel, getLocalizedString( "Common.About.CreditsDialog.Title" ) );
    }

    private String getLocalizedString( String propertyName ) {
        return PhetCommonResources.getInstance().getLocalizedString( propertyName );
    }

    /*
    * Displays a message dialog.
    */
    protected void showMessageDialog( Component component, String title ) {
        // This line fails due to this known bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4545951
//      JOptionPane.showMessageDialog( getOwner(), jTextArea, "License Information", JOptionPane.INFORMATION_MESSAGE );
        // ...so we use Sun's recommended workaround.
        JOptionPane optionPane = new JOptionPane( component, JOptionPane.INFORMATION_MESSAGE );
        JDialog dialog = optionPane.createDialog( null, title );

        // This forces correct resizing.
        component.invalidate();
        dialog.pack();

        dialog.show();
    }

    /*
     * Reads the text from the specified file as a String object.
     * @param textFilename the filename for the File from which to read the String
     * @return the text from the specified file as a String object.
     */
    private static String readFile( String fileResourceName ) {
        String text = new String();
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource( fileResourceName );
            if ( url == null ) {//TODO improve error handling
                new FileNotFoundException( fileResourceName ).printStackTrace();
                return "";
            }
            InputStream inputStream = url.openStream();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
            String line = bufferedReader.readLine();
            while ( line != null ) {
                text += line;
                line = bufferedReader.readLine();
                if ( line != null ) {
                    text += System.getProperty( "line.separator" );
                }
            }
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * A DialogConfig is the minimum amount of information necessary to construct a PhetAboutDialog.
     */
    public static interface DialogConfig {
        String getName();

        String getDescription();

        String getVersionForAboutDialog();

        String getCredits();
    }

    /**
     * A PhetApplicationConfigDialogConfig is an adapter class for using PhetApplicationConfig as DialogConfig.
     * We may prefer to make PhetApplicationConfig implement DialogConfig interface.
     */
    public static class PhetApplicationConfigDialogConfig implements DialogConfig {
        private PhetApplicationConfig applicationConfig;

        public PhetApplicationConfigDialogConfig( PhetApplicationConfig applicationConfig ) {
            this.applicationConfig = applicationConfig;
        }

        public String getName() {
            return applicationConfig.getName();
        }

        public String getDescription() {
            return applicationConfig.getDescription();
        }

        public String getVersionForAboutDialog() {
            return applicationConfig.getVersion().formatForAboutDialog();
        }

        public String getCredits() {
            return applicationConfig.getCredits();
        }
    }

    /**
     * This dialog config allows simulations to directly specify information for the about dialog; it is
     * provided mostly for backward compatibility with older simulations.
     */
    public static class SimpleDialogConfig implements DialogConfig {
        private String name;
        private String description;
        private String versionString;
        private String credits;

        public SimpleDialogConfig( String name, String description, String versionString, String credits ) {
            this.name = name;
            this.description = description;
            this.versionString = versionString;
            this.credits = credits;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getVersionForAboutDialog() {
            return versionString;
        }

        public String getCredits() {
            return credits;
        }

    }

}
