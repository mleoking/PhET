package edu.colorado.phet.wickettest.util;

import java.io.Serializable;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class PageContext implements Serializable {
    private Locale locale;
    private transient PhetPage page;

    public PageContext( PageContext old, Locale newLocale ) {
        this( newLocale, old.page );
    }

    public PageContext( Locale locale, PhetPage page ) {
        this.locale = locale;
        this.page = page;
    }

    public String getPrefix() {
        return "/" + LocaleUtils.localeToString( getLocale() ) + "/";
    }

    public Locale getLocale() {
        return locale;
    }

    public PhetPage getPage() {
        throw new RuntimeException( "temporarily disabled" );
        //return page;
    }

}
