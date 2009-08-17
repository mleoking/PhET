package edu.colorado.phet.wickettest.translation;

import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.model.IModel;
import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class PhetLocalizer extends Localizer {

    private static final String UNTRANSLATED_VALUE = "<!-- untranslated value -->";

    public PhetLocalizer() {
    }

    @Override
    public String getString( String key, Component component ) throws MissingResourceException {
        return getString( key, component, null, null );
    }

    @Override
    public String getString( String key, Component component, IModel model ) throws MissingResourceException {
        return getString( key, component, model, null );
    }

    @Override
    public String getString( String key, Component component, String defaultValue ) throws MissingResourceException {
        return getString( key, component, null, defaultValue );
    }

    @Override
    public String getString( String key, Component component, IModel model, Locale locale, String style, String defaultValue ) throws MissingResourceException {
        return getString( key, component, model, defaultValue );
    }

    protected String mapCacheKey( String key, Locale locale ) {
        return LocaleUtils.localeToString( locale ) + "::" + key;
    }

    protected String mapCacheKey( String key, int translationId ) {
        return String.valueOf( translationId ) + ":" + key;
    }

    /**
     * Whenever a string is modified or added during runtime, this function should be called
     *
     * @param translation The Translation
     * @param key         The localization key
     * @param value       The translated string value
     */
    public void updateCachedString( Translation translation, String key, String value ) {
        putIntoCache( mapCacheKey( key, translation.getId() ), value );
        if ( translation.isVisible() ) {
            // TODO: assure somewhere that multiple visible translations don't have the same locale?
            putIntoCache( mapCacheKey( key, translation.getLocale() ), value );
        }
    }

    @Override
    public String getString( String key, Component component, IModel model, String defaultValue ) throws MissingResourceException {
        //System.out.println( "******************************\nBeginning string lookup: " + key );
        String lookup = null;
        Integer translationId = null;
        if ( component.getVariation() != null ) {
            translationId = Integer.valueOf( component.getVariation() );
        }
        Locale locale = component.getLocale();

        // whether we should look up the string by the translation id, or by the locale
        boolean lookupById = translationId != null;

        String mainCacheKey;

        if ( lookupById ) {
            //System.out.println( "Looking up by translation ID: " + String.valueOf( translationId ) );
            mainCacheKey = mapCacheKey( key, translationId );
            lookup = getFromCache( mainCacheKey );
        }
        else {
            //System.out.println( "Looking up by locale: " + LocaleUtils.localeToString( locale ) );
            mainCacheKey = mapCacheKey( key, locale );
            lookup = getFromCache( mainCacheKey );
        }

        //System.out.println( "First cache lookup: " + lookup );

        // if the cache lookup says the value is not translated, we don't want to look it up in the database
        boolean untranslated = false;

        if ( lookup != null ) {
            untranslated = lookup.equals( UNTRANSLATED_VALUE );
            if ( !untranslated ) {
                // lookup in cache succeeded. return the string
                return lookup;
            }
        }

        Session session = ( (PhetRequestCycle) component.getRequestCycle() ).getHibernateSession();

        // if the value isn't in the cache for the main lookup, request it from the DB
        if ( !untranslated ) {
            //System.out.println( "Looking up main string" );
            // perform the lookup
            if ( translationId != null ) {
                lookup = HibernateUtils.getString( session, key, translationId );
            }
            else {
                lookup = HibernateUtils.getString( session, key, locale );
            }

            //System.out.println( "First DB lookup: " + lookup );

            // if the lookup is found, put it in the cache and return it
            if ( lookup != null ) {
                putIntoCache( mainCacheKey, lookup );
                return lookup;
            }

            // if it isn't found, then it isn't translated. record this information in the cache so future lookups don't
            // hammer away at the database
            putIntoCache( mainCacheKey, UNTRANSLATED_VALUE );
        }

        // at this point, we know it is not in the first translation we looked at

        Locale defaultLocale = WicketApplication.getDefaultLocale();
        String defaultCacheKey = mapCacheKey( key, defaultLocale );

        //System.out.println( "Looking up for default cache" );

        // perform a cache lookup for the default value
        lookup = getFromCache( defaultCacheKey );

        //System.out.println( "Second cache lookup: " + lookup );

        untranslated = false;

        if ( lookup != null ) {
            untranslated = lookup.equals( UNTRANSLATED_VALUE );
            if ( !untranslated ) {
                // lookup of the default value succeeded, return the string
                return lookup;
            }
        }

        if ( untranslated ) {
            if ( defaultValue != null ) {
                return defaultValue;
            }

            // we can find NO translation for this string?!? throw an error
            throw new RuntimeException( "Could not find any translation for the key " + key );
        }

        //System.out.println( "Looking up in default DB" );

        // perform a "default" lookup, which usually should give the English translation, if it exists
        lookup = HibernateUtils.getString( session, key );

        //System.out.println( "Default DB lookup: " + lookup );

        if ( lookup != null ) {
            putIntoCache( defaultCacheKey, lookup );
            return lookup;
        }
        else {
            putIntoCache( defaultCacheKey, UNTRANSLATED_VALUE );

            if ( defaultValue != null ) {
                return defaultValue;
            }

            throw new RuntimeException( "Could not find any translation for the key " + key );
        }
    }
}
