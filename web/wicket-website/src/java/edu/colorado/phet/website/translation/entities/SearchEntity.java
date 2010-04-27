package edu.colorado.phet.website.translation.entities;

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
    }

    public String getDisplayName() {
        return "Search";
    }
}