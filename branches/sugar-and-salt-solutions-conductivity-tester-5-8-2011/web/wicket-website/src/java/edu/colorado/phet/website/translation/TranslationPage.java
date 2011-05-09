package edu.colorado.phet.website.translation;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.AuthenticatedPage;

public class TranslationPage extends AuthenticatedPage {
    public TranslationPage( PageParameters parameters ) {
        super( parameters );

        setTitle( getLocalizer().getString( "forTranslators.website.title", this ) );
    }
}
