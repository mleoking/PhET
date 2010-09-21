package edu.colorado.phet.website.admin;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminProjectConsistencyReport extends AdminPage {
    public AdminProjectConsistencyReport( PageParameters parameters ) {
        super( parameters );

        final StringBuilder builder = new StringBuilder();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List list = session.createQuery( "select p from Project as p where p.visible = true" ).list();
                for ( Object o : list ) {
                    Project project = (Project) o;
                    builder.append( project.consistencyCheck( ( (PhetWicketApplication) getApplication() ).getWebsiteProperties().getPhetDocumentRoot() ) );
                }
                return true;
            }
        } );

        add( new RawLabel( "text", builder.toString() ) );

    }
}