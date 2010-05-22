package edu.colorado.phet.website.content.contribution;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.contribution.ContributionEditPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionCreatePage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( ContributionCreatePage.class.getName() );

    public ContributionCreatePage( PageParameters parameters ) {
        super( parameters );

        AuthenticatedPage.checkSignedIn();

        addTitle( new ResourceModel( "contribution.create.pageTitle" ) );

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas.submit" ) );

        add( new ContributionEditPanel( "contribution-edit-panel", getPageContext(), new Contribution() ) );

        add( new LocalizedText( "check-guidelines", "contribution.create.checkGuidelines", new Object[]{
                ContributionGuidelinesPanel.getLinker().getHref( getPageContext(), getPhetCycle() ),
                "href=\"/publications/activities-guide/contribution-guidelines.pdf\""
        } ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^for-teachers/submit-activity$", ContributionCreatePage.class );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "for-teachers/submit-activity";
            }
        };
    }

}
