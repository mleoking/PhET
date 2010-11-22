package edu.colorado.phet.website.newsletter;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.content.ErrorPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.Result;

/**
 * The panel which represents the main content portion of the home (index) page
 */
public class InitialSubscribePanel extends PhetPanel {

    private FeedbackPanel feedback;

    private static Logger logger = Logger.getLogger( InitialSubscribePanel.class );

    // TODO: add security measures like limiting # of times per IP / user

    public InitialSubscribePanel( String id, PageContext context ) {
        super( id, context );

        add( new SubscribeForm( "subscribe-form" ) );

        feedback = new FeedbackPanel( "feedback" );
        feedback.setVisible( false );
        add( feedback );
    }

    private class SubscribeForm extends Form {
        private final Model<String> emailModel = new Model<String>( "" );
        private RequiredTextField<String> emailTextField;

        public SubscribeForm( String id ) {
            super( id );

            emailTextField = new RequiredTextField<String>( "esnail", emailModel );
            add( emailTextField ); // called esnail instead of email because maybe it won't be picked up as such by spam?

            // validating the emails should hopefully block most things
            add( new AbstractFormValidator() {
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent<?>[] { emailTextField };
                }

                public void validate( Form<?> form ) {
                    String emailAddress = emailTextField.getInput().toString();
                    if ( !PhetUser.isValidEmail( emailAddress ) ) {
                        error( emailTextField, "newsletter.validation.email" );
                    }
                }
            } );
        }

        @Override
        protected void onValidate() {
            super.onValidate();
            feedback.setVisible( feedback.anyMessage() );
        }

        @Override
        protected void onSubmit() {
            // TODO: fill in
            final String emailAddress = emailTextField.getInput().toString();
            final Result<String> confirmationKeyResult = new Result<String>();
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    List users = session.createQuery( "select u from PhetUser as u where u.email = :email" ).setString( "email", emailAddress ).list();
                    PhetUser user;
                    if ( users.size() > 1 ) {
                        throw new RuntimeException( "More than 1 user for an email address." );
                    }
                    else if ( users.size() == 1 ) {
                        user = (PhetUser) users.get( 0 );
                        user.setConfirmationKey( NewsletterUtils.generateConfirmationKey() );
                        session.update( user );
                    }
                    else {
                        // brand new, create a newsletter-only and unconfirmed user
                        user = new PhetUser( emailAddress, true );
                        session.save( user );
                    }
                    confirmationKeyResult.setValue( user.getConfirmationKey() );
                    return true;
                }
            } );
            if ( success ) {
                boolean emailSuccess = NewsletterUtils.sendConfirmSubscriptionEmail( emailAddress, confirmationKeyResult.getValue() );
                if ( emailSuccess ) {
                    PageParameters params = new PageParameters();
                    params.put( "key", confirmationKeyResult.getValue() );
                    setResponsePage( InitialSubscribeConfirmPage.class, params );
                }
                else {
                    // sending the email failed
                    setResponsePage( ErrorPage.class );
                }
            }
            else {
                // hibernate failed (or some more internal error)
                // generic internal error page for now. should redirect people to phethelp if it is serious
                setResponsePage( ErrorPage.class );
            }
        }
    }
}
