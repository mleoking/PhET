package edu.colorado.phet.buildtools.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.buildtools.*;

public class ProjectListPanel extends JPanel {
    private File trunk;
    private JList projectList;
    private JButton runButton;
    private BuildLocalProperties buildLocalProperties;

    public ProjectListPanel( File trunk ) {
        setMaximumSize( new Dimension( 200, 10000 ) );
        this.trunk = trunk;
        this.buildLocalProperties = BuildLocalProperties.getInstance();

        ProjectListElement[] p = getProjectListElements();
        projectList = new JList( p );
        if ( getDefaultProject() != null ) {
            projectList.setSelectedValue( getDefaultProject(), true );
        }
        else {
            projectList.setSelectedIndex( 0 );
        }

        projectList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        projectList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                notifyListeners();
                saveNewProjectSelection();
            }
        } );

//        setLayout( new GridBagLayout() );
//        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets( 2, 2, 2, 2 ), 0, 0 );
        JScrollPane simListPane = new JScrollPane( projectList );
        projectList.ensureIndexIsVisible( projectList.getSelectedIndex() );
        simListPane.setBorder( BorderFactory.createTitledBorder( "Projects" ) );
//        add( simListPane, gridBagConstraints );
        setLayout( new BorderLayout() );
        add( simListPane, BorderLayout.CENTER );

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

//        JButton buildJNLP = new JButton( "Build Local JNLP" );
//        buildJNLP.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                getBuildScript().buildJNLP( "file:///" + getSelectedProject().getDefaultDeployJar().getParentFile().getAbsolutePath(), true );
//            }
//        } );

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
                getBuildScript().deployDev( buildLocalProperties.getDevAuthenticationInfo() );
            }
        } );

        final JButton deployProd = new JButton( "Deploy Dev & Prod" );
        deployProd.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int option = JOptionPane.showConfirmDialog( deployProd, "Are you sure you are ready to deploy " + getSelectedProject().getName() + " to " + PhetServer.PRODUCTION.getHost() + "?" );
                if ( option == JOptionPane.YES_OPTION ) {
                    getBuildScript().deployProd( buildLocalProperties.getDevAuthenticationInfo(), buildLocalProperties.getProdAuthenticationInfo() );
                }
                else {
                    System.out.println( "Cancelled" );
                }
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
                getBuildScript().createHeader( -1 );
            }
        } );

        commandPanel.add( cleanButton );
        commandPanel.add( buildButton );
        commandPanel.add( runButton );
//        commandPanel.add( buildJNLP );
        commandPanel.add( svnStatus );
        commandPanel.add( getSVN );
        commandPanel.add( addMessage );
        commandPanel.add( createHeader );

        commandPanel.add( Box.createVerticalStrut( 50 ) );
        commandPanel.add( deployDev );
        commandPanel.add( deployProd );
        commandPanel.add( Box.createVerticalBox() );

    }

    private ProjectListElement[] getProjectListElements() {
        PhetProject[] a = PhetProject.getAllProjects( trunk );
        return toListElements( a );
    }

    private void saveNewProjectSelection() {
        Properties properties = new Properties();
        properties.setProperty( "project", getSelectedProject().getName() );
        try {
            properties.store( new FileOutputStream( getPhetBuildGUIPropertyFile() ), null );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private ProjectListElement getDefaultProject() {
        File file = getPhetBuildGUIPropertyFile();
        if ( file.exists() ) {
            Properties p = new Properties();
            try {
                p.load( new FileInputStream( file ) );
                if ( p.containsKey( "project" ) ) {
                    String proj = p.getProperty( "project" );
                    ProjectListElement[] ple = getProjectListElements();
                    for ( int i = 0; i < ple.length; i++ ) {
                        ProjectListElement projectListElement = ple[i];
                        if ( projectListElement.getProject().getName().equals( proj ) ) {
                            return projectListElement;
                        }
                    }
                    return null;
                }
                else {
                    return null;
                }
            }
            catch( IOException e ) {
                return null;
            }
        }
        else {
            return null;
        }
    }

    private File getPhetBuildGUIPropertyFile() {
        File file = new File( trunk, ".phet-build-gui.properties" );
        return file;
    }

    private BuildScript getBuildScript() {
        return new BuildScript( trunk, getSelectedProject() );
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
            return p.getListDisplayName();
        }

        public PhetProject getProject() {
            return p;
        }

        public boolean equals( Object obj ) {
            if ( obj instanceof ProjectListElement ) {
                ProjectListElement p = (ProjectListElement) obj;
                return p.p.getName().equals( this.p.getName() );
            }
            else {
                return false;
            }
        }

        public int hashCode() {
            return p.getName().hashCode();
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
