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
import edu.colorado.phet.molecularreactions.model.collision.MoleculeBoxCollisionAgent;
import edu.colorado.phet.molecularreactions.model.collision.SpringCollision;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeHardSphereCollisionAgent;

import java.util.List;
import java.util.ArrayList;

/**
 * CollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CollisionAgent implements ModelElement {

    private MoleculeMoleculeHardSphereCollisionAgent moleculeMoleculeCollisionAgent;
//    private MoleculeMoleculeCollisionAgent moleculeMoleculeCollisionAgent;
    private MoleculeBoxCollisionAgent moleculeBoxCollisionAgent;
    private MRModel model;

    public CollisionAgent( MRModel model ) {
        this.model = model;

        // todo: DEBUG. move elsewhere
        SpringCollision.Spring spring = new SpringCollision.Spring( 10, model.getReaction().getEnergyProfile().getThresholdWidth() / 2 );

        moleculeMoleculeCollisionAgent = new MoleculeMoleculeHardSphereCollisionAgent( model );
//        moleculeMoleculeCollisionAgent = new MoleculeMoleculeCollisionAgent( model,
//                                                                             springCollision,
////                                                                             new HardSphereCollision() );
//springCollision );
        moleculeBoxCollisionAgent = new MoleculeBoxCollisionAgent();
    }

    public void stepInTime( double dt ) {
        // Make a copy of the model element list. We have to use a copy of the list
        // because collisions will add/remove model elements from the model
        List modelElements = new ArrayList( model.getModelElements() );
        for( int i = modelElements.size() - 1; i >= 0; i-- ) {
            Object o = modelElements.get( i );

            // Check every Molecule that is not part of a larger CompositeMolecule
            if( o instanceof AbstractMolecule ) {
                boolean collidedWithMolecule = false;
                AbstractMolecule moleculeA = (AbstractMolecule)o;
                if( !moleculeA.isPartOfComposite() ) {
                    for( int j = i - 1; j >= 0 && !collidedWithMolecule; j-- ) {
                        Object o2 = modelElements.get( j );
                        if( o2 instanceof AbstractMolecule ) {
                            AbstractMolecule moleculeB = (AbstractMolecule)o2;
                            if( !moleculeB.isPartOfComposite() && moleculeA != moleculeB ) {
                                collidedWithMolecule = moleculeMoleculeCollisionAgent.detectAndDoCollision( model,
                                                                                                            moleculeA,
                                                                                                            moleculeB );
                            }
                        }
                    }

                    // Don't know why this was here. rjl 10/13/06
//                    if( !collidedWithMolecule ) {
//                        moleculeA.setAcceleration( 0,0 );
//                    }

                    // Check for collisions between the molecule and the box. Note that we have to
                    // do this several times so that cases in which a molecule
                    // hits in the corner of the box will be handled properly. If we don't do this,
                    // molecules can escape from the box
                    boolean collidedWithBox;
                    collidedWithBox = moleculeBoxCollisionAgent.detectAndDoCollision( moleculeA, model.getBox() );
                    if( collidedWithBox ) {
                        moleculeA.stepInTime( dt );
                        collidedWithBox = moleculeBoxCollisionAgent.detectAndDoCollision( moleculeA, model.getBox() );
                        if( !collidedWithBox ) {
                            moleculeA.stepInTime( -dt );
                        }
                    }
                }
            }
        }
    }
}
