package edu.colorado.phet.wickettest.translation;

import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.model.IModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.TranslatedString;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class PhetLocalizer extends Localizer {
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

    @Override
    public String getString( String key, Component component, IModel model, String defaultValue ) throws MissingResourceException {
        //System.out.println( "getString( \"" + key + "\", " + component.getClass().getSimpleName() + ", " + model + ", " + defaultValue + " )" );
        if ( component.getVariation() != null ) {
            Session session = ( (PhetRequestCycle) component.getRequestCycle() ).getHibernateSession();
            Transaction tx = null;
            String value = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery( "select ts from TranslatedString as ts, Translation as t where (ts.translation = t and t.id = :translation_id and ts.key = :key)" );
                query.setInteger( "translation_id", new Integer( component.getVariation() ) );
                query.setString( "key", key );
                TranslatedString translatedString = (TranslatedString) query.uniqueResult();
                if ( translatedString != null ) {
                    value = translatedString.getValue();
                }

                tx.commit();
            }
            catch( RuntimeException e ) {
                System.out.println( "WARNING: PhetLocalizer exception: " + e );
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

            if ( value == null ) {
                value = defaultValue;
            }

            if ( value == null ) {
                //System.out.println( "Could not find a string for " + key + " with variation " + component.getVariation() );
                return super.getString( key, component, model, defaultValue );
            }

            return value;
        }
        else {
            return super.getString( key, component, model, defaultValue );
        }
    }
}
