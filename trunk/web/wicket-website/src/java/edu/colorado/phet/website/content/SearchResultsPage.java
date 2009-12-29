package edu.colorado.phet.website.content;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class SearchResultsPage extends PhetRegularPage {
    public SearchResultsPage( PageParameters parameters ) {
        super( parameters );

        String query = parameters.getString( "q" );

        initializeLocation( getNavMenu().getLocationByKey( "search.results" ) );

        add( new InvisibleComponent( "search-results-panel" ) );

        // TODO: localize
        addTitle( "Search: " + query );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        // TODO: fix subtle ugliness. is query parameter included in map => q, or does wicket auto-put-it into the parameters?
        mapper.addMap( "^search(\\?q=(.+))?$", SearchResultsPage.class, new String[]{null, "q"} );
    }

    public static RawLinkable getLinker( final String query ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                if ( query != null ) {
                    return "search?q=" + query;
                }
                else {
                    return "search";
                }
            }
        };
    }

}