/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.fissiononenucleus;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.DaughterNucleus;
import edu.colorado.phet.nuclearphysics2.model.FissionOneNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.model.Nucleon;
import edu.colorado.phet.nuclearphysics2.model.Proton;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModel.Listener;
import edu.colorado.phet.nuclearphysics2.view.NeutronNode;

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
    private static final double MOVING_NUCLEUS_VELOCITY = 1.0;  // Femtometer per tick.
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private FissionOneNucleus _primaryNucleus;
    private AtomicNucleus _daughterNucleus;
    private NeutronSource _neutronSource;
    private ArrayList _freeNucleons = new ArrayList();
    private ArrayList _freeAlphas = new ArrayList();
    private ArrayList _listeners = new ArrayList();
    private ConstantDtClock _clock;
    private Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public FissionOneNucleusModel(NuclearPhysics2Clock clock)
    {
        // Add a nucleus of Uranium 235 to the model.
        _primaryNucleus = new FissionOneNucleus( clock, new Point2D.Double( 0, 0 ) );
        
        // Register as a listener to the nucleus so that we can see if any new
        // particles come out of it that need to be managed.

        _primaryNucleus.addListener( new AtomicNucleus.Listener(){
            
            public void atomicWeightChanged(int numProtons, int numNeutrons, ArrayList byProducts){
                handleAtomicWeightChanged(numProtons, numNeutrons, byProducts);
            }
            
            public void positionChanged(){
                // This particular notification doesn't matter here.
            }
        });
        
        // Add the neutron source to the side of the model.
        _neutronSource = new NeutronSource(-30, 0);
        
        // Register as a listener to the neutron source so that we know when
        // new neutrons are generated.
        _neutronSource.addListener( new NeutronSource.Listener (){
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
    public AtomicNucleus getAtomicNucleus(){
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
        
        // Move any free particles that exist.
        for (int i = 0; i < _freeNucleons.size(); i++){
            Nucleon freeNucleon = (Nucleon)_freeNucleons.get( i );
            assert freeNucleon instanceof Nucleon;
            freeNucleon.translate();

            // Check if any of the free particles have collided with the nucleus
            // and, if so, transfer the particle into the nucleus.
            if (Point2D.distance(freeNucleon.getPosition().getX(), freeNucleon.getPosition().getY(), _primaryNucleus.getPosition().getX(), _primaryNucleus.getPosition().getY()) <
                _primaryNucleus.getDiameter() / 2){
                
                if (_primaryNucleus.captureNeutron( freeNucleon )){
                    _freeNucleons.remove( i );                    
                }
            }
        }
    }
    
    /**
     * Reset the model.
     */
    private void reset(){
        if (_daughterNucleus != null){
            // Fission has occurred.  Add the free neutrons and the daughter 
            // back to the primary nucleus.
            _primaryNucleus.reset(_freeNucleons, _daughterNucleus);
            _daughterNucleus.reset();
            _daughterNucleus = null;
            
            // The primary nucleus does not reabsorb all of the neutrons,
            // since at least one was generated by the neutron gun and wasn't a part
            // of the original nucleus.  We don't know which is which, but we
            // do need to remove the remaining one(s) from the view.
            for (int i = 0; i < _freeNucleons.size(); i++){
                for (int j = 0; j < _listeners.size(); j++){
                    ((Listener)_listeners.get( j )).nucleonRemoved( (Nucleon)_freeNucleons.get( i ) );
                }
            }
            _freeNucleons.removeAll( _freeNucleons );
        }
    }
    
    /**
     * Handle a change in atomic weight of the primary nucleus, which
     * generally indicates a fission event.
     */
    private void handleAtomicWeightChanged(int numProtons, int numNeutrons, ArrayList byProducts){
        
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
                    double xVel = Math.sin( angle ) * MOVING_NUCLEUS_VELOCITY;
                    double yVel = Math.cos( angle ) * MOVING_NUCLEUS_VELOCITY;
                    ((Nucleon)byProduct).setVelocity( xVel, yVel );
                    
                    // Add this new particle to our list.
                    _freeNucleons.add( byProduct );
                }
                else if (byProduct instanceof AlphaParticle){
                    _freeAlphas.add( byProduct );
                }
                else if (byProduct instanceof AtomicNucleus){
                    // Save the new daughter and let any listeners
                    // know that it exists.
                    _daughterNucleus = (AtomicNucleus)byProduct;
                    for (int j = 0; j < _listeners.size(); j++){
                        ((Listener)_listeners.get( j )).daughterNucleusCreated( _daughterNucleus );
                    }
                    // Set random but opposite directions for the
                    // nuclei.  Limit them to be roughly horizontal so
                    // that they will be easier to see.
                    double angle = (_rand.nextDouble() * Math.PI / 3) + (Math.PI / 3);
                    if (_rand.nextBoolean()){
                        angle += Math.PI;
                    }
                    double xVel = Math.sin( angle ) * MOVING_NUCLEUS_VELOCITY;
                    double yVel = Math.cos( angle ) * MOVING_NUCLEUS_VELOCITY;
                    _primaryNucleus.setVelocity( xVel, yVel );
                    _daughterNucleus.setVelocity( -xVel, -yVel );
                }
                else {
                    // We should never get here, debug it if it does.
                    System.err.println("Error: Unexpected byproduct of decay event.");
                    assert false;
                }
            }
        }
    }
    
    //------------------------------------------------------------------------
    // Inner interfaces
    //------------------------------------------------------------------------
    
    /**
     * This listener interface allows listeners to get notified when an alpha
     * particle is added (i.e. come in to existence by separating from the
     * nucleus) or is removed (i.e. recombines with the nucleus).
     */
    public static interface Listener {
        /**
         * This informs the listener that a new daughter nucleus was added,
         * meaning that it split off from an existing nucleus.
         * 
         * @param daughterNucleus - Reference to the newly spawned nucleus.
         */
        public void daughterNucleusCreated(AtomicNucleus daughterNucleus);
        
        /**
         * This informs the listener that the daughter nucleus not longer
         * exists, which generally happens when the sim is reset and the
         * daughter is reconsolidated with the "parent".
         *  
         * @param daughterNucleus - The nucleus that is now gone.
         */
        public void daughterNucleusRemoved(AtomicNucleus daughterNucleus);
        
        /**
         * This informs the listener that a particle has been removed from
         * the model.  This generally happens after a reset.
         * @param nucleon
         */
        public void nucleonRemoved(Nucleon nucleon);
    }
}
