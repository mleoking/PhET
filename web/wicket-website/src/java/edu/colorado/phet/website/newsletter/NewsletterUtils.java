package edu.colorado.phet.website.newsletter;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.content.about.AboutNewsPanel;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.util.EmailUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class NewsletterUtils {

    private static final String THANKYOU_MESSAGE = "<p><br/>Thanks,<br/>The PhET Team</p>";

    private static final Logger logger = Logger.getLogger( NewsletterUtils.class );
    private static final String HIDDEN_STYLE = "style=\"font-size: 12px;color: #888888\"";

    public static String getUnsubscribeLink( PageContext context, String confirmationKey ) {
        return EmailUtils.makeUrlAbsolute( UnsubscribeLandingPage.getLinker( confirmationKey ).getRawUrl( context, PhetRequestCycle.get() ) );
    }

    public static String getUnsubscribeText( PageContext context, String confirmationKey ) {
        String link = getUnsubscribeLink( context, confirmationKey );
        return "<p " + HIDDEN_STYLE + ">You received this e-mail because you signed up at our " +
               "website or emailed us at <a " + HIDDEN_STYLE + " href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>. " +
               "To opt out of these newsletters, please click on this link: " +
               "<a " + HIDDEN_STYLE + " href=\"" + link + "\">" + link + "</a> or copy and paste the text into your browser.</p>";
    }

    public static boolean sendConfirmSubscriptionEmail( PageContext context, String emailAddress, String confirmationKey ) {
        String confirmLink = EmailUtils.makeUrlAbsolute( SubscribeLandingPage.getLinker( confirmationKey ).getRawUrl( context, PhetRequestCycle.get() ) );

        String subject = "Please Confirm Your PhET Subscription";
        String body = "<p>Thank you for subscribing to the PhET Newsletter!</p>" +
                      "<p>Please click on this link:</p>" +
                      "<p><a href=\"" + confirmLink + "\">" + confirmLink + "</a></p>" +
                      "<p>or copy and paste the text into your browser in order to confirm your subscription. " +
                      "If you believe you have received this message in error, " +
                      "please <a href=\"mailto:" + WebsiteConstants.HELP_EMAIL + "\">let us know</a>.</p>" +
                      THANKYOU_MESSAGE;
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

    public static boolean sendNewsletter( PhetUser user ) { // TODO: improve signature
        return false; // TODO: reimplement as true success when done
    }

    public static boolean sendNewsletterWelcomeEmail( PageContext context, PhetUser user ) {
        String subject = "PhET Newsletter Subscription Confirmation";
        String newsletterArchiveLink = EmailUtils.makeUrlAbsolute( AboutNewsPanel.getLinker().getRawUrl( context, PhetRequestCycle.get() ) );
        String body = "<p>Thank you for subscribing to the PhET Newsletter!</p>" +
                      "<p>Please visit our website to view <a href=\"" + newsletterArchiveLink + "\">previous newsletters</a> or for sims or activities.</p>" +
                      THANKYOU_MESSAGE +
                      getUnsubscribeText( context, user.getConfirmationKey() );
        try {
            EmailUtils.GeneralEmailBuilder message = new EmailUtils.GeneralEmailBuilder( subject, WebsiteConstants.HELP_EMAIL );
            message.setBody( body );
            message.addRecipient( user.getEmail() );
            message.addReplyTo( WebsiteConstants.HELP_EMAIL );
            return EmailUtils.sendMessage( message );
        }
        catch ( MessagingException e ) {
            logger.warn( "message send error: ", e );
            return false;
        }
    }

    /**
     * Send when the user visits the UnsubscribeLandingPage
     *
     * @param user User
     */
    public static boolean sendUnsubscribedEmail( PageContext context, PhetUser user ) {
        String subject = "PhET Newsletter Unsubscribe Confirmation";
        String body = "<p>The following email has been unsubscribed from the PhET newsletter: " + user.getEmail() + ".</p>" +
                      "<p>To resubscribe, please log in to your account on <a href=\"http://phet.colorado.edu\">the PhET website</a>.</p>" +
                      THANKYOU_MESSAGE +
                      "<p " + HIDDEN_STYLE + ">If you need further assistance, please contact us at " +
                      "<a " + HIDDEN_STYLE + " href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>.</p>";
        try {
            EmailUtils.GeneralEmailBuilder message = new EmailUtils.GeneralEmailBuilder( subject, WebsiteConstants.HELP_EMAIL );
            message.setBody( body );
            message.addRecipient( user.getEmail() );
            message.addReplyTo( WebsiteConstants.HELP_EMAIL );
            return EmailUtils.sendMessage( message );
        }
        catch ( MessagingException e ) {
            logger.warn( "message send error: ", e );
            return false;
        }
    }

}
