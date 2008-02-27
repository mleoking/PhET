package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.mail.MessagingException;


/**
 * Created by: Sam
 * Feb 21, 2008 at 8:28:28 AM
 */
public class IgnoreDuplicatesMessageHandler implements MessageHandler {
    private MessageHandler target;
    private File file;

    public IgnoreDuplicatesMessageHandler( MessageHandler target, File file ) {
        this.target = target;
        this.file = file;
    }

    public void handleMessage( Message m ) throws MessagingException {
        if ( !alreadyHandled( m ) ) {
            target.handleMessage( m );
            setHandled( m );
        }
    }

    private void setHandled( Message m ) {
        try {
            String s = FileUtils.loadFileAsString( file );
            s += "\n" + m.getHashID();
            FileUtils.writeString( file, s );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private boolean alreadyHandled( Message m ) {
        try {
            ArrayList h = getHandledList();
            return h.contains( m.getHashID() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList getHandledList() throws IOException {
        String s = FileUtils.loadFileAsString( file );
        StringTokenizer st = new StringTokenizer( s, " \n" );
        ArrayList handled = new ArrayList();
        while ( st.hasMoreTokens() ) {
            handled.add( st.nextToken().trim() );
        }
        return handled;
    }

}
