package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 21, 2008 at 8:29:16 AM
 */
public interface IMessage {
    String getHashID();

    String getComponent();

    String getEmailBody();

    String getEmailSubject();

    String getFromAddress();
}
