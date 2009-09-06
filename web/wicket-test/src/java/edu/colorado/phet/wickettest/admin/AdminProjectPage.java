package edu.colorado.phet.wickettest.admin;

import java.util.Arrays;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.wickettest.data.Project;
import edu.colorado.phet.wickettest.util.HibernateTask;
import edu.colorado.phet.wickettest.util.HibernateUtils;

public class AdminProjectPage extends AdminPage {

    private Project project;
    private int projectId;
    private Label title;

    public AdminProjectPage( PageParameters parameters ) {
        super( parameters );

        projectId = parameters.getInt( "projectId" );

        HibernateUtils.wrapTransaction( getHibernateSession(), new StartTask() );

        title = new Label( "project-name", getTitleString() );
        add( title );

        add( new ProjectForm( "edit-form" ) );

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
            return true;
        }
    }
}
