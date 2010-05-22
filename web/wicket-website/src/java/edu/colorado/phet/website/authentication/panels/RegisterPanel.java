package edu.colorado.phet.website.authentication.panels;

import java.util.List;

import org.apache.log4j.Logger;
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

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.StringPasswordTextField;
import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class RegisterPanel extends PhetPanel {

    // TODO: i18n (align the fields correctly within the table!)

    private TextField name;
    private TextField organization;
    private TextField username;
    private PasswordTextField password;
    private PasswordTextField passwordCopy;
    private DropDownChoice description;
    private Model errorModel;

    private String destination = null;

    private static final String ERROR_SEPARATOR = "<br/>";

    private static final Logger logger = Logger.getLogger( RegisterPanel.class.getName() );

    public RegisterPanel( String id, PageContext context, String destination ) {
        super( id, context );

        this.destination = destination;

        add( new RegisterForm( "register-form" ) );

        errorModel = new Model( "" );

        add( new RawLabel( "register-errors", errorModel ) );
    }

    public final class RegisterForm extends Form {
        private static final long serialVersionUID = 1L;

        private final ValueMap properties = new ValueMap();

        public RegisterForm( final String id ) {
            super( id );

            add( name = new StringTextField( "name", new PropertyModel( properties, "name" ) ) );
            add( organization = new StringTextField( "organization", new PropertyModel( properties, "organization" ) ) );
            add( description = new DropDownChoice( "description", new PropertyModel( properties, "description" ), PhetUser.getDescriptionOptions() ) );
            add( username = new StringTextField( "username", new PropertyModel( properties, "username" ) ) );
            add( password = new StringPasswordTextField( "password", new PropertyModel( properties, "password" ) ) );
            add( passwordCopy = new StringPasswordTextField( "passwordCopy", new PropertyModel( properties, "passwordCopy" ) ) );

            // so we can respond to the error messages
            password.setRequired( false );
            passwordCopy.setRequired( false );
        }

        public final void onSubmit() {
            Session session = getHibernateSession();

            boolean error = false;
            String errorString = "";
            String err = null;

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
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.user", this, "Please fill in the name field" );
            }

            if ( !pass.equals( passwordCopy.getInput() ) ) {
                error = true;
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.passwordMatch", this, "The entered passwords do not match" );
            }

            if ( pass.length() == 0 ) {
                error = true;
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.password", this, "Please enter a password" );
            }

            err = PhetUser.validateEmail( email );
            if ( err != null ) {
                error = true;
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.email", this, "Please enter a valid email address" );
            }

            if ( desc == null || desc.length() == 0 ) {
                error = true;
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.description", this, "Please pick a description" );
            }

            if ( !error ) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    List users = session.createQuery( "select u from PhetUser as u where u.email = :email" ).setString( "email", email ).list();
                    if ( !users.isEmpty() ) {
                        error = true;
                        errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.emailUsed", this, "That email address is already in use" );
                        // TODO: add option to reset password for an existing account?
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
                    errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "error.internalError", this, "Internal error occurred" );
                }
            }

            if ( error ) {
                logger.error( "Error registering" );
                logger.error( "Reason: " + errorString );
                errorString = getPhetLocalizer().getString( "validation.user.problems", this, "Please fix the following problems with the form:" ) + "<br/>" + errorString;
                errorModel.setObject( errorString );
            }
            else {
                errorModel.setObject( "" );
                PhetSession.get().signIn( (PhetRequestCycle) getRequestCycle(), username.getModelObjectAsString(), password.getInput() );
                if ( destination != null ) {
                    getRequestCycle().setRequestTarget( new RedirectRequestTarget( destination ) );
                }
                else {
                    if ( !RegisterPanel.this.continueToOriginalDestination() ) {
                        getRequestCycle().setRequestTarget( new RedirectRequestTarget( "/" ) );
                    }
                }
            }
        }
    }

}