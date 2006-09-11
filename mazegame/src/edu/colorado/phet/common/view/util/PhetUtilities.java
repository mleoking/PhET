/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

/**
 * PhetModelUtilities
 * <p/>
 * Static methods of general utility
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetUtilities {
    // Operating Systems
    public static final int OS_WINDOWS = 0;
    public static final int OS_MACINTOSH = 1;
    public static final int OS_OTHER = 2;

    /**
     * Gets the operating system type.
     *
     * @return OS_WINDOWS, OS_MACINTOSH, or OS_OTHER
     */
    public static int getOperatingSystem() {

        // Get the operating system name.
        String osName = "";
        try {
            osName = System.getProperty( "os.name" ).toLowerCase();
        }
        catch( Throwable t ) {
            t.printStackTrace();
        }

        // Convert to one of the operating system constants.
        int os = OS_OTHER;
        if( osName.indexOf( "windows" ) >= 0 ) {
            os = OS_WINDOWS;
        }
        else if( osName.indexOf( "mac" ) >= 0 ) {
            os = OS_MACINTOSH;
        }

        return os;
    }

    /**
     * Determines whether we're running on a Macintosh.
     *
     * @return true or false
     */
    public static boolean isMacintosh() {
        return getOperatingSystem() == OS_MACINTOSH;
    }
}
