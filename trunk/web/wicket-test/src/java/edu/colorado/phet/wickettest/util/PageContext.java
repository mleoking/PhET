package edu.colorado.phet.wickettest.util;

import java.util.Locale;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class PageContext {
    private Locale locale;
    private PhetPage page;

    public PageContext( PageContext old, Locale newLocale ) {
        this( newLocale, old.getPage() );
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
        return page;
    }

}
