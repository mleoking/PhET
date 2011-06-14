package edu.colorado.phet.unfuddle;

import java.util.ArrayList;

import javax.mail.MessagingException;

public class CompositeMessageHandler implements IMessageHandler {
    private ArrayList<IMessageHandler> list = new ArrayList<IMessageHandler>();

    public void addMessageHandler( IMessageHandler m ) {
        list.add( m );
    }

    public String handleMessage( IMessage m ) throws MessagingException {
        String s = "";
        for ( int i = 0; i < list.size(); i++ ) {
            IMessageHandler handler = list.get( i );
            s += "ACTION[" + i + "]: " + handler.handleMessage( m );
            if ( i < list.size() - 1 ) {
                s += ", ";
            }
        }
        return s;
    }
}
