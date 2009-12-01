package edu.colorado.phet.website.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.buildtools.util.ProjectPropertiesFile;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminProjectPage extends AdminPage {

    private Project project;
    private int projectId;
    private Label title;

    private String statusString = null;

    private List<Simulation> simulations = new LinkedList<Simulation>();

    public AdminProjectPage( PageParameters parameters ) {
        super( parameters );

        projectId = parameters.getInt( "projectId" );

        HibernateUtils.wrapTransaction( getHibernateSession(), new StartTask() );

        title = new Label( "project-name", getTitleString() );
        add( title );

        add( new ProjectForm( "edit-form" ) );

        add( new AddSimulationForm( "simulations-form" ) );

        ProjectPropertiesFile projectPropertiesFile = project.getProjectPropertiesFile( ( (PhetWicketApplication) getApplication() ).getPhetDocumentRoot() );

        Label projectChecks = null;
        if ( projectPropertiesFile.exists() ) {
            String str = "Detected project properties: " + projectPropertiesFile.getFullVersionString();
            if ( statusString != null ) {
                str += "<br/>" + statusString;
            }
            projectChecks = new Label( "project-properties", str );
        }
        else {
            projectChecks = new Label( "project-properties", "No project properties detected" );
        }
        projectChecks.setEscapeModelStrings( false );
        add( projectChecks );

        add( new ListView( "simulation", simulations ) {
            protected void populateItem( ListItem item ) {
                Simulation sim = (Simulation) item.getModel().getObject();
                item.add( new Label( "simulation-name", sim.getName() ) );
            }
        } );

        try {
            project.debugProjectFiles( ( (PhetWicketApplication) getApplication() ).getPhetDocumentRoot() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }

    }

    private String getTitleString() {
        return project.getName() + " " + project.getVersionString() + " (" + project.getVersionRevision() + ")";
    }

    private class ProjectForm extends Form {
        private TextField major;
        private TextField minor;
        private TextField dev;
        private TextField revision;
        private TextField timestamp;
        private DropDownChoice visible;

        public ProjectForm( String id ) {
            super( id );

            visible = new DropDownChoice( "visible", new Model( project.isVisible() ? "True" : "False" ), Arrays.asList( "True", "False" ), new IChoiceRenderer() {
                public Object getDisplayValue( Object object ) {
                    return object.toString();
                }

                public String getIdValue( Object object, int index ) {
                    return String.valueOf( object );
                }
            } );
            add( visible );

            major = new TextField( "major", new Model( String.valueOf( project.getVersionMajor() ) ) );
            add( major );
            minor = new TextField( "minor", new Model( String.valueOf( project.getVersionMinor() ) ) );
            add( minor );
            dev = new TextField( "dev", new Model( String.valueOf( project.getVersionDev() ) ) );
            add( dev );
            revision = new TextField( "revision", new Model( String.valueOf( project.getVersionRevision() ) ) );
            add( revision );
            timestamp = new TextField( "timestamp", new Model( String.valueOf( project.getVersionTimestamp() ) ) );
            add( timestamp );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            System.out.println( "visible: " + visible.getModelValue() );
            System.out.println( "major: " + major.getModelObjectAsString() );
            System.out.println( "minor: " + minor.getModelObjectAsString() );
            System.out.println( "dev: " + dev.getModelObjectAsString() );
            System.out.println( "revision: " + revision.getModelObjectAsString() );
            System.out.println( "timestamp: " + timestamp.getModelObjectAsString() );

            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {

                    project = (Project) session.load( Project.class, project.getId() );

                    if ( visible.getModelValue().equals( "True" ) ) {
                        project.setVisible( true );
                    }
                    else if ( visible.getModelValue().equals( "False" ) ) {

                        project.setVisible( false );
                    }
                    else {
                        throw new RuntimeException( "True or False?" );
                    }

                    project.setVersionMajor( Integer.valueOf( major.getModelObjectAsString() ) );
                    project.setVersionMinor( Integer.valueOf( minor.getModelObjectAsString() ) );
                    project.setVersionDev( Integer.valueOf( dev.getModelObjectAsString() ) );
                    project.setVersionRevision( Integer.valueOf( revision.getModelObjectAsString() ) );
                    project.setVersionTimestamp( Long.valueOf( timestamp.getModelObjectAsString() ) );

                    session.update( project );

                    title.getModel().setObject( getTitleString() );

                    return true;
                }
            } );

        }
    }

    private class StartTask implements HibernateTask {
        public boolean run( Session session ) {
            project = (Project) session.load( Project.class, projectId );
            for ( Object o : project.getSimulations() ) {
                simulations.add( (Simulation) o );
            }
            statusString = project.consistencyCheck( ( (PhetWicketApplication) getApplication() ).getPhetDocumentRoot() );
            return true;
        }
    }

    private class AddSimulationForm extends Form {
        private static final String JAVA_STRING = "Java";
        private static final String FLASH_STRING = "Flash";

        private DropDownChoice typeField;
        private TextField nameField;

        public AddSimulationForm( String id ) {
            super( id );

            typeField = new DropDownChoice( "type", new Model( JAVA_STRING ), Arrays.asList( JAVA_STRING, FLASH_STRING ), new IChoiceRenderer() {
                public Object getDisplayValue( Object object ) {
                    return object.toString();
                }

                public String getIdValue( Object object, int index ) {
                    return String.valueOf( object );
                }
            } );
            add( typeField );

            nameField = new TextField( "name", new Model( project.getName() ) );
            add( nameField );
        }

        @Override
        protected void onSubmit() {
            final String typeString = typeField.getModelObjectAsString();
            final String name = nameField.getModelObjectAsString();

            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation simulation = new Simulation();
                    simulation.setName( name );
                    if ( typeString.equals( JAVA_STRING ) ) {
                        simulation.setType( Simulation.TYPE_JAVA );
                    }
                    else if ( typeString.equals( FLASH_STRING ) ) {
                        simulation.setType( Simulation.TYPE_FLASH );
                    }
                    else {
                        return false;
                    }
                    Project p = (Project) session.load( Project.class, project.getId() );
                    simulation.setProject( p );
                    session.save( simulation );
                    return true;
                }
            } );
        }
    }
}
