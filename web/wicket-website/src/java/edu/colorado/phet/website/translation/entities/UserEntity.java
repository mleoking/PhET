package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.panels.*;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class UserEntity extends TranslationEntity {
    public UserEntity() {

        addString( "session.loginRegister" );
        addString( "session.editProfile" );
        addString( "session.logout" );

        addString( "signIn.signIn" );
        addString( "signIn.email" );
        addString( "signIn.password" );
        addString( "signIn.rememberMe" );
        addString( "signIn.submit" );
        addString( "signIn.reset" );
        addString( "signIn.toRegister" );
        addString( "signIn.validation.failed" );

        addString( "validation.user.email" );
        addString( "validation.user.user" );
        addString( "validation.user.passwordMatch" );
        addString( "validation.user.password" );
        addString( "validation.user.description" );
        addString( "validation.user.emailUsed" );
        addString( "validation.user.problems" );

        addString( "profile.register" );
        addString( "profile.name" );
        addString( "profile.organization" );
        addString( "profile.description" );
        addString( "profile.email" );
        addString( "profile.password" );
        addString( "profile.passwordCopy" );
        addString( "profile.register.info" );
        addString( "profile.register.submit" );
        addString( "profile.register.reset" );
        addString( "profile.jobTitle" );
        addString( "profile.address1" );
        addString( "profile.address2" );
        addString( "profile.city" );
        addString( "profile.state" );
        addString( "profile.country" );
        addString( "profile.zip" );
        addString( "profile.phone1" );
        addString( "profile.phone2" );
        addString( "profile.fax" );
        addString( "profile.receiveEmail" );

        addString( "profile.edit" );
        addString( "profile.edit.submit" );
        addString( "profile.edit.reset" );

        addString( "signOut.beingSignedOut" );

        addString( "profile.edit.changePassword" );
        addString( "nav.changePasswordSuccess" );
        addString( "changePasswordSuccess.title" );
        addString( "changePassword.validation.newPasswordBlank" );
        addString( "changePassword.reset" );
        addString( "changePassword.submit" );
        addString( "changePassword.currentPassword" );
        addString( "changePassword.newPassword" );
        addString( "changePassword.confirmNewPassword" );
        addString( "changePassword.validation.mismatch" );
        addString( "changePassword.validation.incorrectPassword" );
        addString( "changePassword.title" );
        addString( "changePassword.header" );
        addString( "signIn.resetYourPassword" );
        addString( "resetPasswordRequest.title" );
        addString( "resetPasswordRequest.submit" );
        addString( "resetPasswordRequest.emailAddress" );
        addString( "resetPasswordRequest.description" );
        addString( "resetPasswordRequest.header" );
        addString( "resetPasswordRequest.validation.noAccountFound" );
        addString( "resetPasswordRequest.emailBody" );
        addString( "resetPasswordRequest.emailSubject" );
        addString( "resetPasswordRequestSuccess.message" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new SignInPanel( id, context, "/" );
            }
        }, "Sign In Page" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new RegisterPanel( id, context, "/" );
            }
        }, "Register Page" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new EditProfilePanel( id, context, null );
            }
        }, "Edit Profile Page" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ChangePasswordPanel( id, context, PhetSession.get().getUser(), true );
            }
        }, "Change Password Page" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ResetPasswordRequestPanel( id, context );
            }
        }, "Reset Password Page" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ResetPasswordRequestSuccessPanel( id, context );
            }
        }, "Reset Password Success Page" );

    }

    public String getDisplayName() {
        return "Users";
    }
}