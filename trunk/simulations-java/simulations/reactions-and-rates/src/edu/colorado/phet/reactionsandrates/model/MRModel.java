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
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.collision.Box2D;
import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.reactionsandrates.model.reactions.Reaction;

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
    private MRBox box;
    private Reaction reaction;
    private SelectedMoleculeTracker selectedMoleculeTracker;
    private TemperatureControl tempCtrl;
    // The amount of energy added or removed from the model in a time step
    // by objects such as the temperature control. This is used in tweaking
    // the total amount of energy in the system at the end of each time step
    // so it is conserved.
    private double dEnergy;
    private double defaultTemperature = MRConfig.DEFAULT_TEMPERATURE;
    private boolean averageTotal;

    /**
     * Constructor
     *
     * @param clock The clock to run the simulation.
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
        setEnergyProfile( MRConfig.DEFAULT_ENERGY_PROFILE );

        // Add a box
        box = new MRBox( new Point2D.Double( 30, 30 ),
                         new Point2D.Double( 380, 330 ),
                         0,
                         this );
        addModelElement( box );

        // Add the temperature control to the model
        tempCtrl = new TemperatureControl( this );
        tempCtrl.setPosition( ( getBox().getMaxX() + getBox().getMinX() ) / 2,
                              getBox().getMaxY() + 15 );
        addModelElement( tempCtrl );

        // Add an agent that will control the temperature of the box when the
        // temperature control changes
        // todo: Make the TemperatureControl a heatSource and heatSink and make the
        // box a listener to it, to remove this intermediary object
        new BoxHeater( tempCtrl, box );

        // Create collisions agents that will detect and handle collisions between molecules,
        // and between molecules and the box
        addModelElement( new CollisionAgent( this ) );

        // Add an agent that will track the simple molecule that's closest to the selected
        // molecule
        selectedMoleculeTracker = new SelectedMoleculeTracker( this );
        addModelElement( selectedMoleculeTracker );
    }

    public void setReaction( Reaction reaction ) {
        this.reaction = reaction;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void setEnergyProfile( EnergyProfile profile ) {
        getReaction().setEnergyProfile( profile );
        modelListenerProxy.notifyEnergyProfileChanged( profile );
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

    public SimpleMolecule getNearestToMoleculeBeingTracked() {
        return selectedMoleculeTracker.getClosestMolecule();
    }

    public void addSelectedMoleculeTrackerListener( SelectedMoleculeTracker.Listener listener ) {
        selectedMoleculeTracker.addListener( listener );
    }

    public void removeSelectedMoleculeTrackerListener( SelectedMoleculeTracker.Listener listener ) {
        selectedMoleculeTracker.removeListener( listener );
    }

    //--------------------------------------------------------------------------------------------------
    // Time-dependent behavior
    //--------------------------------------------------------------------------------------------------

    protected void stepInTime( double dt ) {

        // Initialize the accumulator for energy that is deliberately added to the system. It gets
        // added to through calls to addEnergy().
        dEnergy = 0;
        double pe0 = getTotalPotentialEnergy();
        double ke0 = getTotalKineticEnergy();

        super.stepInTime( dt );

        // Adjust the energy in the system, so it is conserved
        double pe1 = getTotalPotentialEnergy();
        double ke1 = getTotalKineticEnergy();

        // Adjust the velocities of objects so energy is conserved
        double keF = pe0 + ke0 - pe1 + dEnergy;
        double r = Math.sqrt( ke1 != 0 ? keF / ke1 : 1 );
        List modelElements = selectFor( Body.class );

        for ( int i = 0; i < modelElements.size(); i++ ) {
            Body body = (Body) modelElements.get( i );
            body.setVelocity( body.getVelocity().scale( r ) );
            body.setOmega( body.getOmega() * r );
        }
    }

    public void monitorEnergy() {
        List modelElements = selectFor( AbstractMolecule.class );

        MutableVector2D m = new MutableVector2D();

        for ( int i = 0; i < modelElements.size(); i++ ) {
            AbstractMolecule abstractMolecule = (AbstractMolecule) modelElements.get( i );

            if ( abstractMolecule.isPartOfComposite() ) {
                m.add( ( (Body) abstractMolecule ).getMomentum() );
            }
        }
    }

    public double getTotalKineticEnergy() {
        double keTotal = 0;
        List modelElements = selectFor( new Class[] { Body.class, KineticEnergySource.class } );

        for ( int i = 0; i < modelElements.size(); i++ ) {
            Body body = (Body) modelElements.get( i );
            keTotal += body.getKineticEnergy();
        }

        return keTotal;
    }

    /**
     * Gets the temperature of the system, which is taken to be the
     * average kinetic energy of all the KineticEnergySources.
     *
     * @return The temperature of the system.
     */
    public double getTemperature() {
        int cnt = countWholeMolecules();

        return cnt > 0 ? getTotalKineticEnergy() / cnt : getDefaultTemperature();
    }

    public double getDefaultTemperature() {
        return defaultTemperature;
    }

    public void setDefaultTemperature( double defaultTemperature ) {
        this.defaultTemperature = defaultTemperature;

        modelListenerProxy.notifyDefaultTemperatureChanged( defaultTemperature );
    }

    // TODO: Factor out common code from 'averaging' methods
    public double getAverageKineticEnergyPerClass() {
        Map peMap = new HashMap(),
                totalMap = new HashMap();

        List modelElements = selectFor( Body.class );

        for ( int i = 0; i < modelElements.size(); i++ ) {
            Body source = (Body) modelElements.get( i );

            Class key = source.getClass();

            if ( !( source instanceof AbstractMolecule ) ||
                 ( !( (AbstractMolecule) source ).isPartOfComposite() ) ) {


                double peTotal = peMap.containsKey( key ) ? ( (Double) peMap.get( key ) ).doubleValue() : 0.0;
                int total = totalMap.containsKey( key ) ? ( (Integer) totalMap.get( key ) ).intValue() : 0;

                peMap.put( key, new Double( peTotal + source.getKineticEnergy() ) );
                totalMap.put( key, new Integer( total + 1 ) );
            }
        }

        Iterator keyIterator = peMap.keySet().iterator();

        double keTotal = 0.0;

        while ( keyIterator.hasNext() ) {
            Class key = (Class) keyIterator.next();

            double pe = ( (Double) peMap.get( key ) ).doubleValue();
            int num = ( (Integer) totalMap.get( key ) ).intValue();

            keTotal += pe / (double) num;
        }

        return keTotal;
    }

    public double getAveragePotentialEnergyPerClass() {
        Map peMap = new HashMap(),
                totalMap = new HashMap();

        List modelElements = selectFor( PotentialEnergySource.class );

        for ( int i = 0; i < modelElements.size(); i++ ) {
            PotentialEnergySource source = (PotentialEnergySource) modelElements.get( i );

            Class key = source.getClass();

            if ( !( source instanceof AbstractMolecule ) ||
                 ( !( (AbstractMolecule) source ).isPartOfComposite() ) ) {


                double peTotal = peMap.containsKey( key ) ? ( (Double) peMap.get( key ) ).doubleValue() : 0.0;
                int total = totalMap.containsKey( key ) ? ( (Integer) totalMap.get( key ) ).intValue() : 0;

                peMap.put( key, new Double( peTotal + source.getPE() ) );
                totalMap.put( key, new Integer( total + 1 ) );
            }
        }

        Iterator keyIterator = peMap.keySet().iterator();

        double peTotal = 0.0;

        while ( keyIterator.hasNext() ) {
            Class key = (Class) keyIterator.next();

            double pe = ( (Double) peMap.get( key ) ).doubleValue();
            int num = ( (Integer) totalMap.get( key ) ).intValue();

            peTotal += pe / (double) num;
        }

        return peTotal;
    }

    public double getTotalPotentialEnergy() {
        double peTotal = 0;

        List modelElements = selectFor( PotentialEnergySource.class );

        for ( int i = 0; i < modelElements.size(); i++ ) {
            PotentialEnergySource body = (PotentialEnergySource) modelElements.get( i );

            peTotal += body.getPE();
        }

        return peTotal;
    }

    public void addEnergy( double de ) {
        dEnergy += de;
    }

    /**
     * Removes all molecules and bonds from the model
     */
    public void removeAllMolecules() {
        List modelElements = selectForAny( new Class[] { AbstractMolecule.class, Bond.class, ProvisionalBond.class } );

        for ( int i = modelElements.size() - 1; i >= 0; i-- ) {
            ModelElement me = (ModelElement) modelElements.get( i );

            removeModelElement( me );
        }
    }

    private double getAverageTotalEnergy() {
        int wholeMoleculeCount = countWholeMolecules();

        if ( wholeMoleculeCount != 0 ) {
            return getTotalKineticEnergy() / wholeMoleculeCount;
        }

        return getDefaultTemperature();
    }

    public void setAverageTotal( boolean averageTotal ) {
        this.averageTotal = averageTotal;
    }

    public double getTotalEnergy() {
        if ( averageTotal ) {
            return getAverageTotalEnergy();
        }

        int wholeMoleculeCount = countWholeMolecules();

        if ( wholeMoleculeCount != 0 ) {
            return getAverageKineticEnergyPerClass() +
                   getAveragePotentialEnergyPerClass();
        }

        return getDefaultTemperature();
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface ModelListener extends EventListener {
        void notifyEnergyProfileChanged( EnergyProfile profile );

        void notifyDefaultTemperatureChanged( double newInitialTemperature );
    }

    public static class ModelListenerAdapter implements ModelListener {
        public void notifyEnergyProfileChanged( EnergyProfile profile ) {
        }

        public void notifyDefaultTemperatureChanged( double newInitialTemperature ) {
        }
    }

    private EventChannel modelEventChannel = new EventChannel( ModelListener.class );
    private ModelListener modelListenerProxy = (ModelListener) modelEventChannel.getListenerProxy();

    public void addListener( ModelListener listener ) {
        modelEventChannel.addListener( listener );
    }

    public void removeListener( ModelListener listener ) {
        modelEventChannel.removeListener( listener );
    }

    public int countSimpleMolecules() {
        int cnt = 0;

        List modelElements = selectFor( AbstractMolecule.class );

        for ( int i = 0; i < modelElements.size(); i++ ) {
            AbstractMolecule abstractMolecule = (AbstractMolecule) modelElements.get( i );

            if ( abstractMolecule.isSimpleMolecule() ) {
                cnt++;
            }
        }

        return cnt;
    }

    public int countCompositeMolecules() {
        int cnt = 0;

        List modelElements = selectFor( AbstractMolecule.class );

        for ( int i = 0; i < modelElements.size(); i++ ) {
            AbstractMolecule abstractMolecule = (AbstractMolecule) modelElements.get( i );

            if ( abstractMolecule.isComposite() ) {
                cnt++;
            }
        }

        return cnt;
    }

    public int countWholeMolecules() {
        int cnt = 0;

        List modelElements = selectFor( AbstractMolecule.class );

        for ( int i = 0; i < modelElements.size(); i++ ) {
            AbstractMolecule abstractMolecule = (AbstractMolecule) modelElements.get( i );

            if ( abstractMolecule.isWholeMolecule() ) {
                cnt++;
            }
        }

        return cnt;
    }
}
