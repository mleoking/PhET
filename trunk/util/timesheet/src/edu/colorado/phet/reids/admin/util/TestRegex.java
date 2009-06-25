package edu.colorado.phet.reids.admin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by: Sam
 * Feb 15, 2008 at 8:51:55 AM
 */
public class TestRegex {
    public static void main( String[] args ) {
        String string = "timecard,worked on 12-31-2007,0:30:00,9:16:00,,,,,,,,,,,";
        ConvertExcelCSV.parseNotes( string );
    }


}
