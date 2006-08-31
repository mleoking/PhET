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

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.collision.SphereBoxExpert;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeBoxCollisionAgent;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionAgent;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.util.List;
import java.util.EventListener;
import java.awt.geom.Point2D;

/**
 * MRModel
 * <p/>
 * The model for molecular reactions
 * <p/>
 * Notes:
 * <p/>
 * When a molecule becomes part of a composite molecule, it is taken out of the model.
 * That means it doesn't get clock ticks or show up in the getModelElements() list.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModel extends PublishingModel {
    private Box2D box;
    private EnergyProfile energyProfile = new EnergyProfile( 0, MRConfig.DEFAULT_REACTION_THRESHOLD, 0 );

    public MRModel( IClock clock ) {
        super( clock );

        // Add a box
        box = new Box2D( new Point2D.Double( 30, 30 ),
                         new Point2D.Double( 250, 350 ),
                         0 );
        addModelElement( box );

        // Create collisions agents that will detect and handle collisions between molecules,
        // and between molecules and the box
        addModelElement( new CollisionAgent() );

        // Add an agent that will track the simple molecule that's closest to the selected
        // molecule
        addModelElement( new SelectedMoleculeTracker( this ) );
    }

//    public double getReactionThresholdEnergy() {
//        return reactionThresholdEnergy;
//    }
//
//    public void setReactionThresholdEnergy( double reactionThresholdEnergy ) {
//        this.reactionThresholdEnergy = reactionThresholdEnergy;
//        modelListenerProxy.reactionThresholdChanged( this );
//    }

    public void setEnergyProfile( EnergyProfile energyProfile ) {
        this.energyProfile.setLeftLevel( energyProfile.getLeftLevel() );
        this.energyProfile.setRightLevel( energyProfile.getRightLevel() );
        this.energyProfile.setPeakLevel( energyProfile.getPeakLevel() );
    }

    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface ModelListener extends EventListener {
        void reactionThresholdChanged( MRModel model );
    }

    private EventChannel modelEventChannel = new EventChannel( ModelListener.class );
    private ModelListener modelListenerProxy = (ModelListener)modelEventChannel.getListenerProxy();

    public void addListener( ModelListener listener ) {
        modelEventChannel.addListener( listener );
    }

    public void removeListener( ModelListener listener ) {
        modelEventChannel.removeListener( listener );
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class CollisionAgent implements ModelElement {
        SphereBoxExpert sphereBoxExpert = new SphereBoxExpert();
        MoleculeMoleculeCollisionAgent moleculeMoleculeCollisionAgent = new MoleculeMoleculeCollisionAgent( MRModel.this );
        MoleculeBoxCollisionAgent moleculeBoxCollisionAgent = new MoleculeBoxCollisionAgent();

        public void stepInTime( double dt ) {
            List modelElements = getModelElements();
            for( int i = modelElements.size() - 1; i >= 0; i-- ) {
                Object o = modelElements.get( i );

                // Checkevery Molecule that is not part of a large CompositeMolecule
                if( o instanceof Molecule ) {
                    Molecule moleculeA = (Molecule)o;
                    if( !moleculeA.isPartOfComposite() ) {
                        for( int j = modelElements.size() - 1; j >= 0; j-- ) {
                            Object o2 = modelElements.get( j );
                            if( o2 instanceof Molecule ) {
                                Molecule moleculeB = (Molecule)o2;
                                if( !moleculeB.isPartOfComposite() && moleculeA != moleculeB ) {
                                    moleculeMoleculeCollisionAgent.detectAndDoCollision( MRModel.this,
                                                                                         moleculeA,
                                                                                         moleculeB );
                                }
                            }
                        }

                        // Check for collisions between the molecule and the box. Note that we have to
                        // do this until no collisions are detected so that cases in which a molecule
                        // hits in the corner of the box will be handled properly. If we don't do this,
                        // molecules can escape from the box
                        boolean collided;
                        collided = moleculeBoxCollisionAgent.detectAndDoCollision( moleculeA, box );
                        if( collided ) {
                            moleculeA.stepInTime( dt );
                            collided = moleculeBoxCollisionAgent.detectAndDoCollision( moleculeA, box );
                            if( !collided ) {
                                moleculeA.stepInTime( -dt );
                            }
                        }
                    }
                }
            }
        }
    }
}
