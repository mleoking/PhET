package edu.colorado.phet.website.authentication.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * @author Sam Reid
 */
public class ChangePasswordPanel extends PhetPanel {

    private final boolean shouldConfirmCurrentPassword; // whether the
    private final PhetUser userToChange; // not required to be persistent with hibernate. don't rely on this in onsubmit for anything other than the id

    private FeedbackPanel feedback;

    private static Logger logger = Logger.getLogger( ChangePasswordPanel.class );

    /**
     * Add a change password panel that will allow changing the password of the passed in user. It can optionally
     * contain a text box to confirm the current user's password before changing.
     *
     * @param id                           Wicket id
     * @param context                      Page context
     * @param user                         A PhetUser if we do not need to check the current password
     * @param shouldConfirmCurrentPassword Whether to prompt for the current user's password
     */
    public ChangePasswordPanel( String id, PageContext context, PhetUser user, boolean shouldConfirmCurrentPassword ) {
        super( id, context );

        // don't check here, because if someone forgot their password they will not be logged in
        //AuthenticatedPage.checkSignedIn();

        this.shouldConfirmCurrentPassword = shouldConfirmCurrentPassword;
        this.userToChange = user;

        feedback = new FeedbackPanel( "feedback" );
        add( feedback );
        add( new SetPasswordForm( "set-password-form", context ) );

        add( new Label( "email-address", userToChange.getEmail() ) );
    }

    public class SetPasswordForm extends Form {
        protected PasswordTextField currentPasswordTextField;
        protected PasswordTextField newPasswordTextField;
        protected PasswordTextField confirmNewPasswordTextField;
        private final PageContext context;

        public SetPasswordForm( String id, PageContext context ) {
            super( id );
            this.context = context;

            if ( shouldConfirmCurrentPassword ) {
                currentPasswordTextField = new PasswordTextField( "current-password", new Model<String>( "" ) );
                currentPasswordTextField.setRequired( false );//Since some users may still have password = ""
                add( currentPasswordTextField );

                // verify the user's current password
                add( new AbstractFormValidator() {
                    public FormComponent[] getDependentFormComponents() {
                        return new FormComponent[]{currentPasswordTextField};
                    }

                    public void validate( Form<?> form ) {
                        if ( !PhetSession.passwordEquals( PhetSession.get().getUser().getHashedPassword(), currentPasswordTextField.getInput() ) ) {
                            error( currentPasswordTextField, "changePassword.validation.incorrectPassword" );
                        }
                    }
                } );
            }
            else {
                add( new InvisibleComponent( "current-password" ) ); // hide the current password control. wicket:enclosure hides the HTML content surrounding it
            }
            newPasswordTextField = new PasswordTextField( "new-password", new Model<String>( "" ) );
            add( newPasswordTextField );
            confirmNewPasswordTextField = new PasswordTextField( "confirm-new-password", new Model<String>( "" ) );
            confirmNewPasswordTextField.setRequired( false ); //If this is required, then the feedback can appear twice; the requirement is handled in the newPassword textfield
            add( confirmNewPasswordTextField );

            // make sure the user's new password matches
            add( new AbstractFormValidator() {
                public FormComponent[] getDependentFormComponents() {
                    return new FormComponent[]{newPasswordTextField, confirmNewPasswordTextField};
                }

                public void validate( Form<?> form ) {
                    if ( !newPasswordTextField.getInput().equals( confirmNewPasswordTextField.getInput() ) ) {
                        error( confirmNewPasswordTextField, "changePassword.validation.mismatch" );
                    }
                }
            } );
        }

        @Override
        protected void onSubmit() {
            final PhetUser[] savedUser = new PhetUser[1];
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    PhetUser user = (PhetUser) session.load( PhetUser.class, userToChange.getId() );
                    savedUser[0] = user;
                    user.setPassword( newPasswordTextField.getModelObject() );
                    session.update( user );
                    return true;
                }
            } );
            logger.debug( "Finished hibernate: success = " + success );
            if ( success ) {
                userToChange.setPassword( newPasswordTextField.getModelObject() );

                if ( !PhetSession.get().isSignedIn() ) {
                    PhetSession.get().setUser( savedUser[0] );
                }

                //redirect to the success page  
                getRequestCycle().setRequestTarget( new RedirectRequestTarget( ChangePasswordSuccessPanel.getLinker().getRawUrl( context, getPhetCycle() ) ) );
            }
        }
    }
}