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
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.A_AB_BC_C_Reaction;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.util.List;
import java.util.EventListener;
import java.awt.geom.Point2D;

/**
 * MRModel
 * <p/>
 * The model for molecular reactions.
 * <p/>
 * The model consists principally of SimpleMolecules and CompositeMolecules, an instance
 * of EnergyProfile, andan instance of Reaction. CompositeMolecules
 * are composed of SimpleMolecules, only. They cannot contain other CompositeMolecules.
 * <p/>
 * There are three types of SimpleMolecules: A, B, and C. A and C molecules can exist either by themselves, or in
 * combination with one B molecule. CompositeMolecules, therefore can be either AB or BC molecules.
 * <p>
 * The EnergyProfile defines the potential energy of an AB molecule, the potential energy of a BC molecule, and
 * the energy threshold between those two that must be crossed in order for a reaction to occur. The reaction can be
 * either an A molecule hitting a BC molecule with enough energy to cross the threshold and become an AB molecule
 * and a free C molecule, or a C molecule hitting an AB molecule resulting in a BC molecule and a free A molecule.
 * <p>
 * All collisions are detected and handled by a CollisionAgent.
 * <p>
 * There are two sorts of bonds in the model. Regular, or "hard" bonds, and provisional bonds. Hard bonds join two
 * molecules in a compound molecule. Provisional bonds are used to show the relationship between simple molecules that
 * are within a certain distance of a bonding site in a compound molecule. This includes simple molecules approaching
 * compound molecules, and simple molecules that are leaving compound molecules after a reaction.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModel extends PublishingModel {
    private Box2D box;
    private Reaction reaction;

    /**
     * Constructor
     * @param clock
     */
    public MRModel( IClock clock ) {
        super( clock );

        // Create the reaction object;
        reaction = new A_AB_BC_C_Reaction( this );

        // Add a box
        box = new Box2D( new Point2D.Double( 30, 30 ),
                         new Point2D.Double( 250, 350 ),
                         0 );
        addModelElement( box );

        // Create collisions agents that will detect and handle collisions between molecules,
        // and between molecules and the box
        addModelElement( new CollisionAgent( this ) );

        // Add an agent that will track the simple molecule that's closest to the selected
        // molecule
        addModelElement( new SelectedMoleculeTracker( this ) );

        // Add an agent that will create provisional bonds when appropriate
        addModelElement( new ProvisionalBondDetector( this ));
    }

    public void setReaction( Reaction reaction ) {
        this.reaction = reaction;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public EnergyProfile getEnergyProfile() {
        return reaction.getEnergyProfile();
    }

    public Box2D getBox() {
        return box;
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
}
