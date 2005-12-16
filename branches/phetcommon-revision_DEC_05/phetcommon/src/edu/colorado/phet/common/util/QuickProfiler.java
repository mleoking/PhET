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
 * Utility class for timing activity:
 * <p/>
 * Sample usage:
 * <p/>
 * QuickTimer paintTime=new QuickTimer();
 * paintComponent(g2d);
 * System.out.println("paintTime="+paintTime);
 */

public class QuickProfiler {
    private long startTime;

    public QuickProfiler() {
        this.startTime = System.currentTimeMillis();
    }

    public long getTime() {
        long now = System.currentTimeMillis();
        return now - startTime;
    }

    public String toString() {
        return String.valueOf( getTime() );
    }
}