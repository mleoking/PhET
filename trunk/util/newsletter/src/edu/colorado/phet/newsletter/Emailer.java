package edu.colorado.phet.newsletter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.notification.NotificationHandler;

/**
 * Sends email + attachments to a list of addresses.  See example newsletter-args.properties file.
 * This requires the website code and build tools code to be in the path, so is commonly run within the ide.
 *
 * @author Sam Reid
 * @author Jon Olson
 */
public class Emailer {

    public static void main( String[] args ) throws IOException, MessagingException {
        Properties properties = new Properties();
        properties.load( new FileInputStream( new File( "newsletter-args.properties" ) ) );//TODO: assumes run from the root of the newsletter directory.
        String body = FileUtils.loadFileAsString( new File( properties.getProperty( "bodyFile" ) ) );//TODO: do we have to specify encoding other than UTF-8?

        ArrayList<String> allEmails = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer( FileUtils.loadFileAsString( new File( properties.getProperty( "toAddressFile" ) ) ), "\n" );
        while ( st.hasMoreTokens() ) {
            allEmails.add( st.nextToken() );
        }
        for ( String emailAddress : allEmails ) {
            ArrayList<String> sendTo = new ArrayList<String>();
            sendTo.add( emailAddress );
            NotificationHandler.sendMessage( properties.getProperty( "mailHost" ),
                                             properties.getProperty( "mailUser" ),
                                             properties.getProperty( "mailPassword" ),
                                             sendTo,
                                             body,
                                             properties.getProperty( "fromAddress" ),
                                             properties.getProperty( "subject" ),
                                             loadAdditionalParts( properties.getProperty( "additionalParts" ) )
            );
            System.out.println( "emailAddress = " + emailAddress );
        }
    }

    private static ArrayList<BodyPart> loadAdditionalParts( String property ) throws MessagingException {
        ArrayList<BodyPart> bodyParts = new ArrayList<BodyPart>();

        StringTokenizer st = new StringTokenizer( property, "," );
        while ( st.hasMoreTokens() ) {
            String next = st.nextToken().trim();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource( next );
            messageBodyPart.setDataHandler( new DataHandler( source ) );
            messageBodyPart.setFileName( next );

            bodyParts.add( messageBodyPart );
        }
        return bodyParts;
    }
}
