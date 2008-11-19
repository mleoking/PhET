package edu.colorado.phet.build;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

/**
 * Author: Sam Reid
 * May 19, 2007, 1:39:19 AM
 */
public class PhetBuildGUITask extends AbstractPhetTask {
    private JFrame frame;
    private final Object blocker = new Object();
    private JList localeList;
    private JList simList;
    private JButton runButton;

    // The method executing the task
    public final void execute() throws BuildException {
        buildGUI();
        start();
        //avoid closing ant until we've finished with this application
        synchronized( blocker ) {
            try {
                int hours = 1;
                blocker.wait( 1000 * 60 * 60 * hours );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    private void buildGUI() {

        this.frame = new JFrame( "PhET Build" );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                synchronized( blocker ) {
                    blocker.notifyAll();
                }
            }
        } );

        Simulation[] sims = listSimulations();

        simList = new JList( sims );
        simList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        simList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                updateLists();
            }
        } );
        JPanel contentPane = new JPanel();

        localeList = new JList( new Object[]{} );
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets( 2, 2, 2, 2 ), 0, 0 );
        JScrollPane simListPane = new JScrollPane( simList );
        simListPane.setBorder( BorderFactory.createTitledBorder( "Simulations" ) );
        contentPane.add( simListPane, gridBagConstraints );

        JScrollPane localeScrollPane = new JScrollPane( localeList );
        localeScrollPane.setPreferredSize( new Dimension( 100, 50 ) );
        localeScrollPane.setBorder( BorderFactory.createTitledBorder( "Languages" ) );
        contentPane.add( localeScrollPane, gridBagConstraints );

        JPanel commandPanel = new JPanel();
        JButton refresh = new JButton( "Refresh" );
        refresh.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                refresh();
            }
        } );
        runButton = new JButton( "Build & Run" );
        runButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                run();
            }
        } );
        updateRunButtonEnabled();
        JButton showLocalizationFile = new JButton( "Show Localization File" );
        showLocalizationFile.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showLocalizationFile();
            }
        } );

        GridBagConstraints commandConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        commandPanel.setLayout( new GridBagLayout() );

        commandPanel.add( runButton, commandConstraints );
        commandPanel.add( Box.createVerticalBox() );

        contentPane.add( commandPanel );


        frame.setSize( 500, 300 );
        frame.setContentPane( contentPane );
    }

    /*
     * Creates a sorted list of simulations.
     */
    private Simulation[] listSimulations() {
        String[] simNames = toArray( getProperty( new PhetListSimTask() ) );
        ArrayList simulations = new ArrayList();
        for ( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
            String[] flavors = getFlavors( simName );
            for ( int j = 0; j < flavors.length; j++ ) {
                String flavor = flavors[j];
                simulations.add( new Simulation( simName, flavor ) );
            }
        }
        Collections.sort( simulations );
        return (Simulation[]) simulations.toArray( new Simulation[0] );
    }

    private static class Simulation implements Comparable {
        private String simName;
        private String flavorName;

        public Simulation( String simName, String flavorName ) {
            this.flavorName = flavorName;
            this.simName = simName;
        }

        public String getSimName() {
            return simName;
        }

        public String getFlavorName() {
            return flavorName;
        }

        public String toString() {
            return flavorName;
        }

        public int compareTo( Object o ) {
            return toString().compareTo( ((Simulation)o).toString() );
        }
    }

    private void showLocalizationFile() {
        PhetProject project = getSelectedProject();
        String locale = getSelectedLocale();
        File localizationFile = project.getLocalizationFile( locale );
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader( localizationFile ) );
            String text = "";
            for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
                text += line + System.getProperty( "line.separator" );
            }
            JTextArea jta = new JTextArea( text );
            JFrame frame = new JFrame( "Localization file for: " + getSelectedProject() + " " + getSelectedLocale() + ". " + localizationFile.getAbsolutePath() );
            frame.setContentPane( new JScrollPane( jta ) );
            frame.setSize( 800, 600 );
            frame.setVisible( true );
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        updateLists();
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

    private void doRun( final JDialog dialog ) {
        String sim = getSelectedSimulation().getSimName();
        String locale = getSelectedLocale();
        System.out.println( "Building sim: " + sim );
        PhetBuildTask phetBuildTask = new PhetBuildTask();
        phetBuildTask.setProject( sim );
        runTask( phetBuildTask );
        System.out.println( "Build complete" );
        Java java = new Java();

        PhetProject phetProject = getSelectedProject();
        if ( phetProject != null ) {
            java.setClassname( phetProject.getFlavor( getSelectedSimulation().getFlavorName() ).getMainclass() );
            java.setFork( true );
            String args = "";
            String[] a = phetProject.getFlavor( getSelectedSimulation().getFlavorName() ).getArgs();
            for ( int i = 0; i < a.length; i++ ) {
                String s = a[i];
                args += s + " ";
            }
            java.setArgs( args );
            Path classpath = new Path( getProject() );
            FileSet set = new FileSet();
            set.setFile( phetProject.getDefaultDeployJar() );
            classpath.addFileset( set );
            java.setClasspath( classpath );
            if ( !locale.equals( "en" ) ) {
                java.setJvmargs( "-Djavaws.phet.locale=" + locale );
            }
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    dialog.dispose();
                }
            } );
            runTask( java );
        }
    }

    private Simulation getSelectedSimulation() {
        return (Simulation) simList.getSelectedValue();
    }

    private String getSelectedLocale() {
        return (String) localeList.getSelectedValue();
    }

    private PhetProject getSelectedProject() {
        String sim = getSelectedSimulation().getSimName();
        File projectParentDir = PhetBuildUtils.resolveProject( getProject().getBaseDir(), sim );
        try {
            return new PhetProject( projectParentDir, sim );
        }
        catch( IOException e ) {
            e.printStackTrace();
            System.out.println( "no project selected" );
            return null;
        }
    }

    private String[] toArray( String simListString ) {
        ArrayList simNames = new ArrayList();
        StringTokenizer st = new StringTokenizer( simListString, "," );
        while ( st.hasMoreTokens() ) {
            simNames.add( st.nextToken() );
        }
        return (String[]) simNames.toArray( new String[0] );
    }

    private String getProperty( Task task ) {
        ( (PropertyTask) task ).setProperty( "phet.sim.list" );
        runTask( task );
        return getProject().getProperty( "phet.sim.list" );
    }

    public java.lang.String[] getFlavors( String sim ) {
        PhetListFlavorsTask flavorsTask = new PhetListFlavorsTask();
        flavorsTask.setProject( sim );
        return toArray( getProperty( flavorsTask ) );
    }

    private void updateLists() {
        PhetListLocalesTask localesTask = new PhetListLocalesTask();
        localesTask.setProject( getSelectedSimulation().getSimName() );
        localeList.setListData( setDefaultValueEnglish( toArray( getProperty( localesTask ) ) ) );
        localeList.setSelectedIndex( 0 );
        updateRunButtonEnabled();
    }

    private void updateRunButtonEnabled() {
        runButton.setEnabled( getSelectedSimulation() != null );
    }

    private String[] setDefaultValueEnglish( String[] strings ) {
        ArrayList list = new ArrayList( Arrays.asList( strings ) );
        list.remove( "en" );
        list.add( 0, "en" );
        return (String[]) list.toArray( new String[0] );
    }

    private void start() {
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.setVisible( true );
    }
}