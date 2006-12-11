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
import edu.colorado.phet.molecularreactions.model.CompositeMolecule;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;

import java.awt.geom.Point2D;

/**
 * CollisionSpec
 * <p/>
 * A data structure that holds the important parameters of A molecule-molecule
 * collision.
 * <p>
 * The spec contains references to the two simple molecules involved in the collision.
 * Note that if there is A composite molecule involved in the collision, one of its
 * component simple molecules is the one hitting the other molecule in the collision.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeMoleculeCollisionSpec {

    private Vector2D loa;
    private Point2D.Double collisionPt;
    private SimpleMolecule moleculeA;
    private SimpleMolecule moleculeB;
    private SimpleMolecule freeMolecule;
    private CompositeMolecule compositeMolecule;

    public MoleculeMoleculeCollisionSpec( Vector2D loa,
                                          Point2D.Double collisionPt,
                                          SimpleMolecule moleculeA,
                                          SimpleMolecule moleculeB ) {
        this.moleculeB = moleculeB;
        this.moleculeA = moleculeA;
        this.loa = loa;
        this.collisionPt = collisionPt;

        if( !moleculeA.isPartOfComposite() ) {
            freeMolecule = moleculeA;
            if( moleculeB.isPartOfComposite()) {
                compositeMolecule = (CompositeMolecule)moleculeB.getFullMolecule();
            }
            else {
                compositeMolecule = null;
            }
        }
        else if( !moleculeB.isPartOfComposite() ) {
            freeMolecule = moleculeB;
            if( moleculeA.isPartOfComposite() ) {
                compositeMolecule = (CompositeMolecule)moleculeA.getFullMolecule();
            }
            else {
                compositeMolecule = null;
            }
        }
    }

    public Vector2D getLoa() {
        return loa;
    }

    public Point2D.Double getCollisionPt() {
        return collisionPt;
    }

    /**
     * One of the simple molecules involved in the collision
     * @return
     */
    public SimpleMolecule getSimpleMoleculeA() {
        return moleculeA;
    }

    /**
     * One of the simple molecules involved in the collision
     * @return
     */
    public SimpleMolecule getSimpleMoleculeB() {
        return moleculeB;
    }

    public SimpleMolecule getFreeMolecule() {
        return freeMolecule;
    }

    public CompositeMolecule getCompositeMolecule() {
        return compositeMolecule;
    }
}

