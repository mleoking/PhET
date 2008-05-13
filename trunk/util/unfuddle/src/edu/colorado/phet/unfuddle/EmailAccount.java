package edu.colorado.phet.unfuddle;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// Send a simple, single part, text/plain e-mail
public class EmailAccount {

    public static void sendEmail( String from, String[] to, String host, String emailBody, String emailSubject, final String user, final String password ) throws MessagingException {
        // Create properties, get Session
        Properties props = new Properties();

        // If using static Transport.send(),
        // need to specify which host to send it to
        props.put( "mail.smtp.host", host );
        // To see what is going on behind the scene
        props.put( "mail.debug", "true" );

        props.put( "mail.smtp.auth", "true" );
        props.put( "password", password );
        props.put( "mail.smtp.user", user );
        Session session = Session.getInstance( props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( user, password );
            }
        } );


        try {
            // Instantiate a message
            Message msg = new MimeMessage( session );

            //Set message attributes
            msg.setFrom( new InternetAddress( from ) );
            InternetAddress[] toAddresses = new InternetAddress[to.length];
            for ( int i = 0; i < toAddresses.length; i++ ) {
                toAddresses[i] = new InternetAddress( to[i] );
            }
//            InternetAddress[] address = {new InternetAddress( to )};
            msg.setRecipients( Message.RecipientType.TO, toAddresses );
            msg.setSubject( emailSubject );
            msg.setSentDate( new Date() );

            // Set message content
            msg.setText( emailBody );

            //Send the message
            Transport.send( msg );
        }
        catch( MessagingException mex ) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
            throw mex;
        }
        System.out.println( "Sent email to: " + Arrays.asList( to ) );
    }

    /**
     * Sends a test email.
     */
    public static void main( String[] args ) throws MessagingException {
        String from = args[0];
        String to = args[1];
        String host = args[2];
        sendEmail( from, new String[]{to}, host, "Hello self email body", "Hello self subject", args[3], args[4] );
    }
}//End of class