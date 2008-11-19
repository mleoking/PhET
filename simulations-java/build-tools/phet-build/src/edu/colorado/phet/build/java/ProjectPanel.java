package edu.colorado.phet.build.java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.build.PhetProject;

public class ProjectPanel extends JPanel {
    private File baseDir;
    private JList projectList;
    private JButton runButton;
    private LocalProperties localProperties;

    public ProjectPanel( File baseDir ) {
        this.baseDir = baseDir;
        this.localProperties = new LocalProperties( baseDir );

        PhetProject[] a = PhetProject.getAllProjects( baseDir );
        PhetProjectAdapter[] b = PhetProjectAdapter.convertToMyPhetProjecets( a, baseDir );
        for ( int i = 0; i < a.length; i++ ) {
            b[i].setAntBaseDir( baseDir );
        }
        ProjectListElement[] p = toListElements( b );
        projectList = new JList( p );
        projectList.setSelectedIndex( 0 );
        projectList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets( 2, 2, 2, 2 ), 0, 0 );
        JScrollPane simListPane = new JScrollPane( projectList );
        simListPane.setBorder( BorderFactory.createTitledBorder( "Projects" ) );
        add( simListPane, gridBagConstraints );

        JPanel commandPanel = new JPanel();
        JButton cleanButton = new JButton( "Clean" );
        cleanButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    getBuildScript().clean();
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );


        JButton buildButton = new JButton( "Build" );
        buildButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().build();
            }
        } );

        JButton buildJNLP = new JButton( "Build Local JNLP" );
        buildJNLP.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().buildJNLP( "file:///" + getSelectedProject().getDefaultDeployJar().getParentFile().getAbsolutePath() );
            }
        } );

        runButton = new JButton( "Run" );
        runButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().runSim();
            }
        } );

        JButton svnStatus = new JButton( "SVN Status" );
        svnStatus.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().isSVNInSync();
            }
        } );

        JButton deployDev = new JButton( "Deploy Dev" );
        deployDev.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().deploy( PhetServer.DEVELOPMENT, getDevelopmentAuthentication( "dev" ), new VersionIncrement.UpdateDev() );
            }
        } );

        JButton deployProd = new JButton( "Deploy Prod" );
        deployProd.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().deploy( PhetServer.PRODUCTION, getDevelopmentAuthentication( "prod" ), new VersionIncrement.UpdateProd() );
            }
        } );

        JButton getSVN = new JButton( "Get SVN version" );
        getSVN.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "getBuildScript().getSVNVersion() = " + getBuildScript().getSVNVersion() );
            }
        } );


        GridBagConstraints commandConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        commandPanel.setLayout( new GridBagLayout() );

        commandPanel.add( cleanButton, commandConstraints );
        commandPanel.add( buildButton, commandConstraints );
        commandPanel.add( runButton, commandConstraints );
        commandPanel.add( buildJNLP, commandConstraints );
        commandPanel.add( svnStatus, commandConstraints );
        commandPanel.add( getSVN, commandConstraints );

        commandPanel.add( Box.createVerticalStrut( 50 ) );
        commandPanel.add( deployDev, commandConstraints );
        commandPanel.add( deployProd, commandConstraints );
        commandPanel.add( Box.createVerticalBox() );

        add( commandPanel, gridBagConstraints );
    }

    private BuildScript getBuildScript() {
        return new BuildScript( baseDir, getSelectedProject(), new AuthenticationInfo( getLocalProperty( "svn.username" ), getLocalProperty( "svn.password" ) ), getLocalProperty( "browser" ) );
    }

    private AuthenticationInfo getDevelopmentAuthentication( String serverType ) {
        return new AuthenticationInfo( getLocalProperty( "deploy." + serverType + ".username" ), getLocalProperty( "deploy." + serverType + ".password" ) );
    }

    private String getLocalProperty( String s ) {
        return localProperties.getProperty( s );
    }

    private PhetProject getSelectedProject() {
        return ( (ProjectListElement) projectList.getSelectedValue() ).getProject();
    }

    private ProjectListElement[] toListElements( PhetProject[] a ) {
        ProjectListElement[] p = new ProjectListElement[a.length];
        for ( int i = 0; i < p.length; i++ ) {
            p[i] = new ProjectListElement( a[i] );
        }
        return p;
    }

    private static class ProjectListElement {
        private PhetProject p;

        private ProjectListElement( PhetProject p ) {
            this.p = p;
        }

        public String toString() {
            return p.getName();
        }

        public PhetProject getProject() {
            return p;
        }
    }
}
