package edu.colorado.phet.unfuddle;

import java.util.ArrayList;

import javax.mail.MessagingException;

public class CompositeMessageHandler implements IMessageHandler {
    private ArrayList<IMessageHandler> list = new ArrayList<IMessageHandler>();

    public void addMessageHandler( IMessageHandler m ) {
        list.add( m );
    }

    public void handleMessage( IMessage m ) throws MessagingException {
        for ( IMessageHandler aList : list ) {
            aList.handleMessage( m );
        }
    }
}
