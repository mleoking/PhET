package edu.colorado.phet.unfuddle;

import java.util.ArrayList;

import javax.mail.MessagingException;

public class CompositeMessageHandler implements IMessageHandler {
    private ArrayList<IMessageHandler> list = new ArrayList<IMessageHandler>();

    public void addMessageHandler( IMessageHandler m ) {
        list.add( m );
    }

    public String handleMessage( IMessage m ) throws MessagingException {
        String s="";
        for ( IMessageHandler aList : list ) {
            s+=aList.handleMessage( m )+" ";
        }
        return s;
    }
}
