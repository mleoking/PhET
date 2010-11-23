package edu.colorado.phet.website;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.util.hibernate.HibernateUtils;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Contains strings that have been added since the last production deployment. If strings by those key names don't exist
 * they will be created.
 */
public class NewStrings {

    private static Logger logger = Logger.getLogger( NewStrings.class );

    public static void checkNewStrings() {
        Session session = HibernateUtils.getInstance().openSession();
        checkString( session, "newsletter.subscribe.email", "Email Address:" );
        checkString( session, "newsletter.subscribe.submit", "Subscribe" );
        checkString( session, "newsletter.validation.email.Required", "An email address is required" );
        checkString( session, "newsletter.validation.email", "A valid email address is required" );
        checkString( session, "newsletter.validation.attempts", "Too many newsletter attempts have been made. Please try again later" );
        checkString( session, "newsletter.nowSubscribed", "{0} is now subscribed to the PhET newsletter." );
        checkString( session, "newsletter.nowUnsubscribed", "{0} is now unsubscribed from the PhET newsletter." );
        session.close();
    }

    private static void checkString( Session session, String key, String value ) {
        String result = StringUtils.getStringDirect( session, key, PhetWicketApplication.getDefaultLocale() );
        if ( result == null ) {
            logger.warn( "Auto-setting English string with key=" + key + " value=" + value );
            StringUtils.setEnglishString( session, key, value );
        }
    }
}
