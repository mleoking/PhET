package edu.colorado.phet.website.content.contribution;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.hibernate.Session;

import edu.colorado.phet.website.content.NotFoundPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.contribution.ContributionMainPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Holder page for showing a single contribution
 */
public class ContributionPage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( ContributionPage.class.getName() );

    private int contributionId;
    private Contribution contribution;

    public ContributionPage( PageParameters parameters ) {
        super( parameters );

        contributionId = parameters.getInt( "contribution" );

        NavLocation navLocation = getNavMenu().getLocationByKey( "teacherIdeas.browse" );
        logger.debug( navLocation.getLocalizationKey() );
        initializeLocation( navLocation );

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

        ContributionMainPanel contributionPanel = new ContributionMainPanel( "contribution-main-panel", contribution, getPageContext() );
        add( contributionPanel );
        addTitle( contributionPanel.getTitle() );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        // WARNING: don't change without also changing the old URL redirection
        mapper.addMap( "^contributions/view/([^/]+)$", ContributionPage.class, new String[]{"contribution"} );
    }

    public static RawLinkable getLinker( final int contributionId ) {
        // WARNING: don't change without also changing the old URL redirection
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "contributions/view/" + contributionId;
            }
        };
    }

    public static RawLinkable getLinker( Contribution contribution ) {
        return getLinker( contribution.getId() );
    }

}