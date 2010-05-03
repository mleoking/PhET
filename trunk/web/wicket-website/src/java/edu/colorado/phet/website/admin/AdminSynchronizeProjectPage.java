package edu.colorado.phet.website.admin;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;

import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;
import edu.colorado.phet.website.content.IndexPage;

/**
 * Page meant solely for handling submitted forms for adding comments. If the user isn't logged in, they are redirected
 * to the sign in page with the correct destination specified to return (so it will save their comment)
 */
public class AdminSynchronizeProjectPage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( AdminSynchronizeProjectPage.class.getName() );

    private int contributionId;
    private Contribution contribution;

    public AdminSynchronizeProjectPage( PageParameters parameters ) {
        super( parameters );

        String addr = getPhetCycle().getWebRequest().getHttpServletRequest().getRemoteAddr();
        String host = getPhetCycle().getWebRequest().getHttpServletRequest().getRemoteHost();

        logger.debug( "addr: " + addr );
        logger.debug( "host: " + host );

//        contributionId = parameters.getInt( "contribution_id" );
//        final String text = parameters.getString( "text" );
//
//        if ( !PhetSession.get().isSignedIn() ) {
//            setResponsePage( new RedirectPage( SignInPage.getLinker( getLinker( contributionId, text ).getRawUrl( getPageContext(), getPhetCycle() ) ).getRawUrl( getPageContext(), getPhetCycle() ) ) );
//            return;
//        }
//
//        final PhetUser user = PhetSession.get().getUser();
//
//        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
//            public boolean run( Session session ) {
//                contribution = (Contribution) session.createQuery( "select c from Contribution as c where c.id = :id" ).setInteger( "id", contributionId ).uniqueResult();
//                return true;
//            }
//        } );
//
//        if ( !success ) {
//            logger.info( "unknown contribution of id " + contributionId );
//            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
//        }
//
//        logger.debug( "adding comment to contribution " + contribution.getId() + " with user " + user.getEmail() + " and text " + text );
//        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
//            public boolean run( Session session ) {
//                Contribution contrib = (Contribution) session.load( Contribution.class, contribution.getId() );
//                PhetUser phetuser = (PhetUser) session.load( PhetUser.class, user.getId() );
//                ContributionComment comment = new ContributionComment();
//                comment.setText( text );
//                comment.setDateCreated( new Date() );
//                comment.setDateUpdated( new Date() );
//                comment.setContribution( contrib );
//                comment.setPhetUser( phetuser );
//                session.save( comment );
//                return true;
//            }
//        } );

        // redirect back to contribution page
        setResponsePage( new RedirectPage( "/" ) );

    }

//    public static void addToMapper( PhetUrlMapper mapper ) {
//        mapper.addMap( "^admin/submit-comment$", AdminSynchronizeProjectPage.class, new String[]{} );
//    }

    public static RawLinkable getLinker( final int projectId ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "admin/synchronize?project=" + projectId;
            }
        };
    }

    public static RawLinkable getLinker( Project project ) {
        return getLinker( project.getId() );
    }

}