package edu.colorado.phet.build.java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.build.PhetProject;

public class ProjectListPanel extends JPanel {
    private File baseDir;
    private JList projectList;
    private JButton runButton;
    private LocalProperties localProperties;

    public ProjectListPanel( File baseDir ) {
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
        projectList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                notifyListeners();
            }
        } );

        setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets( 2, 2, 2, 2 ), 0, 0 );
        JScrollPane simListPane = new JScrollPane( projectList );
        simListPane.setBorder( BorderFactory.createTitledBorder( "Projects" ) );
        add( simListPane, gridBagConstraints );

        JPanel commandPanel = new JPanel();
        commandPanel.setLayout( new BoxLayout( commandPanel, BoxLayout.Y_AXIS ) );
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
        JButton addMessage = new JButton( "Add Message" );
        addMessage.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String m = JOptionPane.showInputDialog( "Enter a message" );
                if ( m.trim().length() > 0 ) {
                    getSelectedProject().prependChangesText( m );
                }
            }
        } );

        JButton createHeader = new JButton( "Create Header" );
        createHeader.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().createHeader(-1);
            }
        } );

        commandPanel.add( cleanButton );
        commandPanel.add( buildButton );
        commandPanel.add( runButton );
        commandPanel.add( buildJNLP );
        commandPanel.add( svnStatus );
        commandPanel.add( getSVN );
        commandPanel.add( addMessage );
        commandPanel.add( createHeader );

        commandPanel.add( Box.createVerticalStrut( 50 ) );
        commandPanel.add( deployDev );
        commandPanel.add( deployProd );
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

    public PhetProject getSelectedProject() {
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

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        public void notifyChanged();
    }

    public void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).notifyChanged();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }
}
