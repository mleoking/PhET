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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.collision.SphereBoxExpert;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionAgent;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeBoxCollisionAgent;
import edu.colorado.phet.molecularreactions.model.collision.SpringCollision;

import java.util.List;

/**
 * CollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CollisionAgent implements ModelElement {

    private MoleculeMoleculeCollisionAgent moleculeMoleculeCollisionAgent;
    private MoleculeBoxCollisionAgent moleculeBoxCollisionAgent;
    private MRModel model;

    public CollisionAgent( MRModel model ) {
        this.model = model;

        // todo: DEBUG. move elsewhere
        SpringCollision.Spring spring = new SpringCollision.Spring( 1, 50 );
        SpringCollision springCollision = new SpringCollision( spring );

        moleculeMoleculeCollisionAgent = new MoleculeMoleculeCollisionAgent( model, springCollision );
        moleculeBoxCollisionAgent = new MoleculeBoxCollisionAgent();
    }

    public void stepInTime( double dt ) {
        List modelElements = model.getModelElements();
        for( int i = modelElements.size() - 1; i >= 0; i-- ) {
            Object o = modelElements.get( i );

            // Check every Molecule that is not part of a larger CompositeMolecule
            if( o instanceof Molecule ) {
                Molecule moleculeA = (Molecule)o;
                if( !moleculeA.isPartOfComposite() ) {
                    for( int j = modelElements.size() - 1; j >= 0; j-- ) {
                        Object o2 = modelElements.get( j );
                        if( o2 instanceof Molecule ) {
                            Molecule moleculeB = (Molecule)o2;
                            if( !moleculeB.isPartOfComposite() && moleculeA != moleculeB ) {
                                moleculeMoleculeCollisionAgent.detectAndDoCollision( model,
                                                                                     moleculeA,
                                                                                     moleculeB );
                            }
                        }
                    }

                    // Check for collisions between the molecule and the box. Note that we have to
                    // do this several times so that cases in which a molecule
                    // hits in the corner of the box will be handled properly. If we don't do this,
                    // molecules can escape from the box
                    boolean collided;
                    collided = moleculeBoxCollisionAgent.detectAndDoCollision( moleculeA, model.getBox() );
                    if( collided ) {
                        moleculeA.stepInTime( dt );
                        collided = moleculeBoxCollisionAgent.detectAndDoCollision( moleculeA, model.getBox() );
                        if( !collided ) {
                            moleculeA.stepInTime( -dt );
                        }
                    }
                }
            }
        }
    }
}
