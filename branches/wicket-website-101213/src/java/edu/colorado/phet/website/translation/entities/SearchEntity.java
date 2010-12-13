package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.search.SearchResultsPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class SearchEntity extends TranslationEntity {
    public SearchEntity() {
        addString( "nav.search.results" );
        addString( "search.title" );
        addString( "search.query" );
        addString( "search.search" );
        addString( "search.go" );
        addString( "search.enterText" );
        addString( "search.clickSearch" );
        addString( "search.simulations" );
        addString( "search.activities" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new SearchResultsPanel( id, context, "electricity" );
            }
        }, "Search Results" );
    }

    public String getDisplayName() {
        return "Search";
    }
}