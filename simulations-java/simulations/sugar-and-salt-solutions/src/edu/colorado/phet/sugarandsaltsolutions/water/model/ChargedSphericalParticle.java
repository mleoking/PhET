// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.Color;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

import static java.awt.Color.white;

/**
 * Charged particles (ions or atoms) for use in the water model for coulomb repulsions
 *
 * @author Sam Reid
 */
public class ChargedSphericalParticle extends SphericalParticle {

    //The charge of the atom
    private double charge;

    public ChargedSphericalParticle( double radiusInPM, Color chargeColor, Color atomColor, double charge ) {
        super( radiusInPM, chargeColor, atomColor );
        this.charge = charge;
    }

    public double getCharge() {
        return charge;
    }

    public static class Hydrogen extends ChargedSphericalParticle {
        public Hydrogen() {
            //See this table for the charge, using TIP3P model: http://en.wikipedia.org/wiki/Water_model
            super( SphericalParticle.Hydrogen.RADIUS_PICOMETERS, SphericalParticle.POSITIVE_COLOR, white, +0.417 );
        }
    }

    public static class Oxygen extends ChargedSphericalParticle {
        public Oxygen() {
            //See this table for the charge, using TIP3P model: http://en.wikipedia.org/wiki/Water_model
            super( SphericalParticle.Oxygen.RADIUS_PICOMETERS, NEUTRAL_COLOR, Color.red, -0.834 );
        }
    }
}
