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

    private static final Logger logger = Logger.getLogger( AdminEditProfilePage.class.getName() );

    private PhetUser user;

    public AdminEditProfilePage( PageParameters parameters ) {
        super( parameters );

        final String userParam = parameters.getString( "userId" );
        final String userEmail = parameters.getString( "userEmail" );

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                if ( userParam == null && userEmail != null ) {
                    user = (PhetUser) session.createQuery( "select u from PhetUser as u where u.email = :email" )
                            .setString( "email", userEmail ).uniqueResult();
                }
                else {
                    int id = Integer.parseInt( userParam );
                    user = (PhetUser) session.load( PhetUser.class, id );
                }
                return true;
            }
        } );

        add( new EditProfilePanel( "edit-profile-panel", getPageContext(), user, null ) );

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

    public static Linkable getLinker( final String userEmail ) {
        return new Linkable() {
            public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
                return new QuickLink( id, userEmail );
            }
        };
    }

    private static class QuickLink extends Link {
        private int userId;
        private String userEmail = null;

        public QuickLink( String id, int userId ) {
            super( id );
            this.userId = userId;
        }

        public QuickLink( String id, String userEmail ) {
            super( id );
            this.userEmail = userEmail;
        }

        public void onClick() {
            PageParameters params = new PageParameters();
            if ( userEmail == null ) {
                params.add( "userId", String.valueOf( userId ) );
            }
            else {
                params.add( "userEmail", userEmail );
            }
            setResponsePage( AdminEditProfilePage.class, params );
        }
    }

}