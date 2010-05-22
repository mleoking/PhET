package edu.colorado.phet.website.content.contribution;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.SignInPage;
import edu.colorado.phet.website.content.NotFoundPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionNomination;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class NominateContributionPage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( NominateContributionPage.class.getName() );

    private int contributionId;
    private Contribution contribution;

    public NominateContributionPage( PageParameters parameters ) {
        super( parameters );

        contributionId = parameters.getInt( "contribution_id" );
        final String reason = parameters.getString( "reason" );

        if ( !PhetSession.get().isSignedIn() ) {
            setResponsePage( new RedirectPage( SignInPage.getLinker( getLinker( contributionId, reason ).getRawUrl( getPageContext(), getPhetCycle() ) ).getRawUrl( getPageContext(), getPhetCycle() ) ) );
            return;
        }

        final PhetUser user = PhetSession.get().getUser();

        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                contribution = (Contribution) session.createQuery( "select c from Contribution as c where c.id = :id" ).setInteger( "id", contributionId ).uniqueResult();
                return true;
            }
        } );

        if ( !success ) {
            logger.info( "unknown contribution of id " + contributionId );
            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
        }

        logger.debug( "adding nomination to contribution " + contribution.getId() + " with user " + user.getEmail() + " and reason " + reason );
        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                Contribution contrib = (Contribution) session.load( Contribution.class, contribution.getId() );
                PhetUser phetuser = (PhetUser) session.load( PhetUser.class, user.getId() );
                ContributionNomination nomination = new ContributionNomination();
                nomination.setContribution( contrib );
                nomination.setDateCreated( new Date() );
                nomination.setPhetUser( phetuser );
                nomination.setReason( reason );
                session.save( nomination );
                return true;
            }
        } );

        // redirect back to contribution page
        setResponsePage( new RedirectPage( ContributionPage.getLinker( contributionId ).getRawUrl( getPageContext(), getPhetCycle() ) ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/submit-nomination", NominateContributionPage.class, new String[]{} );
    }

    public static RawLinkable getLinker( final int contributionId, final String text ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                try {
                    return "contributions/submit-nomination?contribution_id=" + contributionId + "&reason=" + URLEncoder.encode( text, "UTF-8" );
                }
                catch( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        };
    }

    public static RawLinkable getLinker( Contribution contribution, String text ) {
        return getLinker( contribution.getId(), text );
    }

    public static RawLinkable getBaseLinker() {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "contributions/submit-nomination";
            }
        };
    }

}