/**
 * Class: MessageFormatter
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 21, 2003
 */
package edu.colorado.phet.radiowaves.coreadditions;

/**
 * This class has a single static method that turns Java
 * strings into HTML that is Java-printable. The primary
 * use of this is to put line breaks in messages with '\n'
 * characters.
 */
public class MessageFormatter {

    public static String format( String msg ) {
        StringBuffer outString = new StringBuffer( "<html>" );
        int lastIdx = 0;
        for( int nextIdx = msg.indexOf( "\n", lastIdx );
             nextIdx != -1;
             nextIdx = msg.indexOf( "\n", lastIdx ) ) {
            outString.append( msg.substring( lastIdx, nextIdx ) );
            if( nextIdx < msg.length() ) {
                outString.append( "<br>" );
            }
            lastIdx = nextIdx + 1;
        }
        outString.append( msg.substring( lastIdx, msg.length() ) );
        outString.append( "</html>" );
        return outString.toString();
    }
}
