package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.translation.previews.TitlePreviewPanel;
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

        // TODO: convert sign in / register / edit profile to panel? then should be previewable here
    }

    public String getDisplayName() {
        return "Users";
    }
}