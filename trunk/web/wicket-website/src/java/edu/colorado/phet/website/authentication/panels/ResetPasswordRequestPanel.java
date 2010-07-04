package edu.colorado.phet.website.authentication.panels;

import java.util.*;

import javax.mail.BodyPart;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.WebsiteProperties;
import edu.colorado.phet.website.authentication.ResetPasswordCallbackPage;
import edu.colorado.phet.website.authentication.ResetPasswordRequestSuccessPage;
import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.content.ErrorPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.ResetPasswordRequest;
import edu.colorado.phet.website.notification.NotificationHandler;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;

/**
 * @author Sam Reid
 */
public class ResetPasswordRequestPanel extends PhetPanel {
    private static Random random = new Random();
    private static Logger logger = Logger.getLogger( ChangePasswordPanel.class );
    private FeedbackPanel feedback;

    /**
     * Maximum number of failures for a given remote host (IP) for password reset requests in an hour
     */
    private static final int MAX_ATTEMPTS_IN_HOUR = 15;
    private static int count = 0;
    private static final Map<String, SecurityEntry> attempts = new HashMap<String, SecurityEntry>();

    public ResetPasswordRequestPanel( String id, PageContext context ) {
        super( id, context );

        checkSecurity();

        feedback = new FeedbackPanel( "feedback" );
        add( feedback );
        add( new EnterEmailAddressForm( "enter-email-address-form", context ) );
    }

    public class EnterEmailAddressForm extends Form {
        protected TextField emailTextField;
        private final PageContext context;

