package edu.colorado.phet.unfuddle;

import javax.mail.MessagingException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 8:27:23 AM
 */
public interface IMessageHandler {
    void handleMessage( IMessage m ) throws MessagingException;
}
