/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

/**
 * Utility class for timing activity.
 */

public class QuickProfiler {
    private long startTime;
    private String name;

    public QuickProfiler( String name ) {
        this.name = name;
        this.startTime = System.currentTimeMillis();
    }

    public long getTime() {
        long now = System.currentTimeMillis();
        return now - startTime;
    }

    public void println() {
        System.out.println( toString() );
    }

    public String toString() {
        return name + ": " + getTime() + " (ms)";
    }
}