package edu.colorado.phet.website.newsletter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.apache.log4j.Logger;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.util.EmailUtils;
import edu.colorado.phet.website.util.PageContext;

public class NewsletterSender {

    // TODO: add dev flag

    private String rawBody;
    private String subject;
    private String fromAddress;
    private String replyTo;

//    private List<File> images = new LinkedList<File>();

    private static boolean sending = false;
    private static final Object lock = new Object();

    private static final Logger logger = Logger.getLogger( NewsletterSender.class );

    public NewsletterSender() {
        try {
            File newsletterPropertiesFile = PhetWicketApplication.get().getWebsiteProperties().getNewsletterFile();

            Properties properties = new Properties();
            FileInputStream in = new FileInputStream( newsletterPropertiesFile );
            try {
                properties.load( in );
            }
            finally {
                in.close();
            }
            subject = properties.getProperty( "subject" );
            fromAddress = properties.getProperty( "fromAddress" );
            replyTo = properties.getProperty( "replyTo" );
            rawBody = FileUtils.loadFileAsString( new File( properties.getProperty( "bodyFile" ) ) );

//            for ( String imageFilename : properties.getProperty( "images" ).split( " " ) ) {
//                File imageFile = new File( imageFilename );
//                if ( !imageFile.exists() ) {
//                    throw new FileNotFoundException( "image file not found: " + imageFilename );
//                }
//                images.add( imageFile );
//            }
        }
        catch ( FileNotFoundException e ) {
            logger.error( "message prep error: ", e );
        }
        catch ( IOException e ) {
            logger.error( "message prep error: ", e );
        }
    }

    public boolean sendNewsletters( List<PhetUser> users ) {
        // sort all of them
        Collections.sort( users, new Comparator<PhetUser>() {
            public int compare( PhetUser a, PhetUser b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );
        synchronized ( lock ) {
            if ( sending ) {
                return false; // exit immediately. don't double-send
            }
            sending = true;
        }
        for ( PhetUser user : users ) {
            if ( user.isConfirmed() && user.isReceiveEmail() && PhetUser.isValidEmail( user.getEmail() ) ) {
                sendNewsletter( user );
            }
        }
        synchronized ( lock ) {
            sending = false;
        }
        return true;
    }

    private boolean sendNewsletter( PhetUser user ) { // TODO: improve signature
        logger.info( "sending newsletter to " + user.getEmail() );
        try {
            EmailUtils.GeneralEmailBuilder message = new EmailUtils.GeneralEmailBuilder( subject, fromAddress );
            String body = rawBody;
            body = FileUtils.replaceAll( body, "@FOOTER@", NewsletterUtils.getUnsubscribeText( PageContext.getNewDefaultContext(), user.getConfirmationKey() ) );
            body = FileUtils.replaceAll( body, "@NAME@", user.getName() );
            message.setBody( body );
            message.addRecipient( user.getEmail().trim() );
            message.addReplyTo( replyTo );
//            for ( final File imageFile : images ) {
//                message.addBodyPart( new MimeBodyPart() {{
//                    setDataHandler( new DataHandler( new FileDataSource( imageFile ) ) );
//                    setHeader( "Content-ID", "<" + imageFile.getName() + ">" );
//                }} );
//            }
            return EmailUtils.sendMessage( message );
        }
        catch ( MessagingException e ) {
            logger.warn( "message send error: ", e );
            return false;
        }
    }
}
