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
        checkString( session, "profile.edit.changePassword", "Change password" );
        checkString( session, "nav.changePasswordSuccess", "Success" );
        checkString( session, "changePasswordSuccess.title", "Your password has been successfully changed." );
        checkString( session, "changePassword.validation.newPasswordBlank", "The new password must not be blank." );
        checkString( session, "changePassword.reset", "Reset" );
        checkString( session, "changePassword.submit", "Submit" );
        checkString( session, "changePassword.currentPassword", "Current Password:" );
        checkString( session, "changePassword.newPassword", "New Password:" );
        checkString( session, "changePassword.confirmNewPassword", "Confirm New Password:" );
        checkString( session, "changePassword.validation.mismatch", "Confirmed password did not match the new password." );
        checkString( session, "changePassword.validation.incorrectPassword", "The current password was incorrect." );
        checkString( session, "changePassword.title", "Change your PhET Password" );
        checkString( session, "changePassword.header", "Change your password:" );
        checkString( session, "signIn.resetYourPassword", "Forgot your password?" );
        checkString( session, "resetPasswordRequest.title", "Reset your password" );
        checkString( session, "resetPasswordRequest.submit", "Submit" );
        checkString( session, "resetPasswordRequest.emailAddress", "Email Address:" );
        checkString( session, "resetPasswordRequest.description", "Please enter your email address and press sumbit. An email will be sent to you with a link to reset your password." );
        checkString( session, "resetPasswordRequest.header", "Reset your password." );
        checkString( session, "resetPasswordRequest.validation.noAccountFound", "No PhET account was found for the specified email address." );
        checkString( session, "resetPasswordRequest.emailBody", "<p>To reset your password, please click <a href=\"{0}\">{0}</a> or follow the link.</p>" ); // TODO: improve strings
        checkString( session, "resetPasswordRequest.emailSubject", "Password reset requested" );
        checkString( session, "resetPasswordRequestSuccess.message", "Your password has been successfully changed." );
        checkString( session, "resetPasswordCallback.title", "Reset Password" );
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
