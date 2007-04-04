/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.util.PropertiesLoader;
import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * PhetAboutDialog shows information about PhET, the simulation, copyright, and license.
 * 
 * @author Sam Reid
 * @version $Revision$ 
 */
public class PhetAboutDialog extends JDialog {

    // Resource (file) that contains the PhET license, in plain text format.
    private static final String LICENSE_RESOURCE = "phet-license.txt";
    
    private PhetApplication phetApplication;
    
    /**
     * Constructs the dialog.
     * 
     * @param phetApplication
     * @throws HeadlessException
     */
    public PhetAboutDialog( PhetApplication phetApplication ) throws HeadlessException {
        super( phetApplication.getPhetFrame() );
        
        setResizable( false );
        
        this.phetApplication = phetApplication;

        String title = SimStrings.getInstance().getString( "Common.HelpMenu.AboutTitle" ) + " " + phetApplication.getTitle();
        setTitle( title );
        
        JPanel logoPanel = createLogoPanel();
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
        
        JLabel logoLabel = null;
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( PhetLookAndFeel.PHET_LOGO_120x50 );
            logoLabel = new JLabel( new ImageIcon( image ) );
            logoLabel.setBorder( BorderFactory.createLineBorder( Color.black ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
            logoLabel = new JLabel(); // fallback to a blank label
        }
        
        JLabel copyrightLabel = new JLabel( SimStrings.getInstance().getString( "Common.About.Copyright" ) );
        
        HorizontalLayoutPanel logoPanel = new HorizontalLayoutPanel();
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( logoLabel );
        logoPanel.add( copyrightLabel );
        
        return logoPanel;
    }
    
    /*
     * Creates the panel that displays info specific to the simulation.
     */
    private JPanel createInfoPanel() {
        
        // Simulation title
        JLabel title = new JLabel( phetApplication.getTitle() );
        Font f = title.getFont();
        title.setFont( new Font( f.getFontName(), Font.BOLD, f.getSize() ) );
        
        // Simulation description (aka, abstract)
        JLabel description = new JLabel( phetApplication.getDescription() );
        
        // Simulation version
        String versionHeader = SimStrings.getInstance().getString( "Common.About.Version" ) + " ";

        String versionNumber;

        if ( phetApplication.getProjectConfig() != null ) {
            versionNumber = phetApplication.getProjectConfig().getVersion().formatSmart();
        }
        else {
            versionNumber = extractVersionNumber();
        }
        
        JLabel version = new JLabel( versionHeader + versionNumber );

        // Java runtime version
        String javaVersionString = SimStrings.getInstance().getString( "Common.About.JavaVersion" ) + " " + System.getProperty( "java.version" );
        JLabel javaVersion = new JLabel( javaVersionString );
        
        int xMargin = 10;
        int ySpacing = 10;
        VerticalLayoutPanel infoPanel = new VerticalLayoutPanel();
        infoPanel.setInsets( new Insets( 0, xMargin, 0, xMargin ) ); // top,left,bottom,right
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( title );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( description );
        infoPanel.add( Box.createVerticalStrut( ySpacing * 2 ) );
        infoPanel.add( version );
        infoPanel.add( javaVersion );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        
        return infoPanel;
    }

    /**
     * @return The version number.
     *
     * @deprecated
     */
    private String extractVersionNumber() {
        String versionNumber;
        Properties simulationProperties = phetApplication.getSimulationProperties();
        if ( simulationProperties != null ) {
            versionNumber = getVersionString( simulationProperties );
        }
        else {
            // For older sims that don't have a properties file,
            // simply use the same version string that is shown in the PhetFrame's titlebar.
            versionNumber = phetApplication.getVersion();
        }
        return versionNumber;
    }

    /*
    * Create the panel that contains the buttons.
    * The Credits button is added only if a credits file exists.
    */
    private JPanel createButtonPanel() {
        
        JButton licenseButton = new JButton( SimStrings.getInstance().getString( "Common.About.LicenseButton" ) );
        licenseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showLicenseInfo();
            }
        } );

        JButton creditsButton = new JButton(SimStrings.getInstance().getString( "Common.About.CreditsButton" ) );
        creditsButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showCredits();
            }
        } );
        
        JButton okButton = new JButton( SimStrings.getInstance().getString( "Common.About.OKButton" ) );
        getRootPane().setDefaultButton( okButton );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new FlowLayout() );
        buttonPanel.add( licenseButton );
        Properties simulationProperties = phetApplication.getSimulationProperties();
        if ( simulationProperties != null && simulationProperties.getProperty( PropertiesLoader.PROPERTY_ABOUT_CREDITS ) != null ) {
            buttonPanel.add( creditsButton );
        }
        buttonPanel.add( okButton );
        
        return buttonPanel;
    }
    
    /*
     * Displays license information in a message dialog.
     */
    protected void showLicenseInfo() {
        String phetLicenseString = readFile( LICENSE_RESOURCE );
        JTextArea phetLicense = new JTextArea( phetLicenseString );
        phetLicense.setColumns( 45 );
        phetLicense.setLineWrap( true );
        phetLicense.setWrapStyleWord( true );
        phetLicense.setEditable( false );
        phetLicense.setOpaque( false );
        showMessageDialog( phetLicense, SimStrings.getInstance().getString( "Common.About.LicenseDialog.Title" ) );
    }

    /*
     * Displays credits in a message dialog.
     */
    protected void showCredits() {
        Properties simulationProperties = phetApplication.getSimulationProperties();
        String creditsString = simulationProperties.getProperty( PropertiesLoader.PROPERTY_ABOUT_CREDITS, "?" );
        JLabel credits = new JLabel( creditsString );
        showMessageDialog( credits, SimStrings.getInstance().getString( "Common.About.CreditsDialog.Title" ) );
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
            if( url == null ) {//TODO improve error handling
                new FileNotFoundException( fileResourceName ).printStackTrace();
                return "";
            }
            InputStream inputStream = url.openStream();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
            String line = bufferedReader.readLine();
            while( line != null ) {
                text += line;
                line = bufferedReader.readLine();
                if( line != null ) {
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
    
    /*
     * Determines if a resource exists.
     */
    private static boolean resourceExists( String resourceName ) {
        URL url = Thread.currentThread().getContextClassLoader().getResource( resourceName );
        return ( url != null );
    }

    /**
     * Gets the version string required for the About dialog.
     * 
     * @param simulationProperties
     * @return String
     */
    private static String getVersionString( Properties simulationProperties ) {
        assert( simulationProperties != null );
        String major = simulationProperties.getProperty( PropertiesLoader.PROPERTY_VERSION_MAJOR, "?" );
        String minor = simulationProperties.getProperty( PropertiesLoader.PROPERTY_VERSION_MINOR, "?" );
        String dev = simulationProperties.getProperty( PropertiesLoader.PROPERTY_VERSION_DEV, "?" );
        String revision = simulationProperties.getProperty( PropertiesLoader.PROPERTY_VERSION_REVISION, "?" );
        return major + "." + minor + "." + dev + " (" + revision + ")";
    }
}
