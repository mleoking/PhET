package edu.colorado.phet.website.translation.entities;

public class CommonEntity extends TranslationEntity {
    public CommonEntity() {
        addString( "language.dir", "This should be either 'ltr' for left-to-right languages, or 'rtl' for right-to-left languages (both without the quotes)." );
        addString( "language.name", "This will be the language name displayed to the user (translated into the native language)." );
        addString( "footer.someRightsReserved" );
        addString( "error.pageNotFound" );
    }

    public String getDisplayName() {
        return "Common";
    }
}
