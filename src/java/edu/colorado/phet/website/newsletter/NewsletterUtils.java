package edu.colorado.phet.website.newsletter;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.content.contribution.ContributionCreatePage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.translation.TranslationMainPage;
import edu.colorado.phet.website.util.EmailUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class NewsletterUtils {

    private static final String THANKYOU_MESSAGE = "<p><br/>Thanks,<br/>" +
                                                   "The PhET Team<br/>" +
                                                   "<a href=\"http://phet.colorado.edu\">http://phet.colorado.edu</a></p>";

    private static final String WELCOME_FOOTER = "<p>For updates on PhET activities:<br/>" +
                                                 "<a href=\"" + Linkers.BLOG.getDefaultRawUrl() + "\">Read our blog</a>,<br/>" +
                                                 "<a href=\"" + Linkers.FACEBOOK_PAGE.getDefaultRawUrl() + "\">Join us on Facebook</a>, or<br/>" +
                                                 "<a href=\"" + Linkers.TWITTER_PAGE.getDefaultRawUrl() + "\">Follow us on Twitter</a></p>";

    private static final Logger logger = Logger.getLogger( NewsletterUtils.class );
    private static final String HIDDEN_STYLE = "style=\"font-size: 12px;color: #888888\"";

    public static String getUnsubscribeLink( PageContext context, String confirmationKey ) {
        return EmailUtils.makeUrlAbsolute( UnsubscribeLandingPage.getLinker( confirmationKey ).getRawUrl( context, PhetRequestCycle.get() ) );
    }

    public static String getUnsubscribeText( PageContext context, String confirmationKey ) {
        String link = getUnsubscribeLink( context, confirmationKey );
        return "<p " + HIDDEN_STYLE + ">To opt out of PhET newsletters, please click on this link: " +
               "<a " + HIDDEN_STYLE + " href=\"" + link + "\">" + link + "</a> or copy and paste the text into your browser.</p>";
    }

    public static boolean sendConfirmSubscriptionEmail( PageContext context, String emailAddress, String confirmationKey ) {
        String confirmLink = EmailUtils.makeUrlAbsolute( ConfirmEmailLandingPage.getLinker( confirmationKey ).getRawUrl( context, PhetRequestCycle.get() ) );

        String subject = "Please Confirm Your PhET Subscription";
        String body = "<p>Thank you for subscribing to the PhET Newsletter!</p>" +
                      "<p>To complete this process, please confirm your email by clicking on the following link:</p>" +
                      "<p><a href=\"" + confirmLink + "\">" + confirmLink + "</a></p>" +
                      "<p>Or copy and paste the link into your Web browser's address bar.</p>" +
                      "<p>If you do not wish to subscribe, then there is no need to do anything, as the subscription " +
                      "is not complete until you click the link above.</p>" +
                      THANKYOU_MESSAGE;
        return sendSingleMessage( emailAddress, subject, body );
    }

    public static boolean sendConfirmRegisterEmail( PageContext context, String emailAddress, String confirmationKey, String destination ) {
        String confirmLink = EmailUtils.makeUrlAbsolute( ConfirmEmailLandingPage.getLinker( confirmationKey, destination ).getRawUrl( context, PhetRequestCycle.get() ) );

        String subject = "Please Confirm Your PhET Email Address";
        String body = "<p>Thank you for creating an account with PhET Interactive Simulations!</p>" +
                      "<p>To complete this registration process, please confirm your email by clicking on the following link:</p>" +
                      "<p><a href=\"" + confirmLink + "\">" + confirmLink + "</a></p>" +
                      "<p>Or copy and paste the link into your Web browser's address bar.</p>" +
                      "<p>If you do not wish to register, then there is no need to do anything, as the registration " +
                      "is not complete until you click the link above.</p>" +
                      THANKYOU_MESSAGE;
        return sendSingleMessage( emailAddress, subject, body );
    }

    public static boolean sendNewsletterWelcomeEmail( PageContext context, PhetUser user ) {
        String subject = "PhET Newsletter Subscription Confirmation";
        //String newsletterArchiveLink = EmailUtils.makeUrlAbsolute( AboutNewsPanel.getLinker().getRawUrl( context, PhetRequestCycle.get() ) );
        String body = "<p>Your subscription to the PhET newsletter is now complete. We typically send about 2-4 newsletters per year. </p>" +
                      //"<p>Please visit our website to view <a href=\"" + newsletterArchiveLink + "\">previous newsletters</a> or for sims or activities.</p>" +
                      WELCOME_FOOTER +
                      THANKYOU_MESSAGE +
                      getUnsubscribeText( context, user.getConfirmationKey() );
        return sendSingleMessage( user.getEmail(), subject, body );
    }

    public static boolean sendUserWelcomeEmail( PageContext context, PhetUser user ) {
        String activityLink = EmailUtils.makeUrlAbsolute( ContributionCreatePage.getLinker().getRawUrl( context, PhetRequestCycle.get() ) );
        String translationLink = EmailUtils.makeUrlAbsolute( TranslationMainPage.getLinker().getRawUrl( context, PhetRequestCycle.get() ) );

        String subject = "Welcome to PhET";
        String body = "<p>Hello" + ( user.getName() == null || user.getName().length() == 0 ? "" : ", " + user.getName() ) + "!</p>" +
                      "<p>Thank you for creating an account on PhET's website! Your account is now active.</p>" +
                      "<p>You can now <a href=\"" + activityLink + "\">submit activities</a>, comment on activities, and " +
                      "<a href=\"" + translationLink + "\">translate the website</a>.</p>" +
                      WELCOME_FOOTER +
                      THANKYOU_MESSAGE;
        return sendSingleMessage( user.getEmail(), subject, body );
    }

    /**
     * Send when the user visits the UnsubscribeLandingPage
     */
    public static boolean sendUnsubscribedEmail( PageContext context, PhetUser user ) {
        String subject = "PhET Newsletter Unsubscribe Confirmation";
        String body = "<p>The following email has been unsubscribed from the PhET newsletter: " + user.getEmail() + ".</p>" +
                      "<p>To resubscribe, please log in to your account on <a href=\"http://phet.colorado.edu\">the PhET website</a>.</p>" +
                      THANKYOU_MESSAGE +
                      "<p " + HIDDEN_STYLE + ">If you need further assistance, please contact us at " +
                      "<a " + HIDDEN_STYLE + " href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>.</p>";
        return sendSingleMessage( user.getEmail(), subject, body );
    }

    private static boolean sendSingleMessage( String emailAddress, String subject, String body ) {
        logger.info( "sending message '" + subject + "' to " + emailAddress );
        try {
            EmailUtils.GeneralEmailBuilder message = new EmailUtils.GeneralEmailBuilder( subject, WebsiteConstants.HELP_EMAIL );
            message.setBody( body );
            message.addRecipient( emailAddress );
            message.addReplyTo( WebsiteConstants.HELP_EMAIL );
            return EmailUtils.sendMessage( message );
        }
        catch ( MessagingException e ) {
            logger.warn( "message send error: ", e );
            return false;
        }
    }
}
