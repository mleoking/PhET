package edu.colorado.phet.website.translation.entities;

public class ErrorEntity extends TranslationEntity {
    public ErrorEntity() {
        addString( "error.internalError" );
        addString( "error.internalError.message" );
        addString( "error.sessionExpired" );
        addString( "error.sessionExpired.message" );
        addString( "error.pageNotFound.message" );
    }

    public String getDisplayName() {
        return "Errors";
    }

}