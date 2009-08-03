package edu.colorado.phet.wickettest.util;

import java.io.Serializable;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class PageContext implements Serializable {
    private Locale locale;
    private transient PhetPage page;
    private String prefix;
    private String path;

    public PageContext( String prefix, String path, Locale locale, PhetPage page ) {
        this.prefix = prefix;
        this.path = path;
        this.locale = locale;
        this.page = page;
    }

    public PageContext withNewLocale( Locale newLocale ) {
        if ( prefix.equals( getStandardPrefix() ) ) {
            return new PageContext( getStandardPrefix( newLocale ), path, newLocale, page );
        }
        else {
            return new PageContext( prefix, path, newLocale, page );
        }
    }

    public String getStandardPrefix() {
        return getStandardPrefix( locale );
    }

    public String getStandardPrefix( Locale lo ) {
        return "/" + LocaleUtils.localeToString( lo ) + "/";
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

    public String getPath() {
        return path;
    }
}
