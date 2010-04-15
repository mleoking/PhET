package edu.colorado.phet.website.translation;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.authentication.AuthenticatedPage;

public class TranslationPage extends AuthenticatedPage {
    public TranslationPage( PageParameters parameters ) {
        super( parameters );

        addTitle( new ResourceModel( "forTranslators.website.title" ) );
    }
}
