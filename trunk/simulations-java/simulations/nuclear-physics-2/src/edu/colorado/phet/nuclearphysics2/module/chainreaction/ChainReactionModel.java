/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.chainreaction;

import java.awt.geom.Point2D;
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
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
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
    private static final double MODEL_WORLD_WIDTH = 200;
    private static final double MODEL_WORLD_HEIGHT = MODEL_WORLD_WIDTH * 0.75;
    private static final double NUCLEUS_PROXIMITY_LIMIT = 15.0;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private NuclearPhysics2Clock _clock;
    private ArrayList _listeners = new ArrayList();
    private ArrayList _u235Nuclei = new ArrayList();
    private Random _rand = new Random();
    
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
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

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
            for (int j = 0; j < _u235Nuclei.size(); j++){
                if (position.distance( ((AtomicNucleus)_u235Nuclei.get(j)).getPosition()) < NUCLEUS_PROXIMITY_LIMIT){
                    // This point is taken.
                    pointTaken = true;
                    break;
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
