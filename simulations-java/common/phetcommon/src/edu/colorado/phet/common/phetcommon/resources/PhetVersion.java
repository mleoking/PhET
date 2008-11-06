/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.resources;

import java.text.MessageFormat;

/**
 * PhetVersionInfo encapsulates a simulation's version information.
 */
public class PhetVersion {

    // title bar format for public releases, major.minor
    private static final String FORMAT_MAJOR_MINOR = "{0}.{1}";
    // title bar format for development releases, major.minor.dev
    private static final String FORMAT_MAJOR_MINOR_DEV = "{0}.{1}.{2}";
    // About dialog format, major.minor.dev (revision)
    private static final String FORMAT_MAJOR_MINOR_DEV_REVISION = "{0}.{1}.{2} ({3})";

    private final String major, minor, dev, revision;

    public PhetVersion( String major, String minor, String dev, String revision ) {
        this.major = cleanup( major );
        this.minor = cleanup( minor );
        this.dev = cleanup( dev );
        this.revision = cleanup( revision );
    }

    public String getMajor() {
        return major;
    }

    public int getMajorAsInt() {
        return getAsInt( getMajor() );
    }

    public String getMinor() {
        return minor;
    }

    public int getMinorAsInt() {
        return getAsInt( getMinor() );
    }

    public String getDev() {
        return dev;
    }

    public int getDevAsInt() {
        return getAsInt( getDev() );
    }

    public String getRevision() {
        return revision;
    }

    public int getRevisionAsInt() {
        return getAsInt( getRevision() );
    }
    
    /*
     * A development version has a non-zero dev number.
     */
    private boolean isDevVersion() {
        return getDevAsInt() != 0;
    }

    /**
     * Formats the version information for use in the application's title bar.
     * A public release will have a dev number that is all zeros; anything else is a development release.
     * For public releases, we should only the major and minor version numbers.
     * Development release show major, minor and dev version numbers.
     *
     * @return String
     */
    public String formatForTitleBar() {
        return isDevVersion() ? formatMajorMinorDev() : formatMajorMinor();
    }

    public String formatMajorMinorDevRevision() {
        Object[] args = {major, minor, dev, revision};
        return MessageFormat.format( FORMAT_MAJOR_MINOR_DEV_REVISION, args );
    }
    
    public String formatMajorMinorDev() {
        Object[] args = {major, minor, dev};
        return MessageFormat.format( FORMAT_MAJOR_MINOR_DEV, args );
    }
    
    public String formatMajorMinor() {
        Object[] args = {major, minor};
        return MessageFormat.format( FORMAT_MAJOR_MINOR, args );
    }

    public String toString() {
        return formatMajorMinorDevRevision();
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PhetVersion that = (PhetVersion) o;

        return dev.equals( that.dev ) && major.equals( that.major ) &&
               minor.equals( that.minor ) && revision.equals( that.revision );
    }

    public int hashCode() {
        int result;
        result = major.hashCode();
        result = 31 * result + minor.hashCode();
        result = 31 * result + dev.hashCode();
        result = 31 * result + revision.hashCode();
        return result;
    }

    private static String cleanup( String input ) {
        String output = input;
        if ( input == null ) {
            output = "?";
        }
        return output;
    }

    private static int getAsInt( String number ) {
        int i = 0;
        try {
            i = Integer.parseInt( number );
        }
        catch( NumberFormatException e ) {
            e.printStackTrace();
            i = -1;
        }
        return i;
    }

    public boolean isGreaterThan( PhetVersion version ) {
        //todo: should this use major/minor/dev to determine ordering?
        return getRevisionAsInt() > version.getRevisionAsInt();
    }
}
