package edu.colorado.phet.website.admin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.buildtools.util.ProjectPropertiesFile;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.components.RawLabel;
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

    private static final Logger logger = Logger.getLogger( AdminProjectPage.class.getName() );
    private AdminProjectPage.ProjectForm projectForm;

    public AdminProjectPage( PageParameters parameters ) {
        super( parameters );

        projectId = parameters.getInt( "projectId" );

        // fill in project, simulation list and status string
        HibernateUtils.wrapTransaction( getHibernateSession(), new StartTask() );

        title = new Label( "project-name", getTitleString() );
        add( title );

        projectForm = new ProjectForm( "edit-form" );
        add( projectForm );

        add( new AddSimulationForm( "simulations-form" ) );

        ProjectPropertiesFile projectPropertiesFile = project.getProjectPropertiesFile( ( (PhetWicketApplication) getApplication() ).getWebsiteProperties().getPhetDocumentRoot() );

        RawLabel projectChecks = null;
        if ( projectPropertiesFile.exists() ) {
            String str = "Detected project properties: " + projectPropertiesFile.getFullVersionString();
            if ( statusString != null ) {
                str += "<br/>" + statusString;
            }
            projectChecks = new RawLabel( "project-properties", str );
        }
        else {
            projectChecks = new RawLabel( "project-properties", "No project properties detected" );
        }
        add( projectChecks );

        add( new ListView( "simulation", simulations ) {
            protected void populateItem( ListItem item ) {
                Simulation sim = (Simulation) item.getModel().getObject();
                item.add( new Label( "simulation-name", sim.getName() ) );
            }
        } );

        add( new Link( "synchronize-link" ) {
            public void onClick() {
                Project.synchronizeProject( ( (PhetWicketApplication) getApplication() ).getWebsiteProperties().getPhetDocumentRoot(), getHibernateSession(), project.getName() );
                // TODO: get rid of this ugly way of updating everything on the page
                simulations.clear();
                HibernateUtils.wrapTransaction( getHibernateSession(), new StartTask() );
                projectForm.update( project );
                title.setModel( new Model( getTitleString() ) );

                PageParameters params = new PageParameters();
                params.put( "projectId", projectId );
                setResponsePage( AdminProjectPage.class, params );
            }
        } );

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

        public void setMajor( int major ) {
            this.major.setModel( new Model( String.valueOf( major ) ) );
        }

        public void setMinor( int minor ) {
            this.minor.setModel( new Model( String.valueOf( minor ) ) );
        }

        public void setDev( int dev ) {
            this.dev.setModel( new Model( String.valueOf( dev ) ) );
        }

        public void setRevision( int revision ) {
            this.revision.setModel( new Model( String.valueOf( revision ) ) );
        }

        public void setTimestamp( long timestamp ) {
            this.timestamp.setModel( new Model( String.valueOf( timestamp ) ) );
        }

        public void update( Project project ) {
            setMajor( project.getVersionMajor() );
            setMinor( project.getVersionMinor() );
            setDev( project.getVersionDev() );
            setRevision( project.getVersionRevision() );
            setTimestamp( project.getVersionTimestamp() );
        }

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

            major = new StringTextField( "major", new Model( String.valueOf( project.getVersionMajor() ) ) );
            add( major );
            minor = new StringTextField( "minor", new Model( String.valueOf( project.getVersionMinor() ) ) );
            add( minor );
            dev = new StringTextField( "dev", new Model( String.valueOf( project.getVersionDev() ) ) );
            add( dev );
            revision = new StringTextField( "revision", new Model( String.valueOf( project.getVersionRevision() ) ) );
            add( revision );
            timestamp = new StringTextField( "timestamp", new Model( String.valueOf( project.getVersionTimestamp() ) ) );
            add( timestamp );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            logger.info( "setting project values for " + project.getName() );
            logger.info( "visible: " + visible.getModelValue() );
            logger.info( "major: " + major.getModelObjectAsString() );
            logger.info( "minor: " + minor.getModelObjectAsString() );
            logger.info( "dev: " + dev.getModelObjectAsString() );
            logger.info( "revision: " + revision.getModelObjectAsString() );
            logger.info( "timestamp: " + timestamp.getModelObjectAsString() );

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
            statusString = project.consistencyCheck( ( (PhetWicketApplication) getApplication() ).getWebsiteProperties().getPhetDocumentRoot() );
            return true;
        }
    }

    private class AddSimulationForm extends Form {

        private TextField nameField;

        public AddSimulationForm( String id ) {
            super( id );

            nameField = new StringTextField( "name", new Model( project.getName() ) );
            add( nameField );
        }

        @Override
        protected void onSubmit() {
            final String name = nameField.getModelObjectAsString();

            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation simulation = new Simulation();
                    simulation.setSimulationVisible( true );
                    simulation.setName( name );
                    Project p = (Project) session.load( Project.class, project.getId() );
                    simulation.setProject( p );
                    session.save( simulation );
                    return true;
                }
            } );
        }
    }
}
