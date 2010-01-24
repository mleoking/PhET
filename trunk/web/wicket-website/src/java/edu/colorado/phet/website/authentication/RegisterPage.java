package edu.colorado.phet.website.authentication;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class RegisterPage extends PhetPage {

    // TODO: gracefully handle when user enters the wrong password

    private TextField name;
    private TextField organization;
    private TextField username;
    private PasswordTextField password;
    private PasswordTextField passwordCopy;
    private DropDownChoice description;
    private Model errorModel;

    private static Logger logger = Logger.getLogger( RegisterPage.class.getName() );

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

            add( name = new TextField( "name", new PropertyModel( properties, "name" ) ) );
            add( organization = new TextField( "organization", new PropertyModel( properties, "organization" ) ) );
            add( description = new DropDownChoice( "description", new PropertyModel( properties, "description" ), PhetUser.getDescriptionOptions() ) );
            add( username = new TextField( "username", new PropertyModel( properties, "username" ) ) );
            add( password = new PasswordTextField( "password", new PropertyModel( properties, "password" ) ) );
            add( passwordCopy = new PasswordTextField( "passwordCopy", new PropertyModel( properties, "passwordCopy" ) ) );
        }

        public final void onSubmit() {
            Session session = getHibernateSession();

            boolean error = false;
            String errorString = "";

            String nom = name.getModelObjectAsString();
            String org = organization.getModelObjectAsString();
            String email = username.getModelObjectAsString();
            String pass = password.getInput();
            String desc = description.getModelObjectAsString();

            logger.debug( "name: " + nom );
            logger.debug( "org: " + org );
            logger.debug( "desc: " + desc );

            if ( nom == null || nom.length() == 0 ) {
                error = true;
                errorString += " | Name is required";
            }

            if ( !pass.equals( passwordCopy.getInput() ) ) {
                error = true;
                errorString += " | Passwords are different";
            }

            if ( pass.length() == 0 ) {
                error = true;
                errorString += " | Please pick a password";
            }

            if ( !Pattern.matches( "^.+@.+\\.[a-z]+$", email ) ) {
                error = true;
                errorString += " | Email does not validate";
            }

            if ( desc == null || desc.length() == 0 ) {
                error = true;
                errorString += " | Please pick a description";
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
                        user.setName( nom );
                        user.setOrganization( org );
                        user.setDescription( desc );
                        user.setEmail( email );
                        user.setPassword( PhetSession.compatibleHashPassword( pass ) );
                        session.save( user );
                    }

                    tx.commit();
                }
                catch( RuntimeException e ) {
                    logger.warn( e );
                    if ( tx != null && tx.isActive() ) {
                        try {
                            tx.rollback();
                        }
                        catch( HibernateException e1 ) {
                            logger.error( "ERROR: Error rolling back transaction", e1 );
                        }
                        throw e;
                    }
                    error = true;
                    errorString += " | Internal error occurred";
                }
            }

            if ( error ) {
                logger.error( "Error registering" );
                logger.error( "Reason: " + errorString );
                errorModel.setObject( errorString );
            }
            else {
                // TODO: pass in destination to register page?
                PhetSession.get().signIn( (PhetRequestCycle) getRequestCycle(), username.getModelObjectAsString(), password.getInput() );
                getRequestCycle().setRequestTarget( new RedirectRequestTarget( "/" ) );
            }
        }
    }

}
