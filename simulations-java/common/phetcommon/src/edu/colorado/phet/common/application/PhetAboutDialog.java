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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Properties;

import javax.swing.*;

import edu.colorado.phet.common.resources.PhetCommonResources;
import edu.colorado.phet.common.resources.PhetProperties;
import edu.colorado.phet.common.util.PropertiesLoader;
import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.SwingUtils;

/**
 * PhetAboutDialog shows information about PhET, the simulation, copyright, and license.
 * 
 * @author Sam Reid
 * @version $Revision$ 
 */
public class PhetAboutDialog extends JDialog {

    // Resource (file) that contains the PhET license, in plain text format.
    private static final String LICENSE_RESOURCE = "phet-license.txt";
    
    private JPanel logoPanel;
    private String titleString, descriptionString, versionString, creditsString;

    /**
     * Constructs the dialog.
     * 
     * @param phetApplication
     * @throws HeadlessException
     */
    public PhetAboutDialog( PhetApplication phetApplication ) throws HeadlessException {
        super( phetApplication.getPhetFrame() );
        
        setResizable( false );
        
        // TODO: replace these deprecated calls
        titleString = phetApplication.getTitle();
        descriptionString = phetApplication.getDescription();
        versionString = phetApplication.getVersion();
        if ( phetApplication.getApplicationConfig() != null ) {
            versionString = phetApplication.getApplicationConfig().getVersion().formatForAboutDialog();
        }
        creditsString = phetApplication.getCredits();

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
        logoLabel.setBorder( BorderFactory.createLineBorder( Color.black ) );

        JLabel copyrightLabel = new JLabel( getLocalizedString( "Common.About.Copyright" ) );
        
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
        JLabel titleLabel = new JLabel( titleString );
        Font f = titleLabel.getFont();
        titleLabel.setFont( new Font( f.getFontName(), Font.BOLD, f.getSize() ) );

        // Simulation description
        JLabel descriptionLabel = new JLabel( descriptionString );
        
        // Simulation version
        String versionHeader = getLocalizedString( "Common.About.Version" ) + " ";
        JLabel versionLabel = new JLabel( versionHeader + versionString );

        // Java runtime version
        String javaVersionString = getLocalizedString( "Common.About.JavaVersion" ) + " " + System.getProperty( "java.version" );
        JLabel javaVersionLabel = new JLabel( javaVersionString );
        
        int xMargin = 10;
        int ySpacing = 10;
        VerticalLayoutPanel infoPanel = new VerticalLayoutPanel();
        infoPanel.setInsets( new Insets( 0, xMargin, 0, xMargin ) ); // top,left,bottom,right
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( titleLabel );
        infoPanel.add( Box.createVerticalStrut( ySpacing ) );
        infoPanel.add( descriptionLabel );
        infoPanel.add( Box.createVerticalStrut( ySpacing * 2 ) );
        infoPanel.add( versionLabel );
        infoPanel.add( javaVersionLabel );
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

        JButton creditsButton = new JButton(getLocalizedString( "Common.About.CreditsButton" ) );
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
        String phetLicenseString = readFile( LICENSE_RESOURCE );
        JTextArea phetLicense = new JTextArea( phetLicenseString );
        phetLicense.setColumns( 45 );
        phetLicense.setLineWrap( true );
        phetLicense.setWrapStyleWord( true );
        phetLicense.setEditable( false );
        phetLicense.setOpaque( false );
        showMessageDialog( phetLicense, getLocalizedString( "Common.About.LicenseDialog.Title" ) );
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
}
