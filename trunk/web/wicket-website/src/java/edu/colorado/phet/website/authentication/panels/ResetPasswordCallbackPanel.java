package edu.colorado.phet.website.authentication.panels;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.hibernate.Session;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.ResetPasswordRequest;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * TODO: This was copied from ChangePasswordPanel, perhaps in the future they will refactor to share code.
 *
 * @author Sam Reid
 */
public class ResetPasswordCallbackPanel extends PhetPanel {
    private static Logger logger = Logger.getLogger( ResetPasswordCallbackPanel.class );
    private FeedbackPanel feedback;
    private final String key;

    public ResetPasswordCallbackPanel( String id, PageContext context, String key ) {
        super( id, context );
        this.key = key;
        feedback = new FeedbackPanel( "feedback" );
        add( feedback );
        add( new SetPasswordForm( "reset-password-callback-form", context ) );
    }

    public class SetPasswordForm extends Form {
        protected PasswordTextField newPasswordTextField;
        protected PasswordTextField confirmNewPasswordTextField;
        private final PageContext context;

        public SetPasswordForm( String id, PageContext context ) {
            super( id );
            this.context = context;

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
                    if ( !newPasswordTextField.getInput().equals( confirmNewPasswordTextField.getInput() ) ) {
                        error( confirmNewPasswordTextField, "changePassword.validation.mismatch" );
                    }
                }
            } );
        }

        @Override
        protected void onSubmit() {
            logger.debug( "Found key: " + key );

            final PhetUser currentUser = lookupUserForResetPasswordKey( key );
            if ( currentUser == null ) {
                logger.debug( "No user found for key: " + key );
            }
            else {
                logger.debug( "Found corresponding user: " + currentUser.getEmail() );

                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        PhetUser user = (PhetUser) session.load( PhetUser.class, currentUser.getId() );
                        user.setPassword( newPasswordTextField.getModelObject() );
                        session.update( user );
                        return true;
                    }
                } );
                logger.debug( "Finished hibernate: success = " + success );
                if ( success ) {
                    currentUser.setPassword( newPasswordTextField.getModelObject() );

                    //redirect to the success page  
                    getRequestCycle().setRequestTarget( new RedirectRequestTarget( ChangePasswordSuccessPanel.getLinker().getRawUrl( context, getPhetCycle() ) ) );//TODO: new page for reset password?
                }
            }
        }

        private PhetUser lookupUserForResetPasswordKey( final String key ) {
            final PhetUser[] savedUser = {null};
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    List matches = session.createQuery( "select ts from ResetPasswordRequest as ts where ts.key = :key" ).setString( "key", key ).list();
                    if ( matches.size() == 0 ) {
                        logger.info( "No matches in database for key: " + key );
                        return false;
                    }
                    else if ( matches.size() > 1 ) {
                        logger.info( "Multiple database matches for key: " + key );
                        return false;
                    }
                    else {
                        ResetPasswordRequest request = (ResetPasswordRequest) matches.get( 0 );
                        PhetUser user = request.getPhetUser();
                        savedUser[0] = user;
                        return true;
                    }
                }
            } );
            return savedUser[0];//if success==false, this still returns null
        }
    }
}