package edu.colorado.phet.build;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
        simList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        simList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
//                updateLists();
            }
        } );
        JPanel contentPane = new JPanel();

//        flavorList = new JList( new Object[]{} );
//        localeList = new JList( new Object[]{} );
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets( 2, 2, 2, 2 ), 0, 0 );
        JScrollPane simListPane = new JScrollPane( simList );
        simListPane.setBorder( BorderFactory.createTitledBorder( "Projects" ) );
//        simListPane.setPreferredSize( new Dimension( 400,300) );
        contentPane.add( simListPane, gridBagConstraints );
//        contentPane.add( new JScrollPane( flavorList ), gridBagConstraints );

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
                new JavaBuildCommand().clean( getSelectedSimulation() );
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
                run();
            }
        } );
        updateRunButtonEnabled();
//        JButton showLocalizationFile = new JButton( "Show Localization File" );
//        showLocalizationFile.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                showLocalizationFile();
//            }
//        } );

        GridBagConstraints commandConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        commandPanel.setLayout( new GridBagLayout() );
//        commandPanel.add( refresh, commandConstraints );
//        commandPanel.add( showLocalizationFile, commandConstraints );

        commandPanel.add( cleanButton, commandConstraints );
        commandPanel.add( buildButton, commandConstraints );
        commandPanel.add( runButton, commandConstraints );
        commandPanel.add( buildJNLP, commandConstraints );
        commandPanel.add( Box.createVerticalBox() );

        contentPane.add( commandPanel, gridBagConstraints );


        frame.setSize( 500, 300 );
        frame.setContentPane( contentPane );
    }

    private void buildJNLP() {
        System.out.println( "Building JNLP" );
        PhetBuildJnlpTask j = new PhetBuildJnlpTask();
        j.setProject( getSelectedSimulation().getName() );
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
        build( getSelectedSimulation() );
    }

    private void build( PhetProject phetProject ) {
        JavaBuildCommand javaBuildCommand = new JavaBuildCommand();
        javaBuildCommand.build( phetProject );

//        PhetBuildCommand phetBuildCommand = new PhetBuildCommand( phetProject, new MyAntTaskRunner(), true, phetProject.getDefaultDeployJar() );
//        try {
//            phetBuildCommand.execute();
//        }
//        catch( Exception e ) {
//            e.printStackTrace();
//        }
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

    private void run() {
        final JDialog dialog = new JDialog( frame, "Building Sim" );
        JLabel label = new JLabel( "Building " + getSelectedSimulation() + ", please wait..." );
        label.setOpaque( true );
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
        panel.add( label );
        dialog.setContentPane( panel );
        dialog.pack();
        dialog.setResizable( false );
        dialog.setLocation( frame.getX() + frame.getWidth() / 2 - dialog.getWidth() / 2, frame.getY() + frame.getHeight() / 2 - dialog.getHeight() / 2 );
        dialog.setVisible( true );
        Thread thread = new Thread( new Runnable() {
            public void run() {
                doRun( dialog );
            }
        } );
        thread.start();
    }

    private PhetProject getSelectedSimulation() {
        return ( (Project) simList.getSelectedValue() ).p;
    }

    private void doRun( final JDialog dialog ) {
        String sim = getSelectedSimulation().getName();
//        String flavor = (String)flavorList.getSelectedValue();
        String locale = "en";
        System.out.println( "Building sim: " + sim );
//        PhetBuildTask phetBuildTask = new PhetBuildTask();
//        phetBuildTask.setProject( sim );
//        runTask( phetBuildTask );
//        System.out.println( "Build complete" );
//        Java java = new Java();
//
//        PhetProject phetProject = getSelectedProject();
//        if ( phetProject != null ) {
//            java.setClassname( phetProject.getFlavor( getSelectedSimulation().getFlavorName() ).getMainclass() );
//            java.setFork( true );
//            String args = "";
//            String[] a = phetProject.getFlavor( getSelectedSimulation().getFlavorName() ).getArgs();
//            for ( int i = 0; i < a.length; i++ ) {
//                String s = a[i];
//                args += s + " ";
//            }
//            java.setArgs( args );
//            Path classpath = new Path( getProject() );
//            FileSet set = new FileSet();
//            set.setFile( phetProject.getDefaultDeployJar() );
//            classpath.addFileset( set );
//            java.setClasspath( classpath );
//            if ( !locale.equals( "en" ) ) {
//                java.setJvmargs( "-Djavaws.phet.locale=" + locale );
//            }
//            SwingUtilities.invokeLater( new Runnable() {
//                public void run() {
//                    dialog.dispose();
//                }
//            } );
//            runTask( java );
//        }
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
