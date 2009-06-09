package edu.colorado.phet.buildtools.test.gui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.statistics.StatisticsProject;

public class TestProjectList extends JList {

    private File trunk;
    private DefaultListModel model;

    public TestProjectList( File trunk ) {
        this.trunk = trunk;

        model = new DefaultListModel();

        setModel( model );

        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        initializeProjects();

        addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent listSelectionEvent ) {
                notifyChanged();
            }
        } );
    }

    public PhetProject getSelectedProject() {
        int index = getSelectedIndex();
        if ( index < 0 ) {
            return null;
        }
        return ( (ProjectListElement) ( model.get( index ) ) ).getProject();
    }

    private void initializeProjects() {
        for ( PhetProject phetProject : JavaSimulationProject.getJavaSimulations( trunk ) ) {
            model.addElement( new ProjectListElement( phetProject ) );
        }

        for ( PhetProject phetProject : FlashSimulationProject.getFlashSimulations( trunk ) ) {
            model.addElement( new ProjectListElement( phetProject ) );
        }

        try {
            model.addElement( new ProjectListElement( new StatisticsProject( trunk ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
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


    private List<Listener> listeners = new LinkedList<Listener>();

    public static interface Listener {
        public void notifyChanged();
    }

    public void notifyChanged() {
        for ( Listener listener : listeners ) {
            listener.notifyChanged();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }
}
