/**
 * Class: ConstantFunction
 * Package: edu.colorado.phet.emf.model
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.emf.model;

import edu.colorado.phet.waves.model.EMFPeriodicFunction;

public class ConstantFunction implements EMFPeriodicFunction {

    public float valueAtTime( float frequency, float maxAmplitude, float time ) {
        return maxAmplitude;
    }

}
