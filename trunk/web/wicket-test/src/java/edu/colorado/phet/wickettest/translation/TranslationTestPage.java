package edu.colorado.phet.wickettest.translation;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class TranslationTestPage extends TranslationPage {
    public TranslationTestPage( PageParameters parameters ) {
        super( parameters, true );

        addTitle( "Translation test page" );
    }

    public String translateString( Component component, Locale locale, String key ) {
        if ( locale == null && key.equals( "language.dir" ) ) {
            return "ltr";
        }
        return LocaleUtils.localeToString( locale ) + " : " + key;
    }
}
