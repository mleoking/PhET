package edu.colorado.phet.website.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminProjectsPage extends AdminPage {
    private List<Project> projects;

    public AdminProjectsPage( PageParameters parameters ) {
        super( parameters );

        projects = new LinkedList<Project>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List ps = session.createQuery( "select p from Project as p" ).list();
                for ( Object p : ps ) {
                    projects.add( (Project) p );
                }
                return true;
            }
        } );

        sortProjects();

        ListView projectList = new ListView( "project-list", projects ) {
            protected void populateItem( ListItem item ) {
                final Project project = (Project) item.getModel().getObject();
                Link projectLink = new Link( "project-link" ) {
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.put( "projectId", project.getId() );
                        setResponsePage( AdminProjectPage.class, params );
                    }
                };
                projectLink.add( new Label( "project-name", project.getName() ) );
                item.add( projectLink );
                item.add( new Label( "project-version", project.getVersionString() ) );
                item.add( new Link( "remove-link" ) {
                    public void onClick() {
                        try {
                            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                                public boolean run( Session session ) {
                                    Project p = (Project) session.load( Project.class, project.getId() );
                                    // TODO: add checks and error messages for ways this can fail
                                    session.delete( p );
                                    return true;
                                }
                            } );
                            // success should be false if there are other connections to the project
                            if ( success ) {
                                projects.remove( project );
                            }
                        }
                        catch( RuntimeException e ) {
                            e.printStackTrace();
                        }
                    }
                } );
            }
        };

        add( projectList );

        add( new UpdateSpecificProjectForm( "update-named-project-form" ) );

        add( new Form( "update-all-projects-form" ) {
            @Override
            protected void onSubmit() {
                for ( Project project : projects ) {
                    Project.synchronizeProject( ( (PhetWicketApplication) getApplication() ).getWebsiteProperties().getPhetDocumentRoot(), getHibernateSession(), project.getName() );
                }
            }
        } );

    }

    private void sortProjects() {
        Collections.sort( projects, new Comparator<Project>() {
            public int compare( Project a, Project b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );
    }

    public final class UpdateSpecificProjectForm extends Form {
        private static final long serialVersionUID = 1L;

        private TextField projectText;

        private final ValueMap properties = new ValueMap();

        public UpdateSpecificProjectForm( final String id ) {
            super( id );

            add( projectText = new StringTextField( "project", new PropertyModel( properties, "project" ) ) );
        }

        public final void onSubmit() {
            String projectName = projectText.getModelObject().toString();

            Project.synchronizeProject( ( (PhetWicketApplication) getApplication() ).getWebsiteProperties().getPhetDocumentRoot(), getHibernateSession(), projectName );
        }
    }

}
