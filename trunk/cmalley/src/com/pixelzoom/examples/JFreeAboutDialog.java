
package com.pixelzoom.examples;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.ui.about.AboutDialog;
import org.jfree.ui.about.Contributor;
import org.jfree.base.Library;
import org.jfree.ui.about.ProjectInfo;

import edu.colorado.phet.common.view.LogoPanel;
import edu.colorado.phet.common.view.util.ImageLoader;

/**
 * JFreeAboutDialog demonstrates how to use JFree's AboutDialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class JFreeAboutDialog extends JFrame {
    
    public static void main( String[] args ) throws IOException {
        JFrame frame = new JFreeAboutDialog();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
    
    public JFreeAboutDialog() throws IOException {
        super( "Test" );
        
        final String projectName = "PhET Test";
        String version = "1.00";
        String info = "A test of the JFree About dialog";
        Image logo = ImageLoader.loadBufferedImage( LogoPanel.IMAGE_PHET_LOGO );
        String copyright = "Copyright 2006, University of Colorado";
        String licenseName = "GNU Public License (GPL)";
        String licenseText = "PhET's license text goes here";
        
        ArrayList contributorsList = new ArrayList();
        contributorsList.add( new Contributor( "Ron LeMaster", "ron.lemaster@colorado.edu" ) );
        contributorsList.add( new Contributor( "Sam Reid", "reids@ucsub.colorado.edu" ) );
        contributorsList.add( new Contributor( "Chris Malley", "cmalley@pixelzoom.com" ) );
        
        final ProjectInfo projectInfo = new ProjectInfo( projectName, version, info, logo, copyright, licenseName, licenseText );
        projectInfo.setContributors( contributorsList );
        projectInfo.addLibrary( new Library( "Richardson", "1.0", "GPL", "www.somewhere.com" ) );
        projectInfo.addLibrary( new Library( "Schmidt & Lee", "1.0", "free", "www.elsewhere.com" ) );
        
        final Frame owner = this;
        JButton aboutButton = new JButton( "About..." );
        aboutButton.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                String title = "About " + projectName;
                AboutDialog aboutDialog = new AboutDialog( owner, title, projectInfo );
                aboutDialog.show();
            }
        } );
        
        JPanel panel = new JPanel();
        panel.add( aboutButton );
        
        setContentPane( panel );
        pack();
    }
}
