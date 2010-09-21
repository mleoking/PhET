package edu.colorado.phet.website.translation;

import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;
import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.panels.ChangePasswordPanel;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.panels.contribution.ContributionEditPanel;
import edu.colorado.phet.website.templates.Stylable;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Wrapper over the wicket Localizer so we can properly look up translated strings based on either locale or a specific
 * translation ID. Additionally correctly handles caching behavior, and seamlessly returning English strings if a
 * string isn't translated into the desired language.
 */
public class PhetLocalizer extends Localizer {

    private static final String UNTRANSLATED_VALUE = "<!-- untranslated value -->";

    private static final Logger logger = Logger.getLogger( PhetLocalizer.class.getName() );

    private static PhetLocalizer instance = null;

    public static synchronized PhetLocalizer get() {
        if ( instance == null ) {
            instance = new PhetLocalizer();
        }
        return instance;
    }

    private PhetLocalizer() {
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

    /**
     * Get the best translated version of a string for a particular locale. Should be in a session, but not within a
     * transaction. If not translatable, an error string is returned.
     *
     * @param session Hibernate session
     * @param key     Translation key
     * @param locale  Locale
     * @return Translated string
     */
    public String getBestString( Session session, String key, Locale locale ) {
        String ret = getLocaleString( session, key, locale, false );
        if ( ret == null ) {
            ret = getDefaultString( session, key, null, false );
        }
        return ret;
    }

    /**
     * Get the best translated version of a string for a particular locale. Should be in a session, but not within a
     * transaction. If not translatable, an error string is returned.
     *
     * @param session       Hibernate session
     * @param key           Translation key
     * @param translationId Locale
     * @return Translated string
     */
    public String getBestString( Session session, String key, int translationId ) {
        String ret = getTranslationIdString( session, key, translationId, false );
        if ( ret == null ) {
            ret = getDefaultString( session, key, null, false );
        }
        return ret;
    }

    /**
     * Get the best translated version of a string for a particular locale. Should be within a transaction. If not
     * translatable, an error string is returned.
     *
     * @param session Hibernate session
     * @param key     Translation key
     * @param locale  Locale
     * @return Translated string
     */
    public String getBestStringWithinTransaction( Session session, String key, Locale locale ) {
        String ret = getLocaleString( session, key, locale, true );
        if ( ret == null ) {
            ret = getDefaultString( session, key, null, true );
        }
        return ret;
    }

    /**
     * Get the best translated version of a string for a particular locale. Should be within a transaction. If not
     * translatable, an error string is returned.
     *
     * @param session       Hibernate session
     * @param key           Translation key
     * @param translationId Locale
     * @return Translated string
     */
    public String getBestStringWithinTransaction( Session session, String key, int translationId ) {
        String ret = getTranslationIdString( session, key, translationId, true );
        if ( ret == null ) {
            ret = getDefaultString( session, key, null, true );
        }
        return ret;
    }

    /**
     * Returns the translated string from the default translation (probably English). If the string is not translated
     * and the defaultValue is non-null, the defaultValue is returned. Otherwise an error string is returned that
     * contains the key value. (null is NOT returned)
     * <p/>
     * NOTE: assumes that we are NOT currently in a hibernate transaction
     *
     * @param session       Hibernate session (if set to null, will be recovered from the request cycle)
     * @param key           Translation string key
     * @param defaultValue  Default value (set to null if not desired)
     * @param inTransaction Whether or not Hibernate is inside of a transaction
     * @return Translated string
     */
    public String getDefaultString( Session session, String key, String defaultValue, boolean inTransaction ) {
        String defaultCacheKey = mapCacheKey( key, PhetWicketApplication.getDefaultLocale() );

        String lookup = getFromCache( defaultCacheKey );

        if ( lookup != null ) {
            // either hit or miss recorded in cache. handle both possibilities
            if ( !lookup.equals( UNTRANSLATED_VALUE ) ) {
                // lookup of the default value succeeded, return the string
                return lookup;
            }
            else {
                if ( defaultValue != null ) {
                    return defaultValue;
                }

                logger.warn( "unable to find default translation for " + key );
                return getErrorString( key );
            }
        }

        if ( session == null ) {
            session = PhetRequestCycle.get().getHibernateSession();
        }

        // perform a "default" lookup, which usually should give the English translation, if it exists
        if ( inTransaction ) {
            lookup = StringUtils.getStringDirectWithinTransaction( session, key, PhetWicketApplication.getDefaultLocale() );
        }
        else {
            lookup = StringUtils.getDefaultStringDirect( session, key );
        }

        if ( lookup != null ) {
            putIntoCache( defaultCacheKey, lookup );
            return lookup;
        }
        else {
            putIntoCache( defaultCacheKey, UNTRANSLATED_VALUE );

            if ( defaultValue != null ) {
                return defaultValue;
            }

            logger.warn( "unable to find default translation for " + key );
            return getErrorString( key );
        }
    }

    /*---------------------------------------------------------------------------*
    * Implementation handling caching
    *----------------------------------------------------------------------------*/

    public String getString( String key, Component component, IModel model, String defaultValue, boolean checkDefault ) throws MissingResourceException {
        if ( key.startsWith( "style." ) ) {
            Stylable cmp = closestStylableComponent( component );
            if ( cmp != null ) {
                return cmp.getStyle( key );
            }
            logger.warn( "missed style: " + key + " with component of " + component, new RuntimeException() );
        }
        key = mapStrings( key, component, model, defaultValue, checkDefault );

        String lookup = null;
        Integer translationId = null;
        if ( component.getVariation() != null ) {
            translationId = Integer.valueOf( component.getVariation() );
        }
        Locale locale = component.getLocale();

        // whether we should look up the string by the translation id, or by the locale
        boolean lookupById = translationId != null;

        if ( lookupById ) {
            lookup = getTranslationIdString( null, key, translationId, false );
        }
        else {
            lookup = getLocaleString( null, key, locale, false );
        }

        if ( lookup != null ) {
            // our string was found, so return it.
            return lookup;
        }

        // at this point, we know it is not in the first translation we looked at

        if ( !checkDefault && defaultValue != null ) {
            // return either null or the default value, since we won't check the default language
            logger.info( "Shortcut default value on " + key + ": " + defaultValue );
            return defaultValue;
        }

        return getDefaultString( null, key, defaultValue, false );
    }

    /**
     * Map various automatically-created keys (like validation keys) to better named keys
     *
     * @param key          The translation key
     * @param component    The component to translate this key
     * @param model        The model (if applicable)
     * @param defaultValue The default value (if applicable)
     * @param checkDefault Whether to check defaults
     * @return The new string key
     */
    private String mapStrings( String key, Component component, IModel model, String defaultValue, boolean checkDefault ) {
        //TODO: move the logic into the respective panels.
        if ( key.endsWith( "Required" ) && component.findParent( ContributionEditPanel.class )!=null ) {
            if ( key.equals( "authors.Required" ) ) {
                return "contribution.edit.authors.Required";
            }
            else if ( key.equals( "authorOrganization.Required" ) ) {
                return "contribution.edit.organization.Required";
            }
            else if ( key.equals( "contactEmail.Required" ) ) {
                return "contribution.edit.email.Required";
            }
            else if ( key.equals( "title.Required" ) ) {
                return "contribution.edit.title.Required";
            }
            else if ( key.equals( "keywords.Required" ) ) {
                return "contribution.edit.keywords.Required";
            }
        }else if (key.endsWith( "Required" ) &&component.findParent( ChangePasswordPanel.class )!=null){
            return "changePassword.validation.newPasswordBlank";
        }
        return key;
    }

    private Stylable closestStylableComponent( Component component ) {
        if ( component instanceof Stylable ) {
            return (Stylable) component;
        }
        else {
            MarkupContainer container = component.getParent();
            if ( container == null ) {
                return null;
            }
            else {
                return closestStylableComponent( container );
            }
        }
    }

    /**
     * Checks a specific translation for a translated string. If the string is translated, it is returned, otherwise
     * null is returned
     *
     * @param session       Hibernate session (if null, will be taken from the request cycle)
     * @param key           Translation key
     * @param translationId ID for the translation
     * @param inTransaction Whether or not Hibernate is inside of a transaction
     * @return Translated String (or null)
     */
    private String getTranslationIdString( Session session, String key, int translationId, boolean inTransaction ) {
        String mainCacheKey = mapCacheKey( key, translationId );
        String lookup = getFromCache( mainCacheKey );

        if ( lookup != null ) {
            if ( !lookup.equals( UNTRANSLATED_VALUE ) ) {
                return lookup;
            }
            else {
                // untranslated for this translation, return null
                return null;
            }
        }

        if ( session == null ) {
            session = PhetRequestCycle.get().getHibernateSession();
        }

        // perform the lookup
        if ( inTransaction ) {
            lookup = StringUtils.getStringDirectWithinTransaction( session, key, translationId );
        }
        else {
            lookup = StringUtils.getStringDirect( session, key, translationId );
        }

        // if the lookup is found, put it in the cache and return it
        if ( lookup != null ) {
            putIntoCache( mainCacheKey, lookup );
            return lookup;
        }

        // if it isn't found, then it isn't translated. record this information in the cache so future lookups don't
        // hammer away at the database
        putIntoCache( mainCacheKey, UNTRANSLATED_VALUE );

        // untranslated for this translation, return null
        return null;
    }

    /**
     * Checks the translation of a particular locale for a translated string. If the string is translated, it is
     * returned, otherwise null is returned
     *
     * @param session       Hibernate session (if null, will be taken from the request cycle)
     * @param key           Translation key
     * @param locale        Locale for the translation
     * @param inTransaction Whether or not Hibernate is inside of a transaction
     * @return Translated String (or null)
     */
    private String getLocaleString( Session session, String key, Locale locale, boolean inTransaction ) {
        String mainCacheKey = mapCacheKey( key, locale );
        String lookup = getFromCache( mainCacheKey );

        if ( lookup != null ) {
            if ( !lookup.equals( UNTRANSLATED_VALUE ) ) {
                return lookup;
            }
            else {
                // untranslated for this translation, return null
                return null;
            }
        }

        if ( session == null ) {
            session = PhetRequestCycle.get().getHibernateSession();
        }

        // perform the lookup
        if ( inTransaction ) {
            lookup = StringUtils.getStringDirectWithinTransaction( session, key, locale );
        }
        else {
            lookup = StringUtils.getStringDirect( session, key, locale );
        }

        // if the lookup is found, put it in the cache and return it
        if ( lookup != null ) {
            putIntoCache( mainCacheKey, lookup );
            return lookup;
        }

        // if it isn't found, then it isn't translated. record this information in the cache so future lookups don't
        // hammer away at the database
        putIntoCache( mainCacheKey, UNTRANSLATED_VALUE );

        // untranslated for this translation, return null
        return null;
    }

    private String getErrorString( String key ) {
        if ( key.startsWith( "language.names." ) ) {
            Locale locale = LocaleUtils.stringToLocale( key.substring( "language.names.".length() ) );
            if ( locale != null ) {
                String name = PhetWicketApplication.get().getSupportedLocales().getName( locale );
                if ( name == null ) {
                    name = locale.getDisplayName();
                }
                return name;
            }
        }
        return "***" + key + "***";
    }

    protected String mapCacheKey( String key, Locale locale ) {
        return LocaleUtils.localeToString( locale ) + "::" + key;
    }

    protected String mapCacheKey( String key, int translationId ) {
        return String.valueOf( translationId ) + ":" + key;
    }

    /*---------------------------------------------------------------------------*
    * Wicket Localizer Methods
    *----------------------------------------------------------------------------*/

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

    @Override
    public String getString( String key, Component component, IModel model, String defaultValue ) throws MissingResourceException {
        return getString( key, component, model, defaultValue, true );
    }

}
