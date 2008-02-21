package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.colorado.phet.unfuddle.mail.EmailAccount;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:58:28 PM
 */
public class EmailHandler implements MessageHandler {
    private String fromAddress;
    private String server;
    private boolean sendMail;

    public EmailHandler( String fromAddress, String server ) {
        this( fromAddress, server, true );
    }

    public EmailHandler( String fromAddress, String server, boolean sendMail ) {
        this.fromAddress = fromAddress;
        this.server = server;
        this.sendMail = sendMail;
    }

    public void handleMessage( Message m ) {
        String[] to = new String[0];
        try {
            to = getTo( m.getComponent() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        if ( sendMail && to.length > 0 ) {
            EmailAccount.sendEmail( fromAddress, to, server, m.getEmailBody(), m.getEmailSubject() );
        }
        else {
            System.out.println( "email server would have sent message m: " + m.getEmailSubject() + " to " + Arrays.asList( to ) );
        }
    }

    private String[] getTo( String component ) throws IOException {
        Properties p = new Properties();
        p.load( new FileInputStream( new File( "C:\\reid\\phet\\svn\\trunk\\team\\reids\\unfuddle\\data\\email.properties" ) ) );
        String c = p.getProperty( component );
        c = c == null ? "" : c;
        StringTokenizer st = new StringTokenizer( c, ", " );
        ArrayList s = new ArrayList();
        while ( st.hasMoreTokens() ) {
            s.add( st.nextToken() );
        }
        return (String[]) s.toArray( new String[0] );
    }
}
