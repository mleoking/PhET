/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

public class PhetProjectVersion {
    private final String major, minor, dev, revision;

    PhetProjectVersion( String major, String minor, String dev, String revision ) {
        this.major    = cleanup( major );
        this.minor    = cleanup( minor );
        this.dev      = cleanup( dev );
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

    public String formatForDev() {
        return formatForProd() + "." + dev;
    }

    public String formatForProd() {
        return major + "." + minor;
    }

    public String formatSmart() {
        if (getDevAsInt() == 0) {
            return formatForProd();
        }

        return formatForDev();
    }

    public String toString() {
        return formatForDev() + " (" + revision + ")";
    }

    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        if( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PhetProjectVersion that = (PhetProjectVersion)o;

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
        if( input == null ) {
            return "UNKNOWN";
        }

        return input;
    }

    private static int getAsInt( String number ) {
        try {
            return Integer.parseInt( number );
        }
        catch( NumberFormatException e ) {
            return -1;
        }
    }
}
