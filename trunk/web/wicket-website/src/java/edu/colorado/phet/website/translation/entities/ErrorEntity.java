package edu.colorado.phet.website.translation.entities;

public class ErrorEntity extends TranslationEntity {
    public ErrorEntity() {
        addString( "error.internalError" );
    }

    public String getDisplayName() {
        return "Errors";
    }

}