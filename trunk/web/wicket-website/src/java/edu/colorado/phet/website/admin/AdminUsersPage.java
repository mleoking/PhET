package edu.colorado.phet.website.admin;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminUsersPage extends AdminPage {

    private static Logger logger = Logger.getLogger( AdminUsersPage.class.getName() );

    public AdminUsersPage( PageParameters parameters ) {
        super( parameters );

        final List<PhetUser> teamMembers = new LinkedList<PhetUser>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List list = session.createQuery( "select u from PhetUser as u where u.teamMember = true" ).list();
                for ( Object o : list ) {
                    teamMembers.add( (PhetUser) o );
                }
                return true;
            }
        } );

        add( new ListView( "member", teamMembers ) {
            protected void populateItem( ListItem item ) {
                PhetUser user = (PhetUser) item.getModel().getObject();
                Link link = AdminEditProfilePage.getLinker( user ).getLink( "member-link", getPageContext(), getPhetCycle() );
                item.add( link );
                link.add( new Label( "email", user.getEmail() ) );
            }
        } );

    }
}