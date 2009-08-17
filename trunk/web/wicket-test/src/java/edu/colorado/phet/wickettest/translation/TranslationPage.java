package edu.colorado.phet.wickettest.translation;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.authentication.AuthenticatedPage;

public class TranslationPage extends AuthenticatedPage {
    public TranslationPage( PageParameters parameters ) {
        super( parameters );

        addTitle( "PhET Basic Translations" );
    }
}
