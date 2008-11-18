package edu.colorado.phet.build;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.rev6.scf.*;

/**
 * Provides a front-end user interface for building and deploying phet's java simulations.
 * This entry point has no ant dependencies.
 */
public class PhetBuildGUI {
    private JFrame frame = new JFrame();
    private Object blocker = new Object();
    private JList simList;
    private JButton runButton;
    private File baseDir;
    private JList flavorList;
    private JList localeList;

    public PhetBuildGUI( File baseDir ) {
        this.baseDir = baseDir;
        this.frame = new JFrame( "PhET Build" );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                synchronized( blocker ) {
                    blocker.notifyAll();
                }
            }
        } );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetProject[] a = PhetProject.getAllProjects( baseDir );
        MyPhetProject[] b = convertToMyPhetProjecets( a );
        for ( int i = 0; i < a.length; i++ ) {
            b[i].setAntBaseDir( baseDir );
        }
        Project[] p = toProjects( b );
        simList = new JList( p );
        simList.setSelectedIndex( 0 );
        simList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        simList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
//                updateLists();
            }
        } );
        JPanel contentPane = new JPanel();

        flavorList = new JList( new Object[]{} );
        localeList = new JList( new Object[]{} );

        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets( 2, 2, 2, 2 ), 0, 0 );
        JScrollPane simListPane = new JScrollPane( simList );
        simListPane.setBorder( BorderFactory.createTitledBorder( "Projects" ) );
        contentPane.add( simListPane, gridBagConstraints );
//        JScrollPane comp = new JScrollPane( flavorList );
//        comp.setBorder( BorderFactory.createTitledBorder( "Simulations" ) );
//        contentPane.add( comp, gridBagConstraints );

//        JScrollPane localeScrollPane = new JScrollPane( localeList );
//        localeScrollPane.setPreferredSize( new Dimension( 100, 50 ) );
//        localeScrollPane.setBorder( BorderFactory.createTitledBorder( "Languages" ) );
////        gridBagConstraints.fill=GridBagConstraints.NONE;
//        contentPane.add( localeScrollPane, gridBagConstraints );

        JPanel commandPanel = new JPanel();
//        JButton refresh = new JButton( "Refresh" );
//        refresh.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                refresh();
//            }
//        } );
        JButton cleanButton = new JButton( "Clean" );
        cleanButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    new PhetCleanCommand( getSelectedProject(), new MyAntTaskRunner() ).execute();
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );


        JButton buildButton = new JButton( "Build" );
        buildButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                buildProject();
            }
        } );

        JButton buildJNLP = new JButton( "Build JNLP" );
        buildJNLP.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                buildJNLP();
            }
        } );

        runButton = new JButton( "Run" );
        runButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runSim();
            }
        } );
        updateRunButtonEnabled();

        JButton deployDev = new JButton( "Deploy Dev" );
        deployDev.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                deployDev( getSelectedProject() );
            }
        } );


        GridBagConstraints commandConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        commandPanel.setLayout( new GridBagLayout() );
