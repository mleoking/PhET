package edu.colorado.phet.buildtools.gui;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.buildtools.PhetProject;

/**
 * A list of projects that can be selected
 */
public class ProjectList extends JList {

    private File trunk;
    private DefaultListModel model;

    public ProjectList( File trunk ) {
        this.trunk = trunk;

        model = new DefaultListModel();

        setModel( model );

        // only select one at a time
        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        // render cells in a custom way
        setCellRenderer( new ProjectCellRenderer( trunk ) );

        // add all of the projects
        initializeProjects();

        final ProjectListElement defaultProject = getDefaultProject();
        if ( defaultProject != null ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    ProjectList.this.setSelectedValue( defaultProject, true );
                    onProjectChange();
                }
            } );
        }

        addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent listSelectionEvent ) {
                onProjectChange();
            }
        } );
    }

    private void onProjectChange() {
        notifyChanged();
        saveNewProjectSelection();
    }

    public PhetProject getSelectedProject() {
        int index = getSelectedIndex();
        if ( index < 0 ) {
            return null;
        }
        return ( (ProjectListElement) ( model.get( index ) ) ).getProject();
    }

    private void initializeProjects() {
        List<PhetProject> projects = PhetProject.getListOfAllPhetProjects( trunk );

        // sort by name
        Collections.sort( projects, new Comparator<PhetProject>() {
            public int compare( PhetProject a, PhetProject b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );

        for ( PhetProject project : projects ) {
            model.addElement( new ProjectListElement( project ) );
        }

    }

    private void saveNewProjectSelection() {
        PhetBuildGUIProperties.getInstance().setProjectSelected( getSelectedProject().getName() );
    }

    private ProjectListElement getDefaultProject() {
        ProjectListElement element = null;
        String name = PhetBuildGUIProperties.getInstance().getProjectSelected();
        if ( name != null ) {
            Enumeration elements = model.elements();
            while ( elements.hasMoreElements() && element == null ) {
                ProjectListElement e = (ProjectListElement) elements.nextElement();
                if ( e.getProject().getName().equals( name ) ) {
                    element = e;
                }
            }
        }
        return element;
    }

    public static class ProjectListElement {
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
        Listener[] listenerArray = listeners.toArray( new Listener[0] );
        for ( Listener listener : listenerArray ) {
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
