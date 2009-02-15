package edu.colorado.phet.buildtools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.*;

import edu.colorado.phet.buildtools.PhetProject;

public class MiscMenu extends JMenu {
    public MiscMenu( final File trunk ) {
        super( "Misc" );

        JMenuItem menuItem1 = new JMenuItem( "Generate License Info" );
        add( menuItem1 );
        menuItem1.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetProject[] projects = PhetProject.getAllProjects( trunk );
                for ( int i = 0; i < projects.length; i++ ) {
                    PhetProject project = projects[i];
                    project.copyLicenseInfo();
                }
            }
        } );

        JMenuItem showAllLicenseKeys = new JMenuItem( "Show Credits Keys" );
        showAllLicenseKeys.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetProject[] projects = PhetProject.getAllProjects( trunk );
                HashSet keys = new HashSet();
                for ( int i = 0; i < projects.length; i++ ) {
                    PhetProject project = projects[i];
                    keys.addAll( Arrays.asList( project.getCreditsKeys() ) );
                }
                System.out.println( "keys = " + keys );
            }
        } );
        add( showAllLicenseKeys );

        //we probably shouldn't use this, since it will modify all version numbers
//        JMenuItem buildAndDeployAll = new JMenuItem( "Build and Deploy all-dev" );
//        buildAndDeployAll.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                LocalProperties localProperties = new LocalProperties( new File( trunk, "build-tools/build-local.properties" ) );
//                PhetProject[] projects = PhetProject.getAllProjects( trunk );
//                for ( int i = 0; i < projects.length; i++ ) {
//                    BuildScript buildScript = new BuildScript( trunk, projects[i], new AuthenticationInfo( localProperties.getProperty( "svn.username" ), localProperties.getProperty( "svn.password" ) ), localProperties.getProperty( "browser" ) );
//                    buildScript.deployDev( new AuthenticationInfo( localProperties.getProperty( "deploy." + "dev" + ".username" ), localProperties.getProperty( "deploy." + "dev" + ".password" ) ) );
//                }
//            }
//        } );
//        add( buildAndDeployAll );
    }
}
