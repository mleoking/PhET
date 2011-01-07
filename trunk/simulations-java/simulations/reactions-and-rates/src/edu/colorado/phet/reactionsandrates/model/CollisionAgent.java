// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.reactionsandrates.model.collision.MRCollisionAgent;
import edu.colorado.phet.reactionsandrates.model.collision.MoleculeBoxCollisionAgent;
import edu.colorado.phet.reactionsandrates.model.collision.MoleculeMoleculeCollisionAgent_2;
import edu.colorado.phet.reactionsandrates.model.collision.SpringCollision;

import java.util.ArrayList;
import java.util.List;

/**
 * CollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CollisionAgent implements ModelElement {

    private MRCollisionAgent moleculeMoleculeCollisionAgent;
    //    private MoleculeMoleculeHardSphereCollisionAgent moleculeMoleculeCollisionAgent;
    private MoleculeBoxCollisionAgent moleculeBoxCollisionAgent;
    private MRModel model;

    public CollisionAgent( MRModel model ) {
        this.model = model;

        // todo: DEBUG. move elsewhere
        SpringCollision.Spring spring = new SpringCollision.Spring( 10, model.getReaction().getEnergyProfile().getThresholdWidth() / 2 );
        moleculeMoleculeCollisionAgent = new MoleculeMoleculeCollisionAgent_2( model );
        moleculeBoxCollisionAgent = new MoleculeBoxCollisionAgent( model );
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
