package edu.colorado.phet.website.content.contribution;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.contribution.ContributionEditPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionEditPage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( ContributionEditPage.class.getName() );

    private Contribution contribution;

    public ContributionEditPage( PageParameters parameters ) {
        super( parameters );

        AuthenticatedPage.checkSignedIn();

        String contributionIdString = parameters.getString( "contributionId" );
        final int contributionId = Integer.parseInt( contributionIdString );

        addTitle( new ResourceModel( "contribution.edit.pageTitle" ) );

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas.edit" ) );

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                contribution = (Contribution) session.load( Contribution.class, contributionId );
                return true;
            }
        } );

        add( new ContributionEditPanel( "contribution-edit-panel", getPageContext(), contribution ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/edit/([^/]+)$", ContributionEditPage.class, new String[]{"contributionId"} );
    }

    public static RawLinkable getLinker( final int contributionId ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "contributions/edit/" + String.valueOf( contributionId );
            }
        };
    }

    public static RawLinkable getLinker( final Contribution contribution ) {
        return getLinker( contribution.getId() );
    }

}