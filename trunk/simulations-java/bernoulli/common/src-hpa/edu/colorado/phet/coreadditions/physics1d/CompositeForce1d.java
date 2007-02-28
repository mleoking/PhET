/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.physics1d;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 17, 2003
 * Time: 2:28:31 PM
 * Copyright (c) Jun 17, 2003 by Sam Reid
 */
public class CompositeForce1d implements Force1d {
    ArrayList forces = new ArrayList();

    public void addForce(Force1d force) {
        forces.add(force);
    }

    public double getForce(Particle1d p) {
        double sum = 0;
        for (int i = 0; i < forces.size(); i++) {
            Force1d f = (Force1d) forces.get(i);
            sum += f.getForce(p);
        }
        return sum;
    }

}
