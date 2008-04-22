package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:38:50 PM
 */
public class PrintMessageHandler implements IMessageHandler {
    public void handleMessage( IMessage m ) {
        System.out.println( "<<<<<<<<\n" + m + "\n>>>>>>>" );
    }
}
