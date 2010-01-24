package edu.colorado.phet.website.authentication;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.util.value.ValueMap;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.Linkable;

public class SignInPage extends PhetPage {

    // TODO: gracefully handle when user enters the wrong password

    // TODO: add translation links at the bottom?

    private TextField username;
    private PasswordTextField password;

    private String destination = null;

    private static Logger logger = Logger.getLogger( SignInPage.class.getName() );

    /**
     * Whether to remember the user or not.
     * NOTE: don't convert to local variable, PropertyModel uses reflection to change this value
     */
    private boolean remember = true;

    public SignInPage( PageParameters parameters ) {
        super( parameters, true );

        if ( parameters != null && parameters.containsKey( "dest" ) ) {
            destination = parameters.getString( "dest" );
        }

        // TODO: add register link to i18n

        add( new SignInForm( "sign-in-form" ) );

        addTitle( "Sign in" );

        add( new LocalizedText( "to-register", "signIn.toRegister", new Object[]{RegisterPage.getLinker( destination == null ? "/" : destination ).getHref( getPageContext(), getPhetCycle() )} ) );
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

            rememberBox.add( new CheckBox( "remember-check", new PropertyModel( SignInPage.this, "remember" ) ) );

            username.setPersistent( remember );
        }

        public final void onSubmit() {
            if ( PhetSession.get().signIn( (PhetRequestCycle) getRequestCycle(), username.getModelObjectAsString(), password.getInput() ) ) {
                if ( destination != null ) {
                    getRequestCycle().setRequestTarget( new RedirectRequestTarget( destination ) );
                }
                else {
                    if ( !SignInPage.this.continueToOriginalDestination() ) {
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
        getPage().removePersistedFormData( SignInPage.SignInForm.class, true );
    }

    public static Linkable getLinker( final String destination ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "sign-in?dest=" + destination;
            }
        };
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^sign-in$", SignInPage.class );
    }

}
