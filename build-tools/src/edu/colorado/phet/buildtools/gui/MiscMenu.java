package edu.colorado.phet.buildtools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.*;

import edu.colorado.phet.buildtools.PhetProject;

public class MiscMenu extends JMenu {
    public MiscMenu( final File baseDir ) {
        super( "Misc" );

        JMenuItem menuItem1 = new JMenuItem( "Generate License Info" );
        add( menuItem1 );
        menuItem1.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetProject[] projects = PhetProject.getAllProjects( baseDir );
                for ( int i = 0; i < projects.length; i++ ) {
                    PhetProject project = projects[i];
                    project.copyLicenseInfo();
                }
            }
        } );

        JMenuItem showAllLicenseKeys = new JMenuItem( "Show Credits Keys" );
        showAllLicenseKeys.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetProject[] projects = PhetProject.getAllProjects( baseDir );
                HashSet keys = new HashSet();
                for ( int i = 0; i < projects.length; i++ ) {
                    PhetProject project = projects[i];
                    keys.addAll( Arrays.asList( project.getCreditsKeys() ) );
                }
                System.out.println( "keys = " + keys );
            }
        } );
        add( showAllLicenseKeys );
    }
}
