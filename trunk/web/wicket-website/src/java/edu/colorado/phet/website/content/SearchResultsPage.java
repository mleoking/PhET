package edu.colorado.phet.website.content;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.panels.SimulationDisplayPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.test.LuceneTest;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class SearchResultsPage extends PhetRegularPage {
    public SearchResultsPage( PageParameters parameters ) {
        super( parameters );

        String query = parameters.getString( "q" );

        initializeLocation( getNavMenu().getLocationByKey( "search.results" ) );

        List<LocalizedSimulation> lsims = new LinkedList<LocalizedSimulation>();

        PhetLocalizer localizer = (PhetLocalizer) getLocalizer();
        // TODO: display query

        if ( query != null ) {
            lsims = LuceneTest.testSearch( getHibernateSession(), query, getPageContext().getLocale() );
        }

        add( new SimulationDisplayPanel( "search-results-panel", getPageContext(), lsims ) );


        if ( query != null ) {
            addTitle( MessageFormat.format( localizer.getString( "search.title", this ), query ) );
            add( new LocalizedText( "search-query", "search.query", new Object[]{query} ) );
        }
        else {
            addTitle( MessageFormat.format( localizer.getString( "search.title", this ), "-" ) );
            add( new InvisibleComponent( "search-query" ) );
        }

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