/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.chart;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 2:30:01 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class CCKTime {
    public static final double modelTimeScale = 5;
    public static final double viewTimeScale = modelTimeScale / 100.0;

    public static double getDisplayTime( double simulationTime ) {
        return simulationTime * viewTimeScale;
    }
}
