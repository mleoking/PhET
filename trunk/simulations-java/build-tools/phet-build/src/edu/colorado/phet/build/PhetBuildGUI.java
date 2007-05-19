package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Author: Sam Reid
 * May 19, 2007, 1:39:19 AM
 */
public class PhetBuildGUI extends AbstractPhetTask {
    private JFrame frame;
    private final Object blocker = new Object();
    private JList flavorList;
    private JList localeList;
    private JList simList;

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

        String[] objects = toArray( getProperty( new PhetListSimTask() ) );

        simList = new JList( objects );
        simList.setPreferredSize( new Dimension( simList.getPreferredSize().width, 400 ) );
        simList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        simList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                updateLists();
            }
        } );
        JPanel contentPane = new JPanel();

        flavorList = new JList( new Object[]{} );
        localeList = new JList( new Object[]{} );
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 2, 2, 2, 2 ), 0, 0 );
        contentPane.add( new JScrollPane( simList ), gridBagConstraints );
        contentPane.add( new JScrollPane( flavorList ), gridBagConstraints );
        contentPane.add( new JScrollPane( localeList ), gridBagConstraints );

        JPanel commandPanel = new JPanel();
        JButton run = new JButton( "Run" );
        run.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                run();
            }
        } );
        GridBagConstraints commandConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        commandPanel.setLayout( new GridBagLayout() );
        commandPanel.add( run, commandConstraints );

        contentPane.add( commandPanel );

        frame.setContentPane( contentPane );
        frame.setSize( 800, 600 );
        frame.pack();
        frame.setSize( frame.getWidth() + 100, frame.getHeight() + 100 );
    }

    private void run() {
        String sim = (String)simList.getSelectedValue();
        String flavor = (String)flavorList.getSelectedValue();
        String locale = (String)localeList.getSelectedValue();
        System.out.println( "Building sim: " + sim );
        PhetBuildTask phetBuildTask = new PhetBuildTask();
        phetBuildTask.setProject( sim );
        runTask( phetBuildTask );
        System.out.println( "Build complete" );
        Java java = new Java();
        File projectParentDir = PhetBuildUtils.resolveProject( getProject().getBaseDir(), sim );
        try {
            PhetProject phetProject = new PhetProject( projectParentDir, sim );
            java.setClassname( phetProject.getFlavor( flavor, locale ).getMainclass() );
            java.setFork( true );
            Path classpath = new Path( getProject() );
            FileSet set = new FileSet();
            set.setFile( phetProject.getDefaultDeployJar() );
            classpath.addFileset( set );
            java.setClasspath( classpath );
            java.setJvmargs( "-Djavaws.phet.locale=" + locale );
            runTask( java );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

    private String[] toArray( String simListString ) {
        ArrayList simNames = new ArrayList();
        StringTokenizer st = new StringTokenizer( simListString, "," );
        while( st.hasMoreTokens() ) {
            simNames.add( st.nextToken() );
        }
        return (String[])simNames.toArray( new String[0] );
    }

    private String getProperty( Task task ) {
        ( (PropertyTask)task ).setProperty( "phet.sim.list" );
        runTask( task );
        return getProject().getProperty( "phet.sim.list" );
    }

    private String getSelectedSim() {
        return (String)simList.getSelectedValue();
    }

    private void updateLists() {
        PhetListFlavorsTask flavorsTask = new PhetListFlavorsTask();
        flavorsTask.setProject( getSelectedSim() );
        flavorList.setListData( toArray( getProperty( flavorsTask ) ) );

        PhetListLocalesTask localesTask = new PhetListLocalesTask();
        localesTask.setProject( getSelectedSim() );
        localeList.setListData( toArray( getProperty( localesTask ) ) );
    }

    private void start() {
        frame.setVisible( true );
    }
}