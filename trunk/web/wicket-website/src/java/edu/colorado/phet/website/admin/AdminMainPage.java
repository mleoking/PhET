package edu.colorado.phet.website.admin;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.Session;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.data.transfer.TransferData;
import edu.colorado.phet.website.newsletter.NewsletterSender;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.EmailUtils;
import edu.colorado.phet.website.util.SearchUtils;
import edu.colorado.phet.website.util.StringUtils;
import edu.colorado.phet.website.util.hibernate.HibernateTask;
import edu.colorado.phet.website.util.hibernate.HibernateUtils;
import edu.colorado.phet.website.util.hibernate.VoidTask;

public class AdminMainPage extends AdminPage {

    private TextField keyText;
    private TextField valueText;

    private String logTest = "";

    private static final Logger logger = Logger.getLogger( AdminMainPage.class.getName() );

    public AdminMainPage( PageParameters parameters ) {
        super( parameters );

        add( new SetStringForm( "set-string-form" ) );

        add( new Link( "debug-usercontrib" ) {
            public void onClick() {
                boolean success = TransferData.transferUsersContributions( getHibernateSession(), getServletContext() );
                if ( success ) {
                    logger.info( "transfer success" );
                }
                else {
                    logger.error( "transfer failure" );
                }
            }
        } );

        add( new Link( "debug-guide" ) {
            public void onClick() {
                boolean success = TransferData.transferTeachersGuides( getHibernateSession(), getServletContext() );
                if ( success ) {
                    logger.info( "transfer success" );
                }
                else {
                    logger.error( "transfer failure" );
                }
            }
        } );

        add( new Link( "debug-index" ) {
            public void onClick() {
                SearchUtils.reindex( PhetWicketApplication.get(), PhetLocalizer.get() );
            }
        } );

        add( new Link( "debug-email" ) {
            public void onClick() {
                try {
                    logger.info( "debugging email" );
                    EmailUtils.GeneralEmailBuilder message = new EmailUtils.GeneralEmailBuilder( "Test Email \u8FD9\u4E2A\u7537\u5B69\u5B50\u6CA1\u6709\u53EA\u72D7", WebsiteConstants.PHET_NO_REPLY_EMAIL_ADDRESS );
                    message.setBody( "This is the body. Here is some unicode: \u4E00\u4E2A\u82F9\u679C\u7EA2\u8272\u7684" );
                    message.addRecipient( "olsonsjc@gmail.com" );
                    message.addReplyTo( "phethelp@colorado.edu" );
                    EmailUtils.sendMessage( message );
                }
                catch ( MessagingException e ) {
                    e.printStackTrace();
                }
            }
        } );

        add( new Link( "debug-error" ) {
            public void onClick() {
                throw new RuntimeException( "test" );
            }
        } );

        add( new Link( "debug-sign" ) {
            public void onClick() {
                File tmpDir = new File( System.getProperty( "java.io.tmpdir" ) );
                try {
                    FileUtils.copyToDir( new File( "/data/web/htdocs/phetsims/sims/test-project/sim2_zh_TW.jar" ), tmpDir );
                    File jarFile = new File( tmpDir, "sim2_zh_TW.jar" );
                    ( new PhetJarSigner( BuildLocalProperties.getInstance() ) ).signJar( jarFile );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        add( new Link( "debug-ticks" ) {
            public void onClick() {
                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        List strings = session.createQuery( "select ts from TranslatedString as ts" ).list();
                        for ( Object o : strings ) {
                            TranslatedString string = (TranslatedString) o;
                            if ( string.getValue().contains( "''" ) ) {
                                String newVal = string.getValue().replace( "''", "'" );
                                logger.info( "String " + string.getKey() + " in translation " + string.getTranslation().getId() + " removing double single-quotes from \"" + string.getValue() + "\" to \"" + newVal + "\"" );
                                string.setValue( newVal );
                                session.update( string );
                                PhetLocalizer.get().updateCachedString( string.getTranslation(), string.getKey(), string.getValue() );
                            }
                        }
                        return true;
                    }
                } );
            }
        } );

        add( new Link( "debug-logging" ) {
            @Override
            public void onClick() {
                StringWriter writer = new StringWriter();
                WriterAppender appender = new WriterAppender( new PatternLayout( "%d{DATE} %5p %25c{1} - %m%n" ), writer );
                Logger log4jLogger = Logger.getLogger( AdminMainPage.class );
                log4jLogger.addAppender( appender );

                java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger( AdminMainPage.class.getName() );
                logger.info( "log4j info" );
                julLogger.info( "jul info" );
                logger.debug( "log4j debug" );
                julLogger.fine( "jul fine" );

                log4jLogger.removeAppender( appender );

                System.out.println( "Logged:\n" + writer.toString() );
            }
        } );

        add( new Link( "debug-newsletter" ) {
            @Override
            public void onClick() {
                final List<PhetUser> users = new LinkedList<PhetUser>();
                HibernateUtils.wrapCatchTransaction( getHibernateSession(), new VoidTask() {
                    public Void run( Session session ) {
                        users.add( (PhetUser) session.createQuery( "select u from PhetUser as u where u.email = :email" ).setString( "email", "olsonsjc@gmail.com" ).uniqueResult() );

                        // TODO: add in all users.

                        for ( PhetUser user : users ) {
                            // if user doesn't have a good confirmation key for unsubscribing, generate one
                            if ( user.getConfirmationKey() == null ) {
                                user.setConfirmationKey( PhetUser.generateConfirmationKey() );
                                session.update( user );
                            }
                        }
                        return null;
                    }
                } );
                new NewsletterSender().sendNewsletters( users );
            }
        } );

        add( new RawLabel( "logTest", new Model<String>( logTest ) ) );
    }

    public final class SetStringForm extends Form {
        private static final long serialVersionUID = 1L;

        private final ValueMap properties = new ValueMap();

        public SetStringForm( final String id ) {
            super( id );

            add( keyText = new StringTextField( "key", new PropertyModel( properties, "key" ) ) );
            add( valueText = new StringTextField( "value", new PropertyModel( properties, "value" ) ) );

            // don't turn <'s and other characters into HTML/XML entities!!!
            valueText.setEscapeModelStrings( false );
        }

        public final void onSubmit() {
            // TODO: important before deploy: add authorization or remove (for specific translation)
            String key = keyText.getModelObject().toString();
            String value = valueText.getModelObject().toString();
            logger.info( "Submitted new string: " + key + " = " + value );
            StringUtils.setEnglishString( getHibernateSession(), key, value );
        }
    }

}
