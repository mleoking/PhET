/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

public class PhetProjectVersion {
    private final String major, minor, dev, revision;

    PhetProjectVersion( String major, String minor, String dev, String revision ) {
        this.major    = cleanup(major);
        this.minor    = cleanup(minor);
        this.dev      = cleanup(dev);
        this.revision = cleanup(revision);
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getDev() {
        return dev;
    }

    public String getRevision() {
        return revision;
    }

    public String formatForDev() {
        return formatForProd() + "." + dev;
    }

    public String formatForProd() {
        return major + "." + minor;
    }

    public String toString() {
        return formatForDev() + "." + revision;
    }

    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        if( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PhetProjectVersion that = (PhetProjectVersion)o;

        if( !dev.equals( that.dev ) ) {
            return false;
        }
        if( !major.equals( that.major ) ) {
            return false;
        }
        if( !minor.equals( that.minor ) ) {
            return false;
        }
        if( !revision.equals( that.revision ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = major.hashCode();
        result = 31 * result + minor.hashCode();
        result = 31 * result + dev.hashCode();
        result = 31 * result + revision.hashCode();
        return result;
    }

    private static String cleanup(String input) {
        if (input == null) return "UNKNOWN";

        return input;
    }
}