//        commandPanel.add( refresh, commandConstraints );
//        commandPanel.add( showLocalizationFile, commandConstraints );

        commandPanel.add( cleanButton, commandConstraints );
        commandPanel.add( buildButton, commandConstraints );
        commandPanel.add( runButton, commandConstraints );
        commandPanel.add( buildJNLP, commandConstraints );

        commandPanel.add( deployDev, commandConstraints );
        commandPanel.add( Box.createVerticalBox() );

        contentPane.add( commandPanel, gridBagConstraints );


        frame.setSize( 800, 600 );
        frame.setContentPane( contentPane );
    }

    private void deployDev( PhetProject selectedProject ) {
//        buildProject();
//        buildJNLP();

        SshConnection sshConnection = new SshConnection( "spot.colorado.edu", "reids", "password" );
        try {
            sshConnection.connect();
            String remotePathDir = "/home1/reids/testdir/";
            sshConnection.executeTask( new SshCommand( "mkdir " + remotePathDir ) );
            File[] f = selectedProject.getDefaultDeployDir().listFiles();
            //todo: should handle recursive for future use (if we ever want to support nested directories)
            for ( int i = 0; i < f.length; i++ ) {
                if ( f[i].getName().startsWith( "." ) ) {
                    //ignore
                }
                else {
                    sshConnection.executeTask( new ScpUpload( new ScpFile( f[i], remotePathDir + f[i].getName() ) ) );
                }
            }
        }
        catch( SshException e ) {
            e.printStackTrace();
        }
        finally {
            sshConnection.disconnect();
        }
    }

    private void buildJNLP() {
        String[] flavorNames = getSelectedProject().getFlavorNames();
        Locale[] locales = getSelectedProject().getLocales();
        for ( int i = 0; i < locales.length; i++ ) {
            Locale locale = locales[i];

            for ( int j = 0; j < flavorNames.length; j++ ) {
                String flavorName = flavorNames[j];
                buildJNLP( getSelectedProject(), locale, flavorName );
            }
        }
    }

    private void buildJNLP( PhetProject selectedSimulation, Locale locale, String flavorName ) {
        System.out.println( "Building JNLP for locale=" + locale.getLanguage() + ", flavor=" + flavorName );
        PhetBuildJnlpTask j = new PhetBuildJnlpTask();
        j.setDeployUrl( "file:///" + selectedSimulation.getDefaultDeployJar().getParentFile().getAbsolutePath() );//todo: generalize
        j.setProject( selectedSimulation.getName() );
        j.setLocale( locale.getLanguage() );
        j.setFlavor( flavorName );
        org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
        project.setBaseDir( baseDir );
        project.init();
        j.setProject( project );
        j.execute();
        System.out.println( "Finished Building JNLP" );
    }

    private MyPhetProject[] convertToMyPhetProjecets( PhetProject[] a ) {
        MyPhetProject[] b = new MyPhetProject[a.length];
        for ( int i = 0; i < a.length; i++ ) {
            try {
                b[i] = new MyPhetProject( a[i].getProjectDir() );
                b[i].setAntBaseDir( baseDir );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return b;
    }

    private void buildProject() {
        build( getSelectedProject() );
    }

    private void build( PhetProject project ) {
        try {
            new PhetBuildCommand( project, new MyAntTaskRunner(), true, project.getDefaultDeployJar() ).execute();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private Project[] toProjects( PhetProject[] a ) {
        Project[] p = new Project[a.length];
        for ( int i = 0; i < p.length; i++ ) {
            p[i] = new Project( a[i] );
        }
        return p;
    }

    static class Project {
        PhetProject p;

        Project( PhetProject p ) {
            this.p = p;
        }

        public String toString() {
            return p.getName();
        }
    }

    private void updateRunButtonEnabled() {

    }

    private void run( String msg, final Runnable r ) {
        final JDialog dialog = new JDialog( frame, msg );
        JLabel label = new JLabel( "Building " + getSelectedProject() + ", please wait..." );
        label.setOpaque( true );
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
        panel.add( label );
        dialog.setContentPane( panel );
        dialog.pack();
        dialog.setResizable( false );
        dialog.setLocation( frame.getX() + frame.getWidth() / 2 - dialog.getWidth() / 2, frame.getY() + frame.getHeight() / 2 - dialog.getHeight() / 2 );
        dialog.setVisible( true );


        Runnable r2 = new Runnable() {
            public void run() {
                r.run();
                dialog.dispose();
            }
        };
        Thread thread = new Thread( r2 );
        thread.start();
    }

    private PhetProject getSelectedProject() {
        return ( (Project) simList.getSelectedValue() ).p;
    }

    private void runSim() {
        String locale = "en";
        String flavor = "balloons";
        Java java = new Java();

        PhetProject phetProject = getSelectedProject();
        if ( phetProject != null ) {
            java.setClassname( phetProject.getFlavor( "balloons" ).getMainclass() );
            java.setFork( true );
            String args = "";
            String[] a = phetProject.getFlavor( flavor ).getArgs();
            for ( int i = 0; i < a.length; i++ ) {
                String s = a[i];
                args += s + " ";
            }
            java.setArgs( args );

            org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
            project.init();

            Path classpath = new Path( project );
            FileSet set = new FileSet();
            set.setFile( phetProject.getDefaultDeployJar() );
            classpath.addFileset( set );
            java.setClasspath( classpath );
            if ( !locale.equals( "en" ) ) {
                java.setJvmargs( "-Djavaws.phet.locale=" + locale );
            }

            new MyAntTaskRunner().runTask( java );
//            runTask( java );
        }
    }

    private void start() {
        frame.setVisible( true );
    }

    private class MyPhetProject extends PhetProject {
        private File baseDir;

        public MyPhetProject( File projectRoot ) throws IOException {
            super( projectRoot );
        }

        public MyPhetProject( File parentDir, String name ) throws IOException {
            super( parentDir, name );
        }

        public void setAntBaseDir( File baseDir ) {
            this.baseDir = baseDir;
        }

        public File getAntBaseDir() {
            return baseDir;
        }

        public PhetProject[] getDependencies() {
            PhetProject[] a = super.getDependencies();
            return convertToMyPhetProjecets( a );
        }
    }

    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "Usage: args[0]=basedir" );
        }
        else {
            new PhetBuildGUI( new File( args[0] ) ).start();
        }
    }

}
