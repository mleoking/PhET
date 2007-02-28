/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * MoleculeParameterGenerator
 * <p>
 * Generates kinematic parameters for a Molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface MoleculeParamGenerator {

    Params generate();

    public static class Params {
        private Point2D position;
        private double angularVelocity;
        private Vector2D velocity;

        public Params( Point2D position, Vector2D velocity, double angularVelocity ) {
            this.velocity = velocity;
            this.position = position;
            this.angularVelocity = angularVelocity;
        }

        public Point2D getPosition() {
            return position;
        }

        public Vector2D getVelocity() {
            return velocity;
        }

        public double getAngularVelocity() {
            return angularVelocity;
        }
    }
}
