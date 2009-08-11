package edu.colorado.phet.wickettest.authentication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;

import edu.colorado.phet.wickettest.templates.PhetPage;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;
import edu.colorado.phet.wickettest.util.PhetSession;

public class SignInPage extends PhetPage {


    private PasswordTextField password;
    private boolean remember = true;
    private TextField username;

    public SignInPage( PageParameters parameters ) {
        super( parameters, true );

        add( new SignInForm( "sign-in-form" ) );

        addTitle( "Sign in" );
    }

    public final class SignInForm extends Form {
        private static final long serialVersionUID = 1L;

        private final ValueMap properties = new ValueMap();

        public SignInForm( final String id ) {
            super( id );

            add( username = new TextField( "username", new PropertyModel( properties, "username" ) ) );
            add( password = new PasswordTextField( "password", new PropertyModel( properties, "password" ) ) );

            final WebMarkupContainer rememberBox = new WebMarkupContainer( "remember" );
            add( rememberBox );

            rememberBox.add( new CheckBox( "remember-check", new PropertyModel( SignInPage.this,
                                                                                "remember" ) ) );

            username.setPersistent( remember );
        }

        public final void onSubmit() {
            if ( PhetSession.get().signIn( (PhetRequestCycle) getRequestCycle(), username.getModelObjectAsString(), password.getInput() ) ) {
                if ( !SignInPage.this.continueToOriginalDestination() ) {
                    SignInPage.this.setResponsePage( SignInPage.this.getApplication().getSessionSettings().getPageFactory().newPage(
                            SignInPage.this.getApplication().getHomePage(), (PageParameters) null ) );
                }
            }
            else {
                error( "Signing in failed" );
            }
        }
    }

    public final void forgetMe() {
        getPage().removePersistedFormData( SignInPage.SignInForm.class, true );
    }


}
