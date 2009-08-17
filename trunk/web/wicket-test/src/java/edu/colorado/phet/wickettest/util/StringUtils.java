package edu.colorado.phet.wickettest.util;

import java.util.Date;
import java.util.Locale;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.data.TranslatedString;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.translation.PhetLocalizer;

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
        return getString( session, key, WicketApplication.getDefaultLocale() );
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

            Query query = session.createQuery( "select ts from TranslatedString as ts, Translation as t where (ts.translation = t and t.visible = true and t.id = :id and ts.key = :key)" );
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

    public static boolean  setEnglishString( Session session, String key, String value ) {
        return setString( session, key, value, WicketApplication.getDefaultLocale() );
    }

    public static boolean setString( Session session, String key, String value, Locale locale ) {
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
            ( (PhetLocalizer) WicketApplication.get().getResourceSettings().getLocalizer() ).updateCachedString( translation, key, value );

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
            ( (PhetLocalizer) WicketApplication.get().getResourceSettings().getLocalizer() ).updateCachedString( translation, key, value );

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
}
