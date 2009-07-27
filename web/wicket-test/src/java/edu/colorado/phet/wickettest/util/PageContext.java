package edu.colorado.phet.wickettest.util;

import java.util.Locale;

import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WicketApplication;

public class PageContext {
    private Locale locale;
    private Session session;
    private WicketApplication application;
    private PhetPage page;

    public PageContext( PageContext old, Locale newLocale ) {
        this( newLocale, old.getSession(), old.getApplication(), old.getPage() );
    }

    public PageContext( Locale locale, Session session, WicketApplication application, PhetPage page ) {
        this.locale = locale;
        this.session = session;
        this.application = application;
        this.page = page;
    }

    public String getPrefix() {
        return "/" + LocaleUtils.localeToString( getLocale() ) + "/";
    }

    public Locale getLocale() {
        return locale;
    }

    public Session getSession() {
        return session;
    }

    public WicketApplication getApplication() {
        return application;
    }

    public PhetPage getPage() {
        return page;
    }

}
