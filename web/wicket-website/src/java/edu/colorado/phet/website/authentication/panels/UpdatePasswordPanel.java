package edu.colorado.phet.website.authentication.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * @author Sam Reid
 */
public class UpdatePasswordPanel extends PhetPanel {
    private static Logger logger = Logger.getLogger( UpdatePasswordPanel.class );
    private FeedbackPanel feedback;

    public UpdatePasswordPanel( String id, PageContext context ) {
        super( id, context );
        AuthenticatedPage.checkSignedIn();
        feedback = new FeedbackPanel( "feedback" );
        add( feedback );
        add( new SetPasswordForm( "set-password-form" ) );
    }

    public class SetPasswordForm extends Form {
        protected PasswordTextField currentPasswordTextField;
        protected PasswordTextField newPasswordTextField;
        protected PasswordTextField confirmNewPasswordTextField;

        public SetPasswordForm( String id ) {
            super( id );

            currentPasswordTextField = new PasswordTextField( "current-password", new Model<String>( "" ) );
            currentPasswordTextField.setRequired( false );//Since some users may still have password = ""
            add( currentPasswordTextField );
            newPasswordTextField = new PasswordTextField( "new-password", new Model<String>( "" ) );
            add( newPasswordTextField );
            confirmNewPasswordTextField = new PasswordTextField( "confirm-new-password", new Model<String>( "" ) );
            confirmNewPasswordTextField.setRequired( false ); //If this is required, then the feedback can appear twice; the requirement is handled in the newPassword textfield
            add( confirmNewPasswordTextField );
            add( new AbstractFormValidator() {
                public FormComponent[] getDependentFormComponents() {
                    return new FormComponent[]{newPasswordTextField, confirmNewPasswordTextField};
                }

                public void validate( Form<?> form ) {
                    logger.info( "Validating new passwords match: " + getPasswordDebugText() );
                    if ( !newPasswordTextField.getInput().equals( confirmNewPasswordTextField.getInput() ) ) {
                        error( confirmNewPasswordTextField, "updatePassword.validation.mismatch" );
                    }
                }
            } );

            add( new AbstractFormValidator() {
                public FormComponent[] getDependentFormComponents() {
                    return new FormComponent[]{currentPasswordTextField};
                }

                public void validate( Form<?> form ) {
                    logger.info( "Validating old password is correct: " + getPasswordDebugText() );
                    PhetUser currentUser = PhetSession.get().getUser();
                    if ( !PhetSession.passwordEquals( currentUser.getHashedPassword(), currentPasswordTextField.getInput() ) ) {
                        error( currentPasswordTextField, "updatePassword.validation.incorrectPassword" );
                    }
                }
            } );
        }

        private String getPasswordDebugText() {
            return "currentPassword = " + currentPasswordTextField.getInput() + ", newPassword = " + newPasswordTextField.getInput() + ", confirm = " + confirmNewPasswordTextField.getInput();
        }

        @Override
        protected void onSubmit() {
            logger.info( "Current model object = " + getPasswordDebugText() + ", model object = " + newPasswordTextField.getModelObject() );
            final PhetUser currentUser = PhetSession.get().getUser();
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    PhetUser user = (PhetUser) session.load( PhetUser.class, currentUser.getId() );
                    user.setPassword( newPasswordTextField.getModelObject() );
                    session.update( user );
                    return true;
                }
            } );
            logger.info( "Finished hibernate: success = " + success );
            if ( success ) {
                currentUser.setPassword( newPasswordTextField.getModelObject() );
                
                //redirect to another page
            }
        }
    }
}