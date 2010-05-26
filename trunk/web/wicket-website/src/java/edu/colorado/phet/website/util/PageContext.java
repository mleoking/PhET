package edu.colorado.phet.website.util;

import java.io.Serializable;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;

/**
 * Stores the locale, prefix and path of the main page loaded, and is available to all PhetPages and PhetPanels.
 * <p/>
 * For pages under a locale (for example, anything under "http://phet.colorado.edu/en/"), they are associated with a
 * specific locale (in this case "en"), and prefix ("/en/"). The prefix is separate from the locale so that both
 * pages and panels can be previewed with different translations (like locale = 'ar_SA', prefix='/translation/4/')
 * <p/>
 * The path of the request is also kept to inform the sub-panels of the origin.
 */
public class PageContext implements Serializable {
    private Locale locale;
    private String prefix;
    private String path;

    public PageContext( String prefix, String path, Locale locale ) {
        this.prefix = prefix;
        this.path = path;
        this.locale = locale;
    }

    /**
     * @return The default (English, main translation) context at a base path. This should only be used for constructing
     *         links where there is no context (like in RedirectionStrategy)
     */
    public static PageContext getNewDefaultContext() {
        return new PageContext( "/en/", "", PhetWicketApplication.getDefaultLocale() );
    }

    /**
     * Copy the context with a new locale. If the prefix is standard, the prefix will be replaced for the new locale.
     *
     * @param newLocale The new locale
     * @return Return a copy of this context with the new Locale.
     */
    public PageContext withNewLocale( Locale newLocale ) {
        if ( prefix.equals( getStandardPrefix( locale ) ) ) {
            return new PageContext( getStandardPrefix( newLocale ), path, newLocale );
        }
        else {
            return new PageContext( prefix, path, newLocale );
        }
    }

    /**
     * Get the standard prefix for a particular locale. This should be used for globally-visible translations, since
     * only one globally-visible translation exists for a particular locale.
     *
     * @param locale The Locale
     * @return The standard prefix
     */
    public static String getStandardPrefix( Locale locale ) {
        return "/" + LocaleUtils.localeToString( locale ) + "/";
    }

    public static String getTranslationPrefix( int translationId ) {
        return "/translation/" + String.valueOf( translationId ) + "/";
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

    /**
     * A quick check to see whether the panel is cacheable. Currently for performance reasons (and possibly other
     * assumptions) caching should be disabled for translation previews.
     *
     * @return Whether this context allows caching of panels
     */
    public boolean isCacheable() {
        if ( prefix.startsWith( "/translation/" ) ) {
            return false;
        }
        return true;
    }
}
