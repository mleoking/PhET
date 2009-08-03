package edu.colorado.phet.wickettest.util;

import java.io.Serializable;
import java.util.Locale;

public class PageContext implements Serializable {
    private Locale locale;
    private transient PhetPage page;
    private String prefix;

    public PageContext( String prefix, Locale locale, PhetPage page ) {
        this.prefix = prefix;
        this.locale = locale;
        this.page = page;
    }

    public PageContext withNewLocale( Locale newLocale ) {
        return new PageContext( prefix, newLocale, page );
    }

    public String getPrefix() {
        return prefix;
    }

    public Locale getLocale() {
        return locale;
    }

    public PhetPage getPage() {
        throw new RuntimeException( "temporarily disabled" );
        //return page;
    }

}
