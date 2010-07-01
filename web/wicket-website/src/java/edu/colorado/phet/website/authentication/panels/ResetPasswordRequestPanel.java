package edu.colorado.phet.website.authentication.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * @author Sam Reid
 */
public class ResetPasswordRequestPanel extends PhetPanel {
    private static Logger logger = Logger.getLogger( ChangePasswordPanel.class );
    private FeedbackPanel feedback;

    public ResetPasswordRequestPanel( String id, PageContext context ) {
        super( id, context );
        feedback = new FeedbackPanel( "feedback" );
        add( feedback );
        add( new EnterEmailAddressForm( "enter-email-address-form", context ) );
    }

    public class EnterEmailAddressForm extends Form {
        protected TextField emailTextField;
        private final PageContext context;
        private int phetUserID;

        public EnterEmailAddressForm( String id, PageContext context ) {
            super( id );
            this.context = context;

            emailTextField = new TextField<String>( "email-address", new Model<String>( "" ) );
            emailTextField.setRequired( false );//Since some users may still have password = ""
            add( emailTextField );
            add( new AbstractFormValidator() {
                public FormComponent[] getDependentFormComponents() {
                    return new FormComponent[]{emailTextField};
                }

                public void validate( Form<?> form ) {
                    boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                        public boolean run( Session session ) {
                            PhetUser user = (PhetUser) session.createQuery( "select u from PhetUser as u where u.email=:email" ).setString( "email", emailTextField.getInput() ).uniqueResult();
                            if ( user != null ) {
                                phetUserID = user.getId();
                                return true;
                            }
                            else {
                                return false;
                            }
                        }
                    } );
                    if ( !success ) {
                        error( emailTextField, "resetPasswordRequest.validation.noAccountFound" );
                    }
                }
            } );
        }

        @Override
        protected void onSubmit() {
//            getRequestCycle().setRequestTarget( new RedirectPageRequestTarget( ResetPasswordRequestSuccess.getLinker().getRawURL(context,getPhetCycle())) );
        }
    }
}