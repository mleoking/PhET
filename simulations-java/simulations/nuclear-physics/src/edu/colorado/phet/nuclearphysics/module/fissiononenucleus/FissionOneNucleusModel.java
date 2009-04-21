/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.fissiononenucleus;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.Neutron;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;
import edu.colorado.phet.nuclearphysics.common.model.Proton;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.NeutronSource;
import edu.colorado.phet.nuclearphysics.model.Uranium235CompositeNucleus;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate atomic fission of a single nucleus
 * for this sim.
 *
 * @author John Blanco
 */
public class FissionOneNucleusModel {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    private static final double MOVING_NUCLEON_VELOCITY = 1.0;  // Femtometers per tick.
    private static final double INITIAL_NUCLEUS_VELOCITY = 0.05;  // Femtometers per tick.
    private static final double INITIAL_NUCLEUS_ACCELERATION = 0.4;  // Femtometers per tick per tick.
    
    // Time, in sim milliseconds, from the capture of a neutron until fission
    // occurs.
    public static final double FISSION_INTERVAL = 1200;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private Uranium235CompositeNucleus _primaryNucleus;
    private CompositeAtomicNucleus _daughterNucleus;
    private NeutronSource _neutronSource;
    private ArrayList _freeNucleons = new ArrayList();
    private ArrayList _freeAlphas = new ArrayList();
    private ArrayList _listeners = new ArrayList();
    private ConstantDtClock _clock;
    private Random _rand = new Random();
    private Vector2D _initialParentAccel;
    private Vector2D _initialDaughterAccel;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public FissionOneNucleusModel(NuclearPhysicsClock clock)
    {
        // Add a nucleus of Uranium 235 to the model.
        _primaryNucleus = new Uranium235CompositeNucleus( clock, new Point2D.Double( 0, 0 ), FISSION_INTERVAL );
        
        // Register as a listener to the nucleus so that we can see if any new
        // particles come out of it that need to be managed.

        _primaryNucleus.addListener( new AtomicNucleus.Adapter(){
            
            public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
                handleAtomicWeightChanged(atomicNucleus, numProtons, numNeutrons, byProducts);
            }
        });
        
        // Add the neutron source to the side of the model.
        _neutronSource = new NeutronSource(-30, 0);
        
        // Register as a listener to the neutron source so that we know when
        // new neutrons are generated.
        _neutronSource.addListener( new NeutronSource.Adapter (){
            public void neutronGenerated(Neutron neutron){
                // Add this new neutron to the list of free particles.  It
                // should already be represented in the view and thus does
                // not need to be added to it.
                _freeNucleons.add( neutron );
            }
            public void positionChanged(){
                // Ignore this, since we don't really care about it.
            }
        });
        
        // Register as a clock listener.
        _clock = clock;
        _clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                reset();
            }
        });
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    /**
     * Get a reference to the nucleus, of which there is only one in this
     * model.
     * 
     * @return - Reference to the nucleus model element.
     */
    public Uranium235CompositeNucleus getAtomicNucleus(){
        return _primaryNucleus;
    }
    
    /**
     * Get a reference to the neutron source, of which there is only one
     * in this model.
     * 
     * @return - Reference to the neutron generator model element.
     */
    public NeutronSource getNeutronSource(){
        return _neutronSource;
    }
    
    /**
     * Get a reference to the clock that is driving this model.
     * 
     * @return - Reference to the simulation clock.
     */
    public ConstantDtClock getClock(){
        return _clock;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

    /**
     * This method allows the caller to register for changes in the overall
     * model, as opposed to changes in the individual model elements.
     * 
     * @param listener
     */
    public void addListener(Listener listener)
    {
        assert !_listeners.contains( listener );
        
        _listeners.add( listener );
    }
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------

    /**
     * Handle a clock tick event.
     * 
     * @param ce - The clock event representing the sim clock at this point int time.
     */
    private void handleClockTicked(ClockEvent ce){
        
        // Update the velocity and acceleration of the daughter nuclei (if they exist).
        updateNucleiBehavior();
        
        // Move any free particles that exist.
        for (int i = 0; i < _freeNucleons.size(); i++){
            Nucleon freeNucleon = (Nucleon)_freeNucleons.get( i );
            assert freeNucleon instanceof Nucleon;
            freeNucleon.translate();

            // Check if any of the free particles have collided with the nucleus
            // and, if so, transfer the particle into the nucleus.
            if (Point2D.distance(freeNucleon.getPositionReference().getX(), freeNucleon.getPositionReference().getY(),
                    _primaryNucleus.getPositionReference().getX(), _primaryNucleus.getPositionReference().getY()) <
                _primaryNucleus.getDiameter() / 2){
                
                if (_primaryNucleus.captureParticle( freeNucleon )){
                    _freeNucleons.remove( i );                    
                }
            }
        }
    }
    
    /**
     * Reset the model.
     */
    private void reset(){

        // Reset the primary nucleus.
        _primaryNucleus.reset(_freeNucleons, _daughterNucleus);
        
        if (_daughterNucleus != null){
            // Fission has occurred, so the daughter must be reset, meaning
        	// that it essentially goes away.
            _daughterNucleus.reset();
            _daughterNucleus = null;
        }

        // The primary nucleus does not reabsorb all of the neutrons,
        // since at least one (and possibly several) was generated by the
        // neutron gun and wasn't a part of the original nucleus.  So, here we
        // simply get rid of any neutrons that are left over after the primary
        // nucleus has had a chance to reabsorb them.
        for (int i = 0; i < _freeNucleons.size(); i++){
            for (int j = 0; j < _listeners.size(); j++){
                ((Listener)_listeners.get( j )).nucleonRemoved( (Nucleon)_freeNucleons.get( i ) );
            }
        }
        _freeNucleons.clear();
    }
    
    /**
     * Handle a change in atomic weight of the primary nucleus, which
     * generally indicates a fission event.
     */
    private void handleAtomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
    		ArrayList byProducts){
        
        if (byProducts != null){
            // There are some byproducts of this event that need to be
            // managed by this object.
            for (int i = 0; i < byProducts.size(); i++){
                Object byProduct = byProducts.get( i );
                if ((byProduct instanceof Neutron) || (byProduct instanceof Proton)){
                    // Set a direction and velocity for this neutron.
                    double angle = (_rand.nextDouble() * Math.PI / 3);
                    if (_rand.nextBoolean()){
                        angle += Math.PI;
                    }
                    double xVel = Math.sin( angle ) * MOVING_NUCLEON_VELOCITY;
                    double yVel = Math.cos( angle ) * MOVING_NUCLEON_VELOCITY;
                    ((Nucleon)byProduct).setVelocity( xVel, yVel );
                    
                    // Add this new particle to our list.
                    _freeNucleons.add( byProduct );
                }
                else if (byProduct instanceof AlphaParticle){
                    _freeAlphas.add( byProduct );
                }
                else if (byProduct instanceof AtomicNucleus){
                    // Save the new daughter.
                    _daughterNucleus = (CompositeAtomicNucleus)byProduct;
                    
                    // Set random but opposite directions for the
                    // nuclei.  Limit them to be roughly horizontal so
                    // that they will be easier to see.
                    double angle = (_rand.nextDouble() * Math.PI / 3) + (Math.PI / 3);
                    if (_rand.nextBoolean()){
                        angle += Math.PI;
                    }
                    double xVel = Math.sin( angle ) * INITIAL_NUCLEUS_VELOCITY;
                    double yVel = Math.cos( angle ) * INITIAL_NUCLEUS_VELOCITY;
                    double xAcc = Math.sin( angle ) * INITIAL_NUCLEUS_ACCELERATION;
                    double yAcc = Math.cos( angle ) * INITIAL_NUCLEUS_ACCELERATION;
                    _primaryNucleus.setVelocity( xVel, yVel );
                    _primaryNucleus.setAcceleration( xAcc, yAcc );
                    _initialParentAccel = new Vector2D.Double( xAcc, yAcc );
                    _daughterNucleus.setVelocity( -xVel, -yVel );
                    _daughterNucleus.setAcceleration( -xAcc, -yAcc );
                    _initialDaughterAccel = new Vector2D.Double( -xAcc, -yAcc );
                }
                else {
                    // We should never get here, debug it if it does.
                    System.err.println("Error: Unexpected byproduct of decay event.");
                    assert false;
                }
            }
        }
    }
    
    private void updateNucleiBehavior(){
        if (_daughterNucleus != null){
            // The nuclei have fissioned and are traveling away from each
            // other.  As they do this, the acceleration decreases because the
            // force they exert on each other becomes smaller.  That's what we
            // are trying to model here.
            double distance = 
                _daughterNucleus.getPositionReference().distance( _primaryNucleus.getPositionReference() ) /
                _primaryNucleus.getDiameter();
            if (distance > 1){
                double scaleFactor = 1 / (distance * distance);
                _daughterNucleus.setAcceleration(_initialDaughterAccel.getX() * scaleFactor,
                        _initialDaughterAccel.getY() * scaleFactor);
                _primaryNucleus.setAcceleration(_initialParentAccel.getX() * scaleFactor,
                        _initialParentAccel.getY() * scaleFactor);
            }
        }
    }
    
    //------------------------------------------------------------------------
    // Inner interfaces
    //------------------------------------------------------------------------
    
    /**
     * Listener interface for observing this model.
     */
    public static interface Listener {
    	/**
    	 * Notify listeners that a nucleon was removed from the model.
    	 * 
    	 * @param nucleon - Nucleon that has been removed from the model.
    	 */
        public void nucleonRemoved(Nucleon nucleon);
    }
}
