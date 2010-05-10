package edu.colorado.phet.website.admin;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.panels.EditProfilePanel;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.Linkable;

public class AdminEditProfilePage extends AdminPage {

    private static Logger logger = Logger.getLogger( AdminEditProfilePage.class.getName() );

    private PhetUser user;

    public AdminEditProfilePage( PageParameters parameters ) {
        super( parameters );

        String userParam = parameters.getString( "userId" );
        final int id = Integer.parseInt( userParam );

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                user = (PhetUser) session.load( PhetUser.class, id );
                return true;
            }
        } );

        add( new EditProfilePanel( "edit-profile-panel", getPageContext(), user ) );

        add( new Label( "user-email", user.getEmail() ) );

    }

    @Override
    protected void onDetach() {
        super.onDetach();
        user = null;
    }

    public static Linkable getLinker( final PhetUser user ) {
        final int userId = user.getId();
        return new Linkable() {
            public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
                return new QuickLink( id, userId );
            }
        };
    }

    private static class QuickLink extends Link {
        private int userId;

        public QuickLink( String id, int userId ) {
            super( id );
            this.userId = userId;
        }

        public void onClick() {
            PageParameters params = new PageParameters();
            params.add( "userId", String.valueOf( userId ) );
            setResponsePage( AdminEditProfilePage.class, params );
        }
    }

}