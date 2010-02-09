package edu.colorado.phet.website.content;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.hibernate.Session;

import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.ContributionMainPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.*;
import edu.colorado.phet.website.util.links.RawLinkable;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.DistributionHandler;

/**
 * Holder page for showing a single contribution
 */
public class ContributionPage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( ContributionPage.class.getName() );

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
        mapper.addMap( "^contributions/view/([^/]+)$", ContributionPage.class, new String[]{"contribution"} );
    }

    public static PhetLink createLink( String id, PageContext context, Contribution contribution ) {
        return createLink( id, context, contribution.getId() );
    }

    public static PhetLink createLink( String id, PageContext context, int contributionId ) {
        return new PhetLink( id, context.getPrefix() + "contributions/view/" + contributionId );
    }

}