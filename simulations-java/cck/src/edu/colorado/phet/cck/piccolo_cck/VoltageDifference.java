package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.Junction;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 27, 2006
 * Time: 9:06:29 AM
 * Copyright (c) Sep 27, 2006 by Sam Reid
 */
public interface VoltageDifference {
    public double getVoltage( ArrayList visited, Junction at, Junction target, double volts );
}
