/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.chart;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 2:30:01 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class CCKTime {
    public double getDisplayTime( double simulationTime ) {
        return simulationTime / 100.0 * 5;
    }
}
