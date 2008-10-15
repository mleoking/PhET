/**
 * Class: ConstantFunction
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.microwaves.model;

import edu.colorado.phet.microwaves.model.waves.PeriodicFunction;

public class ConstantFunction implements PeriodicFunction {

    public float valueAtTime( float frequency, float maxAmplitude, float time ) {
        return maxAmplitude;
    }

}
