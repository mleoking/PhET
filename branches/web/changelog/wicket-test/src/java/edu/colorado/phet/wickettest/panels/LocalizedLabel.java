package edu.colorado.phet.wickettest.panels;

import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class LocalizedLabel extends Label {
    private Locale locale = null;

    public LocalizedLabel( String id ) {
        super( id );
    }

    public LocalizedLabel( String id, String label ) {
        super( id, label );
    }

    public LocalizedLabel( String id, IModel model ) {
        super( id, model );
    }

    public LocalizedLabel( String id, Locale locale, String label ) {
        super( id, label );
        this.locale = locale;
    }

    public LocalizedLabel( String id, Locale locale, IModel model ) {
        super( id, model );
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        if ( locale == null ) {
            return super.getLocale();
        }
        return locale;
    }

    public void setLocale( Locale locale ) {
        this.locale = locale;
    }

    @Override
    public String getVariation() {
        return null;
    }
}
