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

        // don't try to serialize this
        user = null;

    }

    public static Linkable getLinker( final PhetUser user ) {
        return new Linkable() {
            public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
                return new Link( id ) {
                    @Override
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.add( "userId", String.valueOf( user.getId() ) );
                        setResponsePage( AdminEditProfilePage.class, params );
                    }
                };
            }
        };
    }

}