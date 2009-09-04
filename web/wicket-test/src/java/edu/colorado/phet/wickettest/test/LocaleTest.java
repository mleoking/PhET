package edu.colorado.phet.wickettest.test;

import java.util.Locale;

import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.StringUtils;

public class LocaleTest {
    public static void main( String[] args ) {
        new InitializeDatasource().init();
        Session session = HibernateUtils.getInstance().openSession();
        for ( Locale locale : Locale.getAvailableLocales() ) {
            String id = LocaleUtils.localeToString( locale );
            System.out.println( id + " " + locale.getDisplayName() );
            StringUtils.setEnglishString( session, "language.names." + id, locale.getDisplayName() );
        }
        session.close();
    }
}
