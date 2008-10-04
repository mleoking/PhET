/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.resources;

import java.text.MessageFormat;

/**
 * PhetVersionInfo encapsulates a simulation's version information.
 */
public class PhetVersionInfo {

    // title bar format for public releases, major.minor
    private static final String TITLEBAR_FORMAT_PUBLIC = "{0}.{1}";
    // title bar format for development releases, major.minor.dev
    private static final String TITLEBAR_FORMAT_DEV = "{0}.{1}.{2}";
    // About dialog format, major.minor.dev (revision)
    private static final String ABOUT_DIALOG_FORMAT = "{0}.{1}.{2} ({3})";

    private final String major, minor, dev, revision;

    public PhetVersionInfo( String major, String minor, String dev, String revision ) {
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

    /**
     * Formats the version information for use in the application's title bar.
     * A public release will have a dev number that is all zeros; anything else is a development release.
     * For public releases, we should only the major and minor version numbers.
     * Development release show major, minor and dev version numbers.
     *
     * @return String
     */
    public String formatForTitleBar() {
        Object[] args = {major, minor, dev};
        String pattern = ( getDevAsInt() == 0 ) ? TITLEBAR_FORMAT_PUBLIC : TITLEBAR_FORMAT_DEV;
        return MessageFormat.format( pattern, args );
    }

    /**
     * Formats the version information for use in the Help>About dialog.
     * This format shows the complete version information in all circumstances.
     *
     * @return String
     */
    public String formatForAboutDialog() {
        Object[] args = {major, minor, dev, revision};
        return MessageFormat.format( ABOUT_DIALOG_FORMAT, args );
    }

    public String toString() {
        return formatForAboutDialog();
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PhetVersionInfo that = (PhetVersionInfo) o;

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
            i = -1;
        }
        return i;
    }

    public boolean isGreaterThan( PhetVersionInfo version ) {
        //todo: should this use major/minor/dev to determine ordering?
        return getRevisionAsInt()>version.getRevisionAsInt();
    }
}
