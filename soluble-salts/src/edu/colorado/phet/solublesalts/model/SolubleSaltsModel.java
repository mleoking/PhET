/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.SolubleSaltsApplication;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonListener;
import edu.colorado.phet.solublesalts.model.salt.Salt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * SolubleSaltsModel
 * <p/>
 * Things the model understands:
 * <ul>
 * <li>Ksp, and how to compute the concentrations of ions
 * </ul>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsModel extends BaseModel implements SolubleSaltsApplication.ResetListener {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static final double MIN_SPEED = 1E-3;

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    // The world bounds of the model
    private double scale = 1;
    // Bounds for the entire model
    Rectangle2D bounds = new Rectangle2D.Double( 0, 0, 1024, 850 );
    // Ksp for the model
    double ksp;

    // The vessel
    private Vessel vessel;
    private Point2D vesselLoc = new Point2D.Double( SolubleSaltsConfig.VESSEL_ULC.getX() * scale,
                                                    SolubleSaltsConfig.VESSEL_ULC.getY() * scale );
    private double vesselWidth = SolubleSaltsConfig.VESSEL_SIZE.getWidth() * scale;
    private double vesselDepth = SolubleSaltsConfig.VESSEL_SIZE.getHeight() * scale;
    private double vesselWallThickness = SolubleSaltsConfig.VESSEL_WALL_THICKNESS * scale;

    // The faucet and drain
    private WaterSource waterSource;
    private Drain drain;

    // Collision mechanism objects
    IonIonCollisionExpert ionIonCollisionExpert = new IonIonCollisionExpert( this );

    private IonTracker ionTracker;
    private HeatSource heatSource;
    private boolean nucleationEnabled;
    private Shaker shaker;
    public CrystalTracker crystalTracker;
    private Vector2D accelerationOutOfWater = new Vector2D.Double( 0, SolubleSaltsConfig.DEFAULT_LATTICE_ACCELERATION );
    private Vector2D accelerationInWater = new Vector2D.Double();

    //---------------------------------------------------------------
    // Constructor and lifecycle methods
    //---------------------------------------------------------------
    
    public SolubleSaltsModel( IClock clock ) {

        ((SolubleSaltsApplication)SolubleSaltsApplication.instance()).addResetListener( this );
        clock.addClockListener( new ModelStepper() );

        // Add an agent that will track the ions of various classes
        ionTracker = new IonTracker();

        // Add an agent that will track the crystals that come and go from the model
        crystalTracker = new CrystalTracker( this );
        Crystal.addInstanceLifetimeListener( crystalTracker );

        // Create a vessel
        vessel = new Vessel( vesselWidth, vesselDepth, vesselWallThickness, vesselLoc, this );
        vessel.setWaterLevel( SolubleSaltsConfig.DEFAULT_WATER_LEVEL * scale );
        addModelElement( vessel );

        // Create the faucet and drain
        waterSource = new WaterSource( this );
        waterSource.setPosition( vessel.getLocation().getX() + 35, vessel.getLocation().getY() - 10 );
        addModelElement( waterSource );
        drain = new Drain( this,
                           new Point2D.Double( vessel.getLocation().getX() - vessel.getWallThickness(),
                                               vessel.getLocation().getY() + vessel.getDepth() - 30 ),
                           60, Drain.HORIZONTAL,
                           vesselWallThickness );
        addModelElement( drain );

        // Create an agent that will manage the flow of ions toward the drain when water is
        // flowing out of the vessel
        IonFlowManager ionFlowManager = new IonFlowManager( this );

        // Add a model element that will handle collisions between ions and the vessel
        addModelElement( new CollisionAgent() );

        // Add a heat source/sink
        heatSource = new HeatSource( this );
        addModelElement( heatSource );

        // Add a model element that will flip nucleation on and off depending on the
        // concentration of solutes and the Ksp
        addModelElement( new NucleationMonitorAgent() );

        // Add a shaker
        shaker = new Shaker( this );
        shaker.setPosition( vessel.getLocation().getX() + vessel.getWidth() / 2,
                            vessel.getLocation().getY() - 10 );

        // Listen for runtime changes in the configuration
        ((SolubleSaltsApplication)SolubleSaltsApplication.instance()).addResetListener( new SolubleSaltsApplication.ResetListener() {
            public void reset() {
                vessel.setWaterLevel( SolubleSaltsConfig.DEFAULT_WATER_LEVEL * scale );
            }
        } );
    }

    /**
     * @param event
     */
    public void update( ClockEvent event ) {
        super.update( event );

        // If a crystal is not in the water, it accelerates downward. If it's in the water, it moves
        // at a constant speed
        List crystals = crystalTracker.getCrystals();
        for( int i = 0; i < crystals.size(); i++ ) {
            Crystal crystal = (Crystal)crystals.get( i );
            if( !vessel.getWater().getBounds().contains( crystal.getPosition() ) &&
                !crystal.getAcceleration().equals( accelerationOutOfWater ) ) {
                crystal.setAcceleration( accelerationOutOfWater );
            }
            else if( vessel.getWater().getBounds().contains( crystal.getPosition() ) &&
                     !crystal.getAcceleration().equals( accelerationInWater ) ) {
                crystal.setAcceleration( accelerationInWater );
                crystal.setVelocity( 0, SolubleSaltsConfig.DEFAULT_LATTICE_SPEED );
            }
        }
    }

    /**
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );

        if( modelElement instanceof Ion ) {
            Ion ion = (Ion)modelElement;
            ionTracker.ionAdded( ion );
        }
    }

    /**
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );

        if( modelElement instanceof Ion ) {
            Ion ion = (Ion)modelElement;
            ionTracker.ionRemoved( ion );
            if( ion.isBound() ) {
                ion.getBindingCrystal().removeIon( ion );
            }
        }
    }

    /**
     *
     */
    public void reset() {
        List ions = ionTracker.getIons();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            removeModelElement( ion );
        }
        vessel.setWaterLevel( SolubleSaltsConfig.DEFAULT_WATER_LEVEL * scale );
        waterSource.setFlow( 0 );
        drain.setFlow( 0 );
        heatSource.setHeatChangePerClockTick( 0 );

        ChangeEvent event = new ChangeEvent( this );
        event.modelReset = true;
        changeListenerProxy.stateChanged( event );
    }
    

    /**
     * Determines which type of ion should be preferred for release. This is based on the ratio of the number
     * of free ions of each type in the salt, and the the ratio of their occurances in the salt lattice.
     *
     * @return The class of the preferred type of ion
     */
    public Class getPreferredTypeOfIonToRelease() {
        // Determine the preffered class of ion to release
        int numFreeAnions = getNumFreeIonsOfType( getCurrentSalt().getAnionClass() );
        int numFreeCations = getNumFreeIonsOfType( getCurrentSalt().getCationClass() );
        Class preferredIonType = null;
        if( (double)numFreeAnions / numFreeCations <
            (double)getCurrentSalt().getNumAnionsInUnit() / getCurrentSalt().getNumCationsInUnit() ) {
            preferredIonType = getCurrentSalt().getAnionClass();
        }
        else {
            preferredIonType = getCurrentSalt().getCationClass();
        }
        return preferredIonType;
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public boolean isNucleationEnabled() {
        return nucleationEnabled;
    }

    public double getScale() {
        return scale;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public double getKsp() {
        return ksp;
    }

    public void setKsp( double ksp ) {
        this.ksp = ksp;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public WaterSource getFaucet() {
        return waterSource;
    }

    public Drain getDrain() {
        return drain;
    }

    public int getNumIons() {
        return ionTracker.getIons().size();
    }

    public int getNumIonsOfType( Class ionClass ) {
        return ionTracker.getNumIonsOfType( ionClass );
    }

    public List getIonsOfType( Class ionClass ) {
        return ionTracker.getIonsOfType( ionClass );
    }

    public int getNumFreeIonsOfType( Class ionClass ) {
        return ionTracker.getNumFreeIonsOfType( ionClass );
    }

    public List getIons() {
        return ionTracker.getIons();
    }

    public HeatSource getHeatSource() {
        return heatSource;
    }

    /**
     * The concentration factor is the concentration of anions raised to the power of the number of anions in
     * a unit lattice, times the concentration of cations raised to the power of the number of cations in
     * a unit lattice.
     *
     * @return
     */
    public double getConcentrationFactor() {

        Salt salt = getCurrentSalt();
        int numAnions = ionTracker.getNumFreeIonsOfType( salt.getAnionClass() );
        int numAnionsInUnit = salt.getNumAnionsInUnit();
        int numCations = ionTracker.getNumFreeIonsOfType( salt.getCationClass() );
        int numCationsInUnit = salt.getNumCationsInUnit();
        double volume = vessel.getWaterLevel();
        double denominator = volume * SolubleSaltsConfig.AVAGADROS_NUMBER;
        double concentrationFactor = Math.pow( ( numAnions / denominator ), numAnionsInUnit )
                                     * Math.pow( ( numCations / denominator ), numCationsInUnit );
        return concentrationFactor;
    }

    public Shaker getShaker() {
        return shaker;
    }

    /**
     * Adds kinetic energy to all the ions in the system
     *
     * @param heat
     */
    public void addHeat( double heat ) {
        List ions = getIons();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            double speed0 = ion.getVelocity().getMagnitude();
            double speed1 = Math.sqrt( Math.max( MIN_SPEED, speed0 * speed0 + ( 2 * heat / ion.getMass() ) ) );
            if( ion.getVelocity().getMagnitude() > 0 ) {
                ion.setVelocity( ion.getVelocity().normalize().scale( speed1 ) );
            }
        }
    }

    public double getIonConcentration( Class ionClass ) {
        List ions = ionTracker.getIonsOfType( ionClass );
        double result = 0;
        if( ions != null ) {
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );
                if( !ion.isBound() ) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Returns the bounds of the water in the vessel
     *
     * @return
     */
    public Rectangle2D getWaterBounds() {
        return vessel.getWater().getBounds();
    }

    //-----------------------------------------------------------------
    // Change events and listeners
    //-----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public boolean saltChanged;
        public boolean modelReset;

        public ChangeEvent( Object source ) {
            super( source );
        }

        public SolubleSaltsModel getModel() {
            return (SolubleSaltsModel)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

    //----------------------------------------------------------------
    // Events and listeners for ions
    //----------------------------------------------------------------

    public void addIonListener( IonListener listener ) {
        ionTracker.addIonListener( listener );
    }

    public void removeIonListener( IonListener listener ) {
        ionTracker.removeIonListener( listener );
    }

    public void setCurrentSalt( Salt salt ) {
        shaker.setCurrentSalt( salt );
        ksp = salt.getKsp();
        ChangeEvent event = new ChangeEvent( this );
        event.saltChanged = true;
        changeListenerProxy.stateChanged( event );
    }

    public Salt getCurrentSalt() {
        return shaker.getCurrentSalt();
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Steps the model in time
     */
    private class ModelStepper extends ClockAdapter {
        public void clockTicked( ClockEvent clockEvent ) {

            // REmove ions that have gotten outside the bounds of the model
            List ions = ionTracker.getIons();
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );
                if( !getBounds().contains( ion.getPosition() )) {
                    removeModelElement( ion );
                }
            }
        }
    }

    /**
     * Detects and handles collisions between ions and the vessel
     */
    private class CollisionAgent implements ModelElement {
        IonVesselCollisionExpert ionVesselCollisionExpert = new IonVesselCollisionExpert( SolubleSaltsModel.this );

        public void stepInTime( double dt ) {

            // Look for collisions between ions and the vessel, and each other
            for( int i = 0; i < numModelElements(); i++ ) {
                if( modelElementAt( i ) instanceof Ion ) {
                    Ion ion = (Ion)modelElementAt( i );
                    ionVesselCollisionExpert.detectAndDoCollision( ion, vessel );
                    for( int j = 0; j < numModelElements(); j++ ) {
                        if( modelElementAt( i ) != modelElementAt( j )
                            && modelElementAt( j ) instanceof Ion ) {
                            ionIonCollisionExpert.detectAndDoCollision( (Ion)modelElementAt( i ),
                                                                        (Ion)modelElementAt( j ) );
                        }
                    }
                }
            }
        }
    }


    /**
     * Turns nucleation on and off depending on the concentration of solutes and Ksp, and
     * releases ions from crystals if the concentration is less than Ksp
     */
    private class NucleationMonitorAgent implements ModelElement, Crystal.InstanceLifetimeListener {
        private List crystals = new ArrayList();
        private Random random = new Random();

        public NucleationMonitorAgent() {
            Crystal.addInstanceLifetimeListener( this );
        }

        public void stepInTime( double dt ) {

            nucleationEnabled = getConcentrationFactor() > ksp;

            // Release ions until we're back above Ksp
            boolean ionReleased = true;
            while( crystals.size() > 0 && !nucleationEnabled && ionReleased ) {
                ionReleased = false;
                // pick a crystal at random
                int i = random.nextInt( crystals.size() );
                Crystal crystal = (Crystal)crystals.get( i );
                // Only tell the crystal to toss an ion if the entire crystal is in the water
                boolean isInWater = true;
                List ions = crystal.getIons();

                // If the entire crystal isn't in the water, skip it
                for( int j = 0; j < ions.size() && isInWater; j++ ) {
                    Ion ion = (Ion)ions.get( j );
                    if( !vessel.getWater().getBounds().contains( ion.getPosition() ) ) {
                        isInWater = false;
                    }
                }
                if( isInWater ) {
                    crystal.releaseIon( dt );
                    ionReleased = true;
                }
                nucleationEnabled = getConcentrationFactor() > ksp;
            }
        }

        public void instanceCreated( Crystal.InstanceLifetimeEvent event ) {
            crystals.add( event.getInstance() );
        }

        public void instanceDestroyed( Crystal.InstanceLifetimeEvent event ) {
            crystals.remove( event.getInstance() );
        }
    }

}
