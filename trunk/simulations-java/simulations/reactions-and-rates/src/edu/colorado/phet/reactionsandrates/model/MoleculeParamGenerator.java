// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * MoleculeParameterGenerator
 * <p/>
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
        private MutableVector2D velocity;

        public Params( Point2D position, MutableVector2D velocity, double angularVelocity ) {
            this.velocity = velocity;
            this.position = position;
            this.angularVelocity = angularVelocity;
        }

        public Point2D getPosition() {
            return position;
        }

        public MutableVector2D getVelocity() {
            return velocity;
        }

        public double getAngularVelocity() {
            return angularVelocity;
        }
    }
}
