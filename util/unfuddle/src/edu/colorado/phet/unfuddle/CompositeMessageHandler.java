package edu.colorado.phet.unfuddle;

import java.util.ArrayList;

import javax.mail.MessagingException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:58:42 PM
 * test comment
 */
public class CompositeMessageHandler implements IMessageHandler {
    private ArrayList list = new ArrayList();

    public void addMessageHandler( IMessageHandler m ) {
        list.add( m );
    }

    public void handleMessage( IMessage m ) throws MessagingException {
        for ( int i = 0; i < list.size(); i++ ) {
            IMessageHandler messageHandler = (IMessageHandler) list.get( i );
            messageHandler.handleMessage( m );
        }
    }
}
