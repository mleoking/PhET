package edu.colorado.phet.website.translation.entities;

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

        addString( "validation.user.email" );
        addString( "validation.user.user" );
        addString( "validation.user.passwordMatch" );
        addString( "validation.user.password" );
        addString( "validation.user.description" );
        addString( "validation.user.emailUsed" );
        addString( "validation.user.problems" );

        // TODO: convert sign in / register / edit profile to panel? then should be previewable here
    }

    public String getDisplayName() {
        return "Users";
    }
}