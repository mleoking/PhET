/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.ion;

/**
 * IonProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
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
