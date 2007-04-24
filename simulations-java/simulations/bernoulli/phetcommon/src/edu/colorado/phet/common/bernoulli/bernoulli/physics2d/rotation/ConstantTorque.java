/*, 2003.*/
package edu.colorado.phet.common.bernoulli.bernoulli.physics2d.rotation;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 4:11:01 PM
 *
 */
public class ConstantTorque implements Torque {
    private double t;

    public ConstantTorque(double t) {
        this.t = t;
    }

    public double getTorque() {
        return t;
    }
}
