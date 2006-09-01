/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;

import java.awt.geom.Point2D;

/**
 * CollisionSpec
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeMoleculeCollisionSpec {

        private Vector2D loa;
        private Point2D.Double collisionPt;
        private SimpleMolecule moleculeA;
        private SimpleMolecule moleculeB;

        public MoleculeMoleculeCollisionSpec( Vector2D loa,
                                              Point2D.Double collisionPt,
                                              SimpleMolecule moleculeA,
                                              SimpleMolecule moleculeB ) {
            this.moleculeB = moleculeB;
            this.moleculeA = moleculeA;
            this.loa = loa;
            this.collisionPt = collisionPt;
        }

        public Vector2D getLoa() {
            return loa;
        }

        public Point2D.Double getCollisionPt() {
            return collisionPt;
        }

        public SimpleMolecule getMoleculeA() {
            return moleculeA;
        }

        public SimpleMolecule getMoleculeB() {
            return moleculeB;
        }
    }

