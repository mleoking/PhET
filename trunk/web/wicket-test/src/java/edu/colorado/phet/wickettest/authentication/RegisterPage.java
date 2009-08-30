package edu.colorado.phet.wickettest.authentication;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.PhetUser;
import edu.colorado.phet.wickettest.templates.PhetPage;

public class RegisterPage extends PhetPage {

    // TODO: gracefully handle when user enters the wrong password

    private TextField username;
    private PasswordTextField password;
    private PasswordTextField passwordCopy;
    private Model errorModel;

    // TODO: spruce up error messages (and add them so they are visible to the user)

    public RegisterPage( PageParameters parameters ) {
        super( parameters, true );

        add( new RegisterForm( "register-form" ) );

        errorModel = new Model( "" );

        add( new Label( "register-errors", errorModel ) );

        addTitle( "Register" );
    }

    public final class RegisterForm extends Form {
        private static final long serialVersionUID = 1L;

        private final ValueMap properties = new ValueMap();

        public RegisterForm( final String id ) {
            super( id );

            add( username = new TextField( "username", new PropertyModel( properties, "username" ) ) );
            add( password = new PasswordTextField( "password", new PropertyModel( properties, "password" ) ) );
            add( passwordCopy = new PasswordTextField( "passwordCopy", new PropertyModel( properties, "passwordCopy" ) ) );
        }

        public final void onSubmit() {
            Session session = getHibernateSession();

            boolean error = false;
            String errorString = "";

            String email = username.getModelObjectAsString();
            String pass = password.getInput();

            if ( !pass.equals( passwordCopy.getInput() ) ) {
                error = true;
                errorString += " | Passwords are different";
            }

            if ( !Pattern.matches( "^.+@.+\\.[a-z]+$", email ) ) {
                error = true;
                errorString += " | Email does not validate";
            }

            if ( !error ) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    List users = session.createQuery( "select u from PhetUser as u where u.email = :email" ).setString( "email", email ).list();
                    if ( !users.isEmpty() ) {
                        error = true;
                        errorString += " | That email address is already in use";
                        // TODO: add option to reset password
                    }
                    else {
                        PhetUser user = new PhetUser();
                        user.setTeamMember( false );
                        user.setEmail( email );
                        user.setPassword( PhetSession.hashPassword( pass ) );
                        session.save( user );
                    }

                    tx.commit();
                }
                catch( RuntimeException e ) {
                    if ( tx != null && tx.isActive() ) {
                        try {
                            tx.rollback();
                        }
                        catch( HibernateException e1 ) {
                            System.out.println( "ERROR: Error rolling back transaction" );
                        }
                        throw e;
                    }
                    error = true;
                    errorString += " | Internal error occurred";
                    System.out.println( e );
                }
            }

            if ( error ) {
                System.out.println( "Error registering" );
                System.out.println( "Reason: " + errorString );
                errorModel.setObject( errorString );
            }
            else {
                if ( !RegisterPage.this.continueToOriginalDestination() ) {
                    RegisterPage.this.setResponsePage( RegisterPage.this.getApplication().getSessionSettings().getPageFactory().newPage(
                            RegisterPage.this.getApplication().getHomePage(), (PageParameters) null ) );
                }
            }
        }
    }

}
