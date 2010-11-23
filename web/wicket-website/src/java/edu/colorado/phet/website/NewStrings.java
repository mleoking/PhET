package edu.colorado.phet.website;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.util.StringUtils;
import edu.colorado.phet.website.util.hibernate.HibernateUtils;

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
        checkString( session, "newsletter.nowRegistered", "Thank you for registering! In a few seconds you will be redirected to your original page." );
        checkString( session, "newsletter.toFinishRegistering", "To finish creating your account, we've sent a confirmation email to {0}. In the Email, please click on the link or copy and paste it into a browser in order to confirm your email." );
        checkString( session, "newsletter.toFinishSubscribing", "To finish subscribing, we've sent a confirmation email to {0}. In the Email, please click on the link or copy and paste it into a browser in order to confirm your email." );
        checkString( session, "newsletter.troubleshooting", "If you don't receive the email momentarily, please make sure your email filter allows emails sent from {0}. For additional help, please email {1}." );
        checkString( session, "newsletter.awaitingConfirmation", "Awaiting Email Confirmation" );
        checkString( session, "newsletter.confirmEmailSent.title", "Awaiting Email Confirmation" );
        checkString( session, "newsletter.pastEditions", "<a {0}>Here are some past editions</a> of the PhET Newsletter to give you an idea of what you will receive." );
        checkString( session, "newsletter.pleaseSignUp", "Please sign up if you would like to receive the PhET Newsletter. Once you have submitted your email address, you should receive a confirmation of your subscription via email." );
        checkString( session, "newsletter.subscribeTo", "Subscribe to the Newsletter" );
        checkString( session, "newsletter.subscribe.title", "Subscribe to PhET" );
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
