package edu.colorado.phet.unfuddle;

import java.util.ArrayList;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:58:42 PM
 */
public class CompositeMessageHandler implements MessageHandler {
    private ArrayList list = new ArrayList();

    public void addMessageHandler( MessageHandler m ) {
        list.add( m );
    }

    public void handleMessage( Message m ) {
        for ( int i = 0; i < list.size(); i++ ) {
            MessageHandler messageHandler = (MessageHandler) list.get( i );
            messageHandler.handleMessage( m );
        }
    }
}
