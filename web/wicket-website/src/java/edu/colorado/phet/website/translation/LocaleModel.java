package edu.colorado.phet.website.translation;

import java.util.Locale;

import org.apache.wicket.model.IModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

class LocaleModel implements IModel {
    private Locale locale;
    private String name;

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
        else {
            throw new RuntimeException( "Bad LocaleModel!" );
        }
    }

    public void detach() {

    }

    @Override
    public String toString() {
        return name + "  ( " + LocaleUtils.localeToString( locale ) + " )";
    }
}
