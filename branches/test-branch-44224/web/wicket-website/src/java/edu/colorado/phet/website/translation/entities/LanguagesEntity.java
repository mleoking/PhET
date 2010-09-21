package edu.colorado.phet.website.translation.entities;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class LanguagesEntity extends TranslationEntity {

    public LanguagesEntity() {
        final List<TranslatedString> strings = new LinkedList<TranslatedString>();
        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                List strs = session.createQuery( "select ts from Translation as t, TranslatedString as ts where t.visible = true and t.locale = :locale and ts.translation = t and ts.key like 'language.names%'" )
                        .setLocale( "locale", PhetWicketApplication.getDefaultLocale() ).list();
                for ( Object o : strs ) {
                    TranslatedString string = (TranslatedString) o;
                    strings.add( string );
                }
                return true;
            }
        } );

        Collections.sort( strings, new Comparator<TranslatedString>() {
            public int compare( TranslatedString a, TranslatedString b ) {
                return a.getKey().compareTo( b.getKey() );
            }
        } );

        for ( TranslatedString languageName : strings ) {
            addString( languageName.getKey() );
        }
    }

    public String getDisplayName() {
        return "Languages";
    }
}