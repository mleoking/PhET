package edu.colorado.phet.website.authentication.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.util.value.ValueMap;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.RegisterPage;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.StringPasswordTextField;
import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class SignInPanel extends PhetPanel {

    private TextField username;
    private PasswordTextField password;

    private String destination = null;

    /**
     * Whether to remember the user or not.
     * NOTE: don't convert to local variable, PropertyModel uses reflection to change this value
     */
    private boolean remember = true;

    private static final Logger logger = Logger.getLogger( SignInPanel.class.getName() );

    public SignInPanel( String id, PageContext context, String destination ) {
        super( id, context );

        this.destination = destination;

        add( new SignInForm( "sign-in-form" ) );

        add( new LocalizedText( "to-register", "signIn.toRegister", new Object[]{
                RegisterPage.getLinker( destination == null ? "/" : destination ).getHref( context, getPhetCycle() )
        } ) );
    }

    public final class SignInForm extends Form {
        private static final long serialVersionUID = 1L;

        private final ValueMap properties = new ValueMap();

        public SignInForm( final String id ) {
            super( id );

            add( username = new StringTextField( "username", new PropertyModel( properties, "username" ) ) );
            add( password = new StringPasswordTextField( "password", new PropertyModel( properties, "password" ) ) );

            final WebMarkupContainer rememberBox = new WebMarkupContainer( "remember" );
            add( rememberBox );

            rememberBox.add( new CheckBox( "remember-check", new PropertyModel( SignInPanel.this, "remember" ) ) );

            username.setPersistent( remember );
        }

        public final void onSubmit() {
            if ( PhetSession.get().signIn( (PhetRequestCycle) getRequestCycle(), username.getModelObjectAsString(), password.getInput() ) ) {
                if ( destination != null ) {
                    getRequestCycle().setRequestTarget( new RedirectRequestTarget( destination ) );
                }
                else {
                    if ( !SignInPanel.this.continueToOriginalDestination() ) {
                        getRequestCycle().setRequestTarget( new RedirectRequestTarget( "/" ) );
                    }
                }
            }
            else {
                error( "Signing in failed" );
            }
        }
    }

    public final void forgetMe() {
        getPage().removePersistedFormData( SignInForm.class, true );
    }
}
