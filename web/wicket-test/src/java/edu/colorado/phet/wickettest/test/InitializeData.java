package edu.colorado.phet.wickettest.test;

public class InitializeData {

    // DROP TABLE category, category_mapping, keyword, keyword_mapping, localized_simulation, phet_user, project, simulation, translated_string, translation, user_translation_mapping;

    public static void main( String[] args ) {
        InitializeSimulations.main( args );
        InitializeCategories.main( args );
        InitializeUsers.main( args );
        InitializeTranslations.main( args );
    }
}
