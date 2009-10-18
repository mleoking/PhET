package edu.colorado.phet.website.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminProjectsPage extends AdminPage {
    public AdminProjectsPage( PageParameters parameters ) {
        super( parameters );

        final List<Project> projects = new LinkedList<Project>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List ps = session.createQuery( "select p from Project as p" ).list();
                for ( Object p : ps ) {
                    projects.add( (Project) p );
                }
                return true;
            }
        } );

        Collections.sort( projects, new Comparator<Project>() {
            public int compare( Project a, Project b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );

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
            }
        };

        add( projectList );
    }
}
