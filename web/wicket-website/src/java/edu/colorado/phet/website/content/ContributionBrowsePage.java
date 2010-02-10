package edu.colorado.phet.website.content;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.ContributionBrowsePanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionBrowsePage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( ContributionBrowsePage.class.getName() );

    public ContributionBrowsePage( PageParameters parameters ) {
        super( parameters );

//        if ( category == null && parameters.containsKey( "categories" ) ) {
//            // didn't find the category
//            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
//        }

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas.browse" ) );

        // TODO: localize
        addTitle( "" );

        add( new ContributionBrowsePanel( "contribution-browse-panel", getPageContext(), new LinkedList<Contribution>() ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/browse$", ContributionBrowsePage.class );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "contributions/browse";
            }
        };
    }

}