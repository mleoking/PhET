/**
 * Class: Uranium2235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class Uranium2235 extends Nucleus {
    public Uranium2235(Point2D.Double position) {
        super(position, 145, 92, null);
    }

    public DecayProducts decay() {
        // Create two new nucleuses
        int n1Protons = 60;
        int n1Neutrons = 40;
        double theta = Math.random() * Math.PI * 2;
        double separation = 100;
        double dx = separation * Math.cos(theta);
        double dy = separation * Math.sin(theta);
        Nucleus n1 = new Nucleus(new Point2D.Double(this.getLocation().getX() - dx,
                this.getLocation().getY() - dy),
                n1Protons, n1Neutrons, this.getPotentialProfile());
        Nucleus n2 = new Nucleus(new Point2D.Double(this.getLocation().getX() + dx,
                this.getLocation().getY() + dy),
                this.getNumProtons() - n1Protons,
                this.getNumNeutrons() - n1Neutrons, this.getPotentialProfile());
        DecayProducts products = new DecayProducts(this, n1, n2);
        return products;
    }
}
