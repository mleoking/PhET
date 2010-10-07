package edu.colorado.phet.website;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Contains strings that have been added since the last production deployment. If strings by those key names don't exist
 * they will be created.
 */
public class NewStrings {

    private static Logger logger = Logger.getLogger( NewStrings.class );

    public static void checkNewStrings() {
        Session session = HibernateUtils.getInstance().openSession();
//        checkString( session, "error.internalError.message", "Please contact us at {0} for assistance or to report the error." );
//        checkString( session, "error.sessionExpired", "Your session has expired" );
//        checkString( session, "error.sessionExpired.message", "If you are submitting information, please hit 'Back', copy the information, and log in again." );
//        checkString( session, "error.pageNotFound.message", "Please contact us at {0} for assistance or to report the error." );
        checkString( session, "troubleshooting.main.q14.title", "Why can’t I completely uninstall the PhET sims that I downloaded from my Windows computer?" );
        checkString( session, "troubleshooting.main.q14.answer", "<p>When a Java Web Start simulation is run in Windows, it is added to the list of programs in Control Panel -> Add or Remove Programs.  Due to a problem in Java Web Start, sometimes the item may remain in the list even after the simulation has been removed, and Windows may report \"Unable to completely uninstall application\".  More information about this issue can be found at {0}, and the producers of Java have acknowledged the problem and reported that they plan to fix it in an upcoming version of Java.</p>" );
        checkString( session, "home.facebookText", "Join us on {0}" );
        checkString( session, "home.twitterText", "Follow us on  {0}" );
        checkString( session, "home.blogText", "Read our blog" );
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
