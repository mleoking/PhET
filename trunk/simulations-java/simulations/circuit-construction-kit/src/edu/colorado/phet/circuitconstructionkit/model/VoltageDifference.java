package edu.colorado.phet.circuitconstructionkit.model;

import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.model.Junction;

/**
 * User: Sam Reid
 * Date: Sep 27, 2006
 * Time: 9:06:29 AM
 */
public interface VoltageDifference {
    public double getVoltage( ArrayList visited, Junction at, Junction target, double volts );
}
