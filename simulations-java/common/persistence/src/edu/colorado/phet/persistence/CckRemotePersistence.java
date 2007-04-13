/**
 * Class: CckRemotePersistence
 * Package: edu.colorado.phet.persistence
 * Author: Another Guy
 * Date: Feb 5, 2004
 */
package edu.colorado.phet.persistence;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A special persistence strategy for storing CCK output. A PHP script
 * on a server is called to write the file. The URL path to where the
 * file is to be written must be supplied by the client. The path must
 * NOT end with '/', and the PHP script that write the file, remote-pst.php,
 * must be present in the directory.
 *
 * If store() is called two or more times on an instance of this class,
 * the file previously written by the instance is overwritten.
 */
public class CckRemotePersistence implements PersistenceStrategy {
    RemotePersistence remotePersistence;

    /**
     * Creates a persistence mechanism for storing a CCK circuit
     * as an XML file. The file is given a name generated from the
     * specified student ID and the current date and time.
     * @param url   The URL path to where the file is to be stored
     * @param studentID A unique identifier for the student submitting
     * the file. NOte that it will be used as part of the filename, and
     * so must not contain illegal filename characters (other than spaces,
     * which will be replaced with '-'s).
     */
    public CckRemotePersistence( String url, String studentID ) {
        String fileName = generateFileName( studentID );
        remotePersistence = new RemotePersistence( url + "/remote-pst.php", fileName );
    }

    /**
     * Writes the specified text to the file associated with this
     * instance of CckRemotePersistence. Repeated calls to this method
     * on the same instance of CckRemotePersistence cause the previous
     * file to be overwritten.
     * @param text The text to be written to the file
     */
    public void store( String text ) {
        remotePersistence.store( text );
    }

    private String generateFileName( String studentID ) {
        String patternStr = " ";
        String replaceStr = "-";
        Pattern pattern = Pattern.compile( patternStr );
        Matcher matcher = pattern.matcher( studentID );
        String fileNamePrefix = matcher.replaceAll( replaceStr );

        Calendar cal = new GregorianCalendar();
        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH );
        int day = cal.get( Calendar.DAY_OF_MONTH );
        int hour24 = cal.get( Calendar.HOUR_OF_DAY );
        int min = cal.get( Calendar.MINUTE );
        int sec = cal.get( Calendar.SECOND );
        String suffix = "-CCK-" + sec + "-" + min
                + "-" + hour24 + "-" + day + "-" + month + "-" + year;

        return fileNamePrefix + suffix + ".xml";
    }
}