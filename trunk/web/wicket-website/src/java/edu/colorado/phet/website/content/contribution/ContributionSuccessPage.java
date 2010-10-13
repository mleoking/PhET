package edu.colorado.phet.website.content.contribution;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Shown to the user after successfully creating or editing a contribution
 */
public class ContributionSuccessPage extends PhetMenuPage {

    private static final Logger logger = Logger.getLogger( ContributionSuccessPage.class.getName() );

    private int contributionId;
    //private Contribution contribution;

    public ContributionSuccessPage( PageParameters parameters ) {
        super( parameters );

        contributionId = parameters.getInt( "contribution" );

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas" ) );

//        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
//            public boolean run( Session session ) {
//                contribution = (Contribution) session.createQuery( "select c from Contribution as c where c.id = :id" ).setInteger( "id", contributionId ).uniqueResult();
//                return true;
//            }
//        } );
//
//        if ( !success ) {
//            logger.warn( "unknown contribution of id " + contributionId );
//            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
//        }

        add( ContributionPage.getLinker( contributionId ).getLink( "contribution-link", getPageContext(), getPhetCycle() ) );

        setTitle( getLocalizer().getString( "contribution.edit.success", this ) );

        hideSocialBookmarkButtons();

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/update-success/([^/]+)$", ContributionSuccessPage.class, new String[] { "contribution" } );
    }

    public static RawLinkable getLinker( final int contributionId ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "contributions/update-success/" + contributionId;
            }
        };
    }

    public static RawLinkable getLinker( Contribution contribution ) {
        return getLinker( contribution.getId() );
    }

}