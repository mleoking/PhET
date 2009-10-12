package edu.colorado.phet.website.util;

import java.util.Date;
import java.util.Locale;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.LocalizedLabel;
import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.translation.PhetLocalizer;

/**
 * Includes static functions (with their own transaction handling) that handle setting and getting of localization strings
 */
public class StringUtils {

    /**
     * Returns a default (English) string from the database
     *
     * @param session Hibernate session (already open)
     * @param key     Localization key
     * @return Translated String (probably not translated though!)
     */
    public static String getString( Session session, String key ) {
        return getString( session, key, PhetWicketApplication.getDefaultLocale() );
    }

    /**
     * Returns a string from the database for a visible translation (specified by a locale)
     *
     * @param session Hibernate session (already open)
     * @param key     Localization key
     * @param locale  Locale of the string
     * @return Translated String
     */
    public static String getString( Session session, String key, Locale locale ) {
        Transaction tx = null;
        String ret = null;
        try {
            tx = session.beginTransaction();

            Query query = session.createQuery( "select ts from TranslatedString as ts, Translation as t where (ts.translation = t and t.visible = true and t.locale = :locale and ts.key = :key)" );
            query.setLocale( "locale", locale );
            query.setString( "key", key );

            TranslatedString translatedString = (TranslatedString) query.uniqueResult();

            if ( translatedString != null ) {
                ret = translatedString.getValue();
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "WARNING: exception:\n" + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }
        return ret;

    }

    /**
     * Returns a string from the database for any translation (by id)
     *
     * @param session       Hibernate Session (already open)
     * @param key           Localization key
     * @param translationId Translation ID
     * @return Translated String
     */
    public static String getString( Session session, String key, int translationId ) {
        Transaction tx = null;
        String ret = null;
        try {
            tx = session.beginTransaction();

            Query query = session.createQuery( "select ts from TranslatedString as ts, Translation as t where (ts.translation = t and t.id = :id and ts.key = :key)" );
            query.setInteger( "id", translationId );
            query.setString( "key", key );

            TranslatedString translatedString = (TranslatedString) query.uniqueResult();

            if ( translatedString != null ) {
                ret = translatedString.getValue();
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "WARNING: exception:\n" + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }
        return ret;

    }

    public static final int STRING_TRANSLATED = 0;
    public static final int STRING_UNTRANSLATED = 1;
    public static final int STRING_OUT_OF_DATE = 2;

    public static int stringStatus( Session session, final String key, final int translationId ) {
        StatusTask task = new StatusTask( translationId, key );
        HibernateUtils.wrapTransaction( session, task );
        return task.status;
    }

    private static class StatusTask implements HibernateTask {
        public int status;
        private final int translationId;
        private final String key;

        public StatusTask( int translationId, String key ) {
            this.translationId = translationId;
            this.key = key;
            status = STRING_UNTRANSLATED;
        }

        public boolean run( Session session ) {
            // should hit an error and return false if it doesn't exist
            TranslatedString string = (TranslatedString) session.createQuery(
                    "select ts from TranslatedString as ts where ts.translation.id = :id and ts.key = :key" )
                    .setInteger( "id", translationId ).setString( "key", key ).uniqueResult();
            if ( string != null ) {
                status = STRING_TRANSLATED;
            }
            else {
                return true;
            }
            TranslatedString englishString = (TranslatedString) session.createQuery(
                    "select ts from TranslatedString as ts where ts.translation.visible = true and ts.translation.locale = :locale and ts.key = :key" )
                    .setLocale( "locale", PhetWicketApplication.getDefaultLocale() ).setString( "key", key ).uniqueResult();
            if ( string.getUpdatedAt().compareTo( englishString.getUpdatedAt() ) < 0 ) {
                status = STRING_OUT_OF_DATE;
            }
            return true;
        }
    }

    public static boolean isStringSet( Session session, final String key, final int translationId ) {
        return HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                // should hit an error and return false if it doesn't exist
                TranslatedString string = (TranslatedString) session.createQuery(
                        "select ts from TranslatedString as ts where ts.translation.id = :id and ts.key = :key" )
                        .setInteger( "id", translationId ).setString( "key", key ).uniqueResult();
                return string != null;
            }
        } );
    }

    public static boolean setEnglishString( Session session, String key, String value ) {
        return setString( session, key, value, PhetWicketApplication.getDefaultLocale() );
    }

    public static boolean setString( Session session, String key, String value, Locale locale ) {
        // TODO: read in lines in different ways OR test in Mac?
        value = value.replaceAll( "\r", "" );
        value = value.replaceAll( "\n", "<br/>" );

        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Translation translation = (Translation) session.createQuery( "select t from Translation as t where t.visible = true and t.locale = :locale" ).setLocale( "locale", locale ).uniqueResult();
            TranslatedString tString = null;
            for ( Object o : translation.getTranslatedStrings() ) {
                TranslatedString ts = (TranslatedString) o;
                if ( ts.getKey().equals( key ) ) {
                    tString = ts;
                    break;
                }
            }
            if ( tString == null ) {
                tString = new TranslatedString();
                tString.initializeNewString( translation, key, value );
                session.save( tString );
                session.update( translation );
            }
            else {
                tString.setValue( value );
                tString.setUpdatedAt( new Date() );

                session.update( tString );
            }

            // if it's cached, change the cache entries so it doesn't fail
            ( (PhetLocalizer) PhetWicketApplication.get().getResourceSettings().getLocalizer() ).updateCachedString( translation, key, value );

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "Exception: " + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
            return false;
        }
        return true;
    }

    public static boolean setString( Session session, String key, String value, int translationId ) {
        System.out.println( "Request to set string with key=" + key + " and value=" + value );
        if ( value == null ) {
            return false;
        }
        // TODO: refactor this somehow with setString with locale
        value = value.replaceAll( "\r", "" );
        value = value.replaceAll( "\n", "<br/>" );

        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Translation translation = (Translation) session.createQuery( "select t from Translation as t where t.id = :id" ).setInteger( "id", translationId ).uniqueResult();
            TranslatedString tString = null;
            for ( Object o : translation.getTranslatedStrings() ) {
                TranslatedString ts = (TranslatedString) o;
                if ( ts.getKey().equals( key ) ) {
                    tString = ts;
                    break;
                }
            }
            if ( tString == null ) {
                tString = new TranslatedString();
                tString.initializeNewString( translation, key, value );
                session.save( tString );
                session.update( translation );
            }
            else {
                tString.setValue( value );
                tString.setUpdatedAt( new Date() );

                session.update( tString );
            }

            // if it's cached, change the cache entries so it doesn't fail
            ( (PhetLocalizer) PhetWicketApplication.get().getResourceSettings().getLocalizer() ).updateCachedString( translation, key, value );

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "Exception: " + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
            return false;
        }
        return true;
    }

    public static String getLocaleTitle( Locale locale, Locale targetLocale, PhetLocalizer localizer ) {
        String defaultLanguageName = locale.getDisplayName( targetLocale );
        String languageName = localizer.getString( "language.names." + LocaleUtils.localeToString( locale ), new LocalizedLabel( "toss", targetLocale, "toss" ), null, defaultLanguageName, false );
        return languageName;
    }
}
