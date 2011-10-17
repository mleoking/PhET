// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.model.engine;

/**
 * This class is the "null" version of the molecule forces and motion
 * calculator.  It essentially does nothing, and is used as a default in the
 * model to avoid having null checks sprinkled throughout the code.
 *
 * @author John Blanco
 */
public class NullMoleculeForceAndMotionCalculator implements MoleculeForceAndMotionCalculator {

    double scaledEpsilon = 0;

    public void updateForcesAndMotion() {
        // Does nothing.
    }

    public double getPressure() {
        return 0;
    }

    public double getTemperature() {
        return 0;
    }

    public void setScaledEpsilon( double scaledEpsilon ) {
        this.scaledEpsilon = scaledEpsilon;
    }

    public double getScaledEpsilon() {
        return scaledEpsilon;
    }
}
