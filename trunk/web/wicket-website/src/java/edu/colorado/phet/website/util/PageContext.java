package edu.colorado.phet.website.util;

import java.io.Serializable;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class PageContext implements Serializable {
    private Locale locale;
    private String prefix;
    private String path;
    private PhetRequestCycle cycle;

    public PageContext( String prefix, String path, Locale locale, PhetRequestCycle cycle ) {
        this.prefix = prefix;
        this.path = path;
        this.locale = locale;
        this.cycle = cycle;
    }

    public PageContext withNewLocale( Locale newLocale ) {
        if ( prefix.equals( getStandardPrefix() ) ) {
            return new PageContext( getStandardPrefix( newLocale ), path, newLocale, cycle );
        }
        else {
            return new PageContext( prefix, path, newLocale, cycle );
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

    public PhetRequestCycle getCycle() {
        return cycle;
    }
}