        public EnterEmailAddressForm( String id, PageContext context ) {
            super( id );
            this.context = context;

            emailTextField = new TextField<String>( "email-address", new Model<String>( "" ) );
            emailTextField.setRequired( false );//Since some users may still have password = ""
            add( emailTextField );

            // just validate email matching. don't bother making sure the email is in the proper form, since it won't matter anyways
            add( new AbstractFormValidator() {
                public FormComponent[] getDependentFormComponents() {
                    return new FormComponent[]{emailTextField};
                }

                public void validate( Form<?> form ) {
                    checkSecurity();
                    addTry( getPhetCycle().getRemoteHost() );
                    boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                        public boolean run( Session session ) {
                            PhetUser user = lookupUser( session, emailTextField.getInput() );
                            return user != null;
                        }
                    } );
                    if ( !success ) {
                        logger.warn( "Could not find email for reset password attempt. Email: " + emailTextField.getInput() + " client: " + getPhetCycle().getRemoteHost() );
                        error( emailTextField, "resetPasswordRequest.validation.noAccountFound" );
                    }
                }
            } );
        }

        private PhetUser lookupUser( Session session, String email ) {
            return (PhetUser) session.createQuery( "select u from PhetUser as u where u.email=:email" ).setString( "email", email ).uniqueResult();
        }

        @Override
        protected void onSubmit() {
            checkSecurity();
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    PhetUser user = lookupUser( session, emailTextField.getInput() );
                    if ( user != null ) {
                        //Send email to the user with a link to reset their password
                        String key = Long.toHexString( random.nextLong() ) + "g" + Long.toHexString( random.nextLong() ); //TODO: if a malicious hacker knows the time seed for random, would they be able to take advantage of this implementation?
                        logger.info( "Created reset password request key = " + key + " for email = " + user.getEmail() );
                        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest( user, new Date(), key );

                        //store the resetPasswordRequest in the database
                        session.save( resetPasswordRequest );

                        //send the email to the user
                        WebsiteProperties websiteProperties = PhetWicketApplication.get().getWebsiteProperties();
                        String body = StringUtils.messageFormat( getPhetLocalizer().getString( "resetPasswordRequest.emailBody", EnterEmailAddressForm.this ), new Object[]{"http://phet.colorado.edu" + ResetPasswordCallbackPage.getLinker( key ).getRawUrl( context, getPhetCycle() )} );
                        String subject = getPhetLocalizer().getString( "resetPasswordRequest.emailSubject", EnterEmailAddressForm.this );
                        boolean success = NotificationHandler.sendMessage( websiteProperties.getMailHost(),
                                                                           websiteProperties.getMailUser(),
                                                                           websiteProperties.getMailPassword(),
                                                                           Arrays.asList( user.getEmail() ),
                                                                           body,
                                                                           WebsiteConstants.PHET_NO_REPLY_EMAIL_ADDRESS,
                                                                           subject,
                                                                           new ArrayList<BodyPart>() );

                        return success;
                    }
                    else {
                        return false;
                    }
                }
            } );
            if ( success ) {
                //prune the table so it doesn't grow without bounds
                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        long oneDayInMillis = 1000 * 3600 * 24;
                        List expiredResetPasswordRequests = session.createQuery( "select r from ResetPasswordRequest as r where r.timestamp < :timestamp" ).setDate( "timestamp", new Date( System.currentTimeMillis() - oneDayInMillis ) ).list();
                        logger.debug( "Pruning ResetPasswordRequests, number of expired requests = " + expiredResetPasswordRequests.size() );
                        for ( Object o : expiredResetPasswordRequests ) {
                            session.delete( o );
                        }
                        return true;
                    }
                } );

                //redirect to the success page
                getRequestCycle().setRequestTarget( new RedirectRequestTarget( ResetPasswordRequestSuccessPage.getLinker().getRawUrl( context, getPhetCycle() ) ) );
            }
            else {
                logger.warn( "Failed reset password attempt for email=" + emailTextField.getInput() );
            }
        }

    }

    private void checkSecurity() {
        if ( !allowHost( getPhetCycle().getRemoteHost() ) ) {
            // they have had too many requests. deny.
            throw new RestartResponseAtInterceptPageException( ErrorPage.class );
        }
    }

    private static synchronized void addTry( String host ) {
        if ( count++ == 50 ) {
            count = 0;
            // take this opportunity to go through and clean out all of the old entries
            clearOldHosts();
        }

        SecurityEntry entry = attempts.get( host );
        if ( entry == null ) {
            entry = new SecurityEntry( 1, new Date() );
            attempts.put( host, entry );
        }
        else {
            entry.setAttempts( entry.getAttempts() + 1 );
            entry.setLastAttempt( new Date() );
        }

        if ( entry.getAttempts() >= MAX_ATTEMPTS_IN_HOUR ) {
            logger.warn( "Locking out host " + host + " for " + MAX_ATTEMPTS_IN_HOUR + " attempts in an hour" );
        }
    }


    private static synchronized void clearOldHosts() {
        List<String> hostsToRemove = new LinkedList<String>();
        for ( String str : attempts.keySet() ) {
            if ( attempts.get( str ).getLastAttempt().compareTo( hourAgo() ) < 0 ) {
                hostsToRemove.add( str );
            }
        }

        // avoid concurrent modification
        for ( String str : hostsToRemove ) {
            attempts.remove( str );
        }
    }

    private static synchronized boolean allowHost( String host ) {
        SecurityEntry entry = attempts.get( host );
        if ( entry == null ) { return true; } // has not attempted within the last hour (for sure)
        Date hourAgo = hourAgo();
        if ( entry.getLastAttempt().compareTo( hourAgo ) < 0 ) {
            clearOldHosts();
            return true;
        }

        return entry.getAttempts() < MAX_ATTEMPTS_IN_HOUR;
    }

    private static Date hourAgo() {
        long hourInMillis = 1000 * 3600;
        return new Date( System.currentTimeMillis() - hourInMillis );
    }

    private static class SecurityEntry {
        private int attempts;
        private Date lastAttempt;

        private SecurityEntry( int attempts, Date lastAttempt ) {
            this.attempts = attempts;
            this.lastAttempt = lastAttempt;
        }

        public int getAttempts() {
            return attempts;
        }

        public void setAttempts( int attempts ) {
            this.attempts = attempts;
        }

        public Date getLastAttempt() {
            return lastAttempt;
        }

        public void setLastAttempt( Date lastAttempt ) {
            this.lastAttempt = lastAttempt;
        }
    }
}