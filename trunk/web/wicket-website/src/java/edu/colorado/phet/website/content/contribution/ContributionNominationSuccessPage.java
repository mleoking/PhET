package edu.colorado.phet.website.content.contribution;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.Session;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.NotFoundPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.*;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Shown to the user after nominating a contribution
 */
public class ContributionNominationSuccessPage extends PhetMenuPage {

    private static final int REDIRECTION_DELAY_SECONDS = 3;

    private static final Logger logger = Logger.getLogger( ContributionNominationSuccessPage.class.getName() );

    private int contributionId;
    private Contribution contribution;

    public ContributionNominationSuccessPage( PageParameters parameters ) {
        super( parameters );

        contributionId = parameters.getInt( "contribution" );

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

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas" ) );

        add( new LocalizedText( "success", "contribution.nomination.success", new Object[]{
                HtmlUtils.encode( contribution.getTitle() )
        } ) );

        add( new LocalizedText( "nomination-redirection", "contribution.nomination.successRedirection", new Object[]{
                ContributionPage.getLinker( contributionId ).getHref( getPageContext(), getPhetCycle() ),
                REDIRECTION_DELAY_SECONDS
        } ) );

        Label redirector = new Label( "redirector", "" );
        redirector.add( new AttributeModifier( "content", true, new Model<String>( REDIRECTION_DELAY_SECONDS + ";url=" + ContributionPage.getLinker( contributionId ).getRawUrl( getPageContext(), getPhetCycle() ) ) ) );
        add( redirector );

        addTitle( new ResourceModel( "contribution.nomination.success" ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/nomination-success/([^/]+)$", ContributionNominationSuccessPage.class, new String[]{"contribution"} );
    }

    public static RawLinkable getLinker( final int contributionId ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "contributions/nomination-success/" + contributionId;
            }
        };
    }

    public static RawLinkable getLinker( Contribution contribution ) {
        return getLinker( contribution.getId() );
    }

}