/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.chainreaction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AlphaRadiationNucleus;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleusConstituent;
import edu.colorado.phet.nuclearphysics2.model.FissionOneNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.model.Uranium238Nucleus;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModel.Listener;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate nuclear chain reaction within this
 * sim.
 *
 * @author John Blanco
 */
public class ChainReactionModel {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    private static final double MODEL_WORLD_WIDTH = 300;
    private static final double MODEL_WORLD_HEIGHT = MODEL_WORLD_WIDTH * 0.75;
    private static final double NUCLEUS_PROXIMITY_LIMIT = 15.0;
    
    // Controls position of the neutron source.
    private static final double NEUTRON_SOURCE_POS_X = -45;
    private static final double NEUTRON_SOURCE_POS_Y = 0;
    
    // A rectangle that is used to keep neulei from being placed too close
    // to the neutron source.  Tweak it to meet the needs of the sim.
    private static final Rectangle2D NEUTRON_SOURCE_RECT = new Rectangle2D.Double(NEUTRON_SOURCE_POS_X - 30, 
            NEUTRON_SOURCE_POS_Y - 20, 50, 50);
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private NuclearPhysics2Clock _clock;
    private ArrayList _listeners = new ArrayList();
    private ArrayList _u235Nuclei = new ArrayList();
    private ArrayList _u238Nuclei = new ArrayList();
    private Random _rand = new Random();
    private NeutronSource _neutronSource;
    private ArrayList _freeNeutrons;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public ChainReactionModel(NuclearPhysics2Clock clock)
    {
        _clock = clock;

        // Register as a listener to the clock.
        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // Reset the nucleus, including passing the alpha particle
                // back to it.
                // TODO: JPB TBD.
            }
        });
        
        // Add the neutron source to the side of the model.
        _neutronSource = new NeutronSource(NEUTRON_SOURCE_POS_X, NEUTRON_SOURCE_POS_Y);
        
        // Register as a listener to the neutron source so that we know when
        // new neutrons are generated.
        _neutronSource.addListener( new NeutronSource.Listener (){
            public void neutronGenerated(Neutron neutron){
                // Add this new neutron to the list of free particles.  It
                // should already be represented in the view and thus does
                // not need to be added to it.
                _freeNeutrons.add( neutron );
            }
            public void positionChanged(){
                // Ignore this, since we don't really care about it.
            }
        });
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock(){
        return _clock;
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
    
    /**
     * Set the number of U235 nuclei that are present in the model.  Note that
     * this can only be done before the chain reaction has been started.
     * 
     * @param numU235Nuclei
     * @return Number of this type of nuclei now present in model.
     */
    public int setNumU235Nuclei(int numU235Nuclei){
        if ( numU235Nuclei == _u235Nuclei.size() ){
            // Nothing to do - we've got what we need.
        }
        else if ( numU235Nuclei > _u235Nuclei.size() ){
            
            // We need to add some new nuclei.
            
            if (_u235Nuclei.size() == 0){
                // This is the first U235 nucleus, so put it at the origin.
                // TODO: JPB TBD - Need to either create a different nucleus or refactor FissionOneNucleus.
                AtomicNucleus nucleus = new FissionOneNucleus(_clock, new Point2D.Double(0, 0));
                nucleus.setDynamic( false );
                _u235Nuclei.add(nucleus);
                sendAddedNotifications( nucleus );
            }
            // We need to add some new nuclei.
            for (int i = 0; i < numU235Nuclei - _u235Nuclei.size(); i++){
                // TODO: JPB TBD - Need to either create a different nucleus or refactor FissionOneNucleus.
                Point2D position = getOpenNucleusLocation();
                if (position == null){
                    // We were unable to find a spot for this nucleus.
                    continue;
                }
                AtomicNucleus nucleus = new FissionOneNucleus(_clock, position);
                nucleus.setDynamic( false );
                _u235Nuclei.add(nucleus);
                sendAddedNotifications( nucleus );
            }
        }
        else{
            // We need to remove some nuclei.  Take them from the back of the
            // list, since this leaves the nucleus at the origin for last.
            int numNucleiToRemove = _u235Nuclei.size() - numU235Nuclei;
            for (int i = 0; i < numNucleiToRemove; i++){
                if (_u235Nuclei.size() > 0){
                    Object nucleus = _u235Nuclei.get( _u235Nuclei.size() - 1 );
                    _u235Nuclei.remove( nucleus );
                    sendRemovalNotifications( nucleus );
                }
            }
        }
        
        return _u235Nuclei.size();
    }

    /**
     * Set the number of U238 nuclei that are present in the model.  Note that
     * this can only be done before the chain reaction has been started.
     * 
     * @param numU238Nuclei
     * @return Number of this type of nuclei now present in model.
     */
    public int setNumU238Nuclei(int numU238Nuclei){
        if ( numU238Nuclei == _u238Nuclei.size() ){
            // Nothing to do - we've got what we need.
        }
        else if ( numU238Nuclei > _u238Nuclei.size() ){
            
            // We need to add some new nuclei.
            
            // We need to add some new nuclei.
            for (int i = 0; i < numU238Nuclei - _u238Nuclei.size(); i++){
                Point2D position = getOpenNucleusLocation();
                if (position == null){
                    // We were unable to find a spot for this nucleus.
                    continue;
                }
                AtomicNucleus nucleus = new Uranium238Nucleus(_clock, position);
                nucleus.setDynamic( false );
                _u238Nuclei.add(nucleus);
                sendAddedNotifications( nucleus );
            }
        }
        else{
            // We need to remove some nuclei.  Take them from the back of the
            // list, since this leaves the nucleus at the origin for last.
            int numNucleiToRemove = _u238Nuclei.size() - numU238Nuclei;
            for (int i = 0; i < numNucleiToRemove; i++){
                if (_u238Nuclei.size() > 0){
                    Object nucleus = _u238Nuclei.get( _u238Nuclei.size() - 1 );
                    _u238Nuclei.remove( nucleus );
                    sendRemovalNotifications( nucleus );
                }
            }
        }
        
        return _u238Nuclei.size();
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent){
        // TODO: JPB TBD
    }
    
    /**
     * Search for a location that is not already occupied by another nucleus.
     * 
     * @return
     */
    private Point2D getOpenNucleusLocation(){
        for (int i = 0; i < 100; i++){
            // Randomly select an x & y position
            double xPos = (MODEL_WORLD_WIDTH / 2) * (_rand.nextDouble() - 0.5); 
            double yPos = (MODEL_WORLD_HEIGHT / 2) * (_rand.nextDouble() - 0.5);
            Point2D position = new Point2D.Double(xPos, yPos);
            
            // Check if this point is taken.
            boolean pointTaken = false;
            if (NEUTRON_SOURCE_RECT.contains( position )){
                // Too close to the neutron source.
                pointTaken = true;
            }
            for (int j = 0; (j < _u235Nuclei.size()) && (pointTaken == false); j++){
                if (position.distance( ((AtomicNucleus)_u235Nuclei.get(j)).getPosition()) < NUCLEUS_PROXIMITY_LIMIT){
                    // This point is taken.
                    pointTaken = true;
                }
            }
            for (int j = 0; (j < _u238Nuclei.size()) && (pointTaken == false); j++){
                if (position.distance( ((AtomicNucleus)_u238Nuclei.get(j)).getPosition()) < NUCLEUS_PROXIMITY_LIMIT){
                    // This point is taken.
                    pointTaken = true;
                }
            }
            
            if (pointTaken == false){
                // We have found a usable location.  Return it.
                return position;
            }
        }
        
        // If we get to this point in the code, it means that we were unable
        // to locate a usable point.  Return null.
        return null;
    }
    
    /**
     * Notify listeners about the removal of an element from the model.
     * 
     * @param removedElement
     */
    private void sendRemovalNotifications (Object removedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).modelElementRemoved( removedElement );
        }
    }
    
    /**
     * Notify listeners about the addition of an element to the model.
     * @param addedElement
     */
    private void sendAddedNotifications (Object addedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).modelElementAdded( addedElement );
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
         * This informs the listener that a model element was added.
         * 
         * @param modelElement - Reference to the newly added model element.
         */
        public void modelElementAdded(Object modelElement);
        
        /**
         * This is invoked when a model element is removed from the model.
         * 
         * @param modelElement - Reference to the model element that was removed.
         */
        public void modelElementRemoved(Object modelElement);
    }
}
