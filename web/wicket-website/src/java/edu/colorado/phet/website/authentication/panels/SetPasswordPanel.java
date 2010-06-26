package edu.colorado.phet.website.authentication.panels;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.PropertyModel;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * @author Sam Reid
 */
public class SetPasswordPanel extends PhetPanel {
    public SetPasswordPanel( String id, PageContext context ) {
        super( id, context );
        add( new SetPasswordForm( "set-password-form" ) );
    }

    public class SetPasswordForm extends Form {
        private String currentPassword;
        private String newPassword;
        private String confirmNewPassword;

        public SetPasswordForm( String id ) {
            super( id );
            add( new PasswordTextField( "current-password" ,new PropertyModel<String>( this, "currentPassword" ) ) );
            add( new PasswordTextField( "new-password" ) );
            add( new PasswordTextField( "confirm-new-password" ) );
        }

        @Override
        protected void onSubmit() {

        }
    }
}