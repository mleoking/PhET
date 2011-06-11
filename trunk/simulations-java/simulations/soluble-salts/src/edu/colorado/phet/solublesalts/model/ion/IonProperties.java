// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * IonProperties
 *
 * @author Ron LeMaster
 */
public class IonProperties {
    private double mass;
    private double charge;
    private double radius;

    public IonProperties( double mass, double charge, double radius ) {
        this.mass = mass;
        this.charge = charge;
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }

    public double getCharge() {
        return charge;
    }

    public double getRadius() {
        return radius;
    }
}
