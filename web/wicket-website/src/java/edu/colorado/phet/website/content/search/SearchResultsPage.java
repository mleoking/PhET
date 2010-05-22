package edu.colorado.phet.website.content.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.StringUtils;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class SearchResultsPage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( SearchResultsPage.class.getName() );

    public SearchResultsPage( PageParameters parameters ) {
        super( parameters );

        String query = parameters.getString( "q" );

        initializeLocation( getNavMenu().getLocationByKey( "search.results" ) );

        if ( query != null ) {
            addTitle( StringUtils.messageFormat( getPhetLocalizer().getString( "search.title", this ), query ) );
        }
        else {
            addTitle( StringUtils.messageFormat( getPhetLocalizer().getString( "search.title", this ), "-" ) );
        }

        add( new SearchResultsPanel( "search-results-panel", getPageContext(), query ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        // TODO: fix subtle ugliness. is query parameter included in map => q, or does wicket auto-put-it into the parameters?
        mapper.addMap( "^search(\\?q=(.+))?$", SearchResultsPage.class, new String[]{null, "q"} );
    }

    public static RawLinkable getLinker( final String query ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                if ( query != null ) {
                    try {
                        return "search?q=" + URLEncoder.encode( query, "UTF-8" );
                    }
                    catch( UnsupportedEncodingException e ) {
                        e.printStackTrace();
                        logger.error( e );
                        return "";
                    }
                }
                else {
                    return "search";
                }
            }
        };
    }

}