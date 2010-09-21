package edu.colorado.phet.unfuddle;

import javax.mail.MessagingException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 8:27:23 AM
 */
public interface IMessageHandler {
    /**
     * Handles the specified message
     * @param m the message to handle
     * @return a human-readable string signifying the action taken, if any
     * @throws MessagingException
     */
    String handleMessage( IMessage m ) throws MessagingException;
}
