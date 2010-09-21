package edu.colorado.phet.website.authentication;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.panels.ChangePasswordPanel;
import edu.colorado.phet.website.content.ErrorPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.ResetPasswordRequest;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * The landing page for when a user clicks on a forgot-my-password link in their email.
 *
 * @author Sam Reid
 */
public class ResetPasswordCallbackPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( ResetPasswordCallbackPage.class );

    public ResetPasswordCallbackPage( PageParameters parameters ) {
        super( parameters );
        addTitle( new ResourceModel( "resetPasswordCallback.title" ) );

        // for now, if a step here fails we will just go to the error page
        PhetUser user = lookupUserForResetPasswordKey( parameters.getString( "key" ) );
        if ( user == null ) {
            throw new RestartResponseAtInterceptPageException( ErrorPage.class );
        }

        add( new ChangePasswordPanel( "reset-password-callback-panel", getPageContext(), user, false ) );
    }

    private PhetUser lookupUserForResetPasswordKey( final String key ) {
        final PhetUser[] savedUser = {null};
        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List matches = session.createQuery( "select ts from ResetPasswordRequest as ts where ts.key = :key" ).setString( "key", key ).list();
                if ( matches.size() == 0 ) {
                    logger.info( "No matches in database for key: " + key );
                    return false;
                }
                else if ( matches.size() > 1 ) {
                    logger.warn( "Multiple database matches for key: " + key );
                    return false;
                }
                else {
                    ResetPasswordRequest request = (ResetPasswordRequest) matches.get( 0 );
                    PhetUser user = request.getPhetUser();
                    savedUser[0] = user;
                    return true;
                }
            }
        } );
        return savedUser[0];//if success==false, this still returns null
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^reset-password-callback$", ResetPasswordCallbackPage.class );//Wicket automatically strips the "?key=..."
    }

    public static RawLinkable getLinker( final String key ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "reset-password-callback?key=" + key;
            }
        };
    }
}
