package edu.colorado.phet.wickettest.translation;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.util.PhetPage;

public abstract class TranslationPage extends PhetPage {
    protected TranslationPage( PageParameters parameters ) {
        super( parameters, false );
    }

    public TranslationPage( PageParameters parameters, boolean markup ) {
        super( parameters, markup );
    }

    public abstract String translateString( Component component, Locale locale, String key );
}
