package edu.colorado.phet.website.translation;

import java.util.Locale;

import org.apache.wicket.model.IModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;

/**
 * Wicket model of a locale that makes it easy to use within locale drop-down boxes
 */
class LocaleModel implements IModel {
    private Locale locale;
    private String name;

    /**
     * @param locale The locale
     * @param name   The name of the locale translated into the desired language
     */
    LocaleModel( Locale locale, String name ) {
        this.locale = locale;
        this.name = name;
    }

    public Object getObject() {
        return locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setObject( Object object ) {
        if ( object instanceof Locale ) {
            locale = (Locale) object;
        }
        else if ( object instanceof LocaleModel ) {
            locale = (Locale) ( (LocaleModel) object ).getObject();
        }
        else if ( object == null ) {
            locale = PhetWicketApplication.getDefaultLocale();
        }
        else {
            throw new RuntimeException( "Bad LocaleModel! : " + object.getClass().getCanonicalName() );
        }
    }

    public void detach() {

    }

    @Override
    public String toString() {
        return name + "  ( " + LocaleUtils.localeToString( locale ) + " )";
    }
}
