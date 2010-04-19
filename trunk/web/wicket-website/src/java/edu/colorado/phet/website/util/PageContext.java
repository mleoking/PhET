package edu.colorado.phet.website.util;

import java.io.Serializable;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;

public class PageContext implements Serializable {
    private Locale locale;
    private String prefix;
    private String path;

    public PageContext( String prefix, String path, Locale locale ) {
        this.prefix = prefix;
        this.path = path;
        this.locale = locale;
    }

    public static PageContext getNewDefaultContext() {
        return new PageContext( "/en/", "", PhetWicketApplication.getDefaultLocale() );
    }

    public PageContext withNewLocale( Locale newLocale ) {
        if ( prefix.equals( getStandardPrefix() ) ) {
            return new PageContext( getStandardPrefix( newLocale ), path, newLocale );
        }
        else {
            return new PageContext( prefix, path, newLocale );
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

    public String getPath() {
        return path;
    }

    public boolean isCacheable() {
        if ( prefix.startsWith( "/translation/" ) ) {
            return false;
        }
        return true;
    }
}
