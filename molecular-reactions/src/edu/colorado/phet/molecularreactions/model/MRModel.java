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
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.mechanics.Body;

import java.util.List;
import java.util.EventListener;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

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
 * <p/>
 * The EnergyProfile defines the potential energy of an AB molecule, the potential energy of a BC molecule, and
 * the energy threshold between those two that must be crossed in order for a reaction to occur. The reaction can be
 * either an A molecule hitting a BC molecule with enough energy to cross the threshold and become an AB molecule
 * and a free C molecule, or a C molecule hitting an AB molecule resulting in a BC molecule and a free A molecule.
 * <p/>
 * All collisions are detected and handled by a CollisionAgent.
 * <p/>
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
    private SelectedMoleculeTracker selectedMoleculeTracker;
    private double potentialEnergyStored;
    private List potentialEnergySources = new ArrayList();
    private TemperatureControl tempCtrl;

    /**
     * Constructor
     *
     * @param clock
     */
    public MRModel( IClock clock ) {
        super( clock );

        setInitialConditions();

        // Add a listener that will take care of adding and removing bonds from the model
        addListener( new CompositeMolecule.DependentModelElementMonitor( this ) );
    }

    public void setInitialConditions() {
        // Create the reaction object;
        reaction = new A_BC_AB_C_Reaction( this );

        // Add a box
        box = new Box2D( new Point2D.Double( 30, 30 ),
                         new Point2D.Double( 380, 330 ),
                         0 );
        addModelElement( box );

        // Add the temperature control to the model
        tempCtrl = new TemperatureControl( this );
        tempCtrl.setPosition( ( getBox().getMaxX() + getBox().getMinX() ) / 2,
                              getBox().getMaxY() + 15 );
        addModelElement( tempCtrl );

        // Create collisions agents that will detect and handle collisions between molecules,
        // and between molecules and the box
        addModelElement( new CollisionAgent( this ) );

        // Add an agent that will track the simple molecule that's closest to the selected
        // molecule
        selectedMoleculeTracker = new SelectedMoleculeTracker( this );
        addModelElement( selectedMoleculeTracker );

        // Add an agent that will create provisional bonds when appropriate
        addModelElement( new ProvisionalBondDetector( this ) );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof PotentialEnergySource ) {
            potentialEnergySources.add( modelElement );
        }
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if( modelElement instanceof PotentialEnergySource ) {
            potentialEnergySources.remove( modelElement );
        }
    }

    public void addPotentialEnergySource( PotentialEnergySource peSource ) {
        potentialEnergySources.add( peSource );
    }

    public void removePotentialEnergySource( PotentialEnergySource peSource ) {
        potentialEnergySources.remove( peSource );
    }

    public double getPotentialEnergy() {
        double pe = 0;
        for( int i = 0; i < potentialEnergySources.size(); i++ ) {
            PotentialEnergySource source = (PotentialEnergySource)potentialEnergySources.get( i );
            pe += source.getPE();
        }
        return pe;
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

    public TemperatureControl getTemperatureControl() {
        return tempCtrl;
    }

    public SimpleMolecule getMoleculeBeingTracked() {
        return selectedMoleculeTracker.getMoleculeTracked();
    }

    public void addSelectedMoleculeTrackerListener( SelectedMoleculeTracker.Listener listener ) {
        selectedMoleculeTracker.addListener( listener );
    }

    public void removeSelectedMoleculeTrackerListener( SelectedMoleculeTracker.Listener listener ) {
        selectedMoleculeTracker.removeListener( listener );
    }

    public void addToPotentialEnergyStored( double pe ) {
        potentialEnergyStored += pe;
    }

    //--------------------------------------------------------------------------------------------------
    // Time-dependent behavior
    //--------------------------------------------------------------------------------------------------

    double pe0 = 0;
    double pe1 = 0;

    protected void stepInTime( double dt ) {
        double ke0 = getTotalKineticEnergy();
//        double pe0 = pePrev;
        potentialEnergyStored = 0;

//        potentialEnergyStored = 0;
        super.stepInTime( dt );
//        clearForces();
        double ke1 = getTotalKineticEnergy();
        pe1 = potentialEnergyStored;
//        System.out.println( "pe0 = " + pe0 + "\tpe1 = " + pe1 );
//        double deltaPe = pe1 - pe0;
//        pe0 = pe1;
//        double f = ( ke0 - deltaPe ) / ke1;
//        if( f < 0 ) {
//            System.out.println( "MRModel.stepInTime" );
//        }
//        if( f > 0 ) {
//            adjustKineticEnergy( Math.sqrt( f ) );
//        }
//        double ke2 = getTotalKineticEnergy();
//        System.out.println( "ke0 = " + ke0 + "\tke1 = " + ke1 + "\tdeltaPe = " + deltaPe + "\tke2 = " + ke2 );


        monitorEnergy();
    }

    private void monitorEnergy() {
        List modelElements = getModelElements();
        double pe = 0;
        double ke = 0;
        Vector2D m = new Vector2D.Double( );
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof PotentialEnergySource ) {
                pe += ((PotentialEnergySource)o).getPE();
            }
            if( o instanceof Body && !( o instanceof SimpleMolecule && ((SimpleMolecule)o).isPartOfComposite() )) {
                ke += ((Body)o).getKineticEnergy();
            }

            if( o instanceof AbstractMolecule && !( o instanceof AbstractMolecule && ((AbstractMolecule)o).isPartOfComposite() )) {
                m.add(((Body)o).getMomentum());
            }
        }
        DecimalFormat df = new DecimalFormat( "#.000");
        System.out.println( "te = " + df.format( pe + ke ) + "\tpe = " + df.format( pe ) + "\tke = " + df.format( ke ) + "\tm = " + df.format( m.getMagnitude() ));
    }

    private void adjustKineticEnergy( double eFactor ) {
        List modelElements = getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof Body ) {
                Body body = (Body)o;
                body.setVelocity( body.getVelocity().getX() * eFactor, body.getVelocity().getY() * eFactor );
            }
        }
    }

    private double clearForces() {
        double keTotal = 0;
        List modelElements = getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof Body ) {
                Body body = (Body)o;
                body.setAcceleration( 0, 0 );
            }
        }
        return keTotal;
    }

    private double getTotalKineticEnergy() {
        double keTotal = 0;
        List modelElements = getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof Body ) {
                Body body = (Body)o;
                keTotal += body.getKineticEnergy();
            }
        }
        return keTotal;
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
