/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.chainreaction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
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
import edu.colorado.phet.nuclearphysics.model.ContainmentVessel;
import edu.colorado.phet.nuclearphysics.model.DaughterNucleus;
import edu.colorado.phet.nuclearphysics.model.NeutronSource;
import edu.colorado.phet.nuclearphysics.model.Uranium235Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;

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
    
    // Constants that control the range within the model where nuclei may be
    // initially located.
    private static final double MAX_NUCLEUS_RANGE_X = 400;
    private static final double MAX_NUCLEUS_RANGE_Y = MAX_NUCLEUS_RANGE_X * 0.75;
    private static final double INTER_NUCLEUS_PROXIMITRY_LIMIT = 12;
    private static final double INITIAL_CONTAINMENT_VESSEL_RADIUS = MAX_NUCLEUS_RANGE_X / 6;
    private static final double CONTAINMENT_VESSEL_MARGIN = 12;
    
    // Constants that control the position of the neutron source.
    private static final double NEUTRON_SOURCE_POS_X = -50;
    private static final double NEUTRON_SOURCE_POS_Y = 0;
    
    // Constant rect that defines a space around the neutron source where
    // nuclei cannot initially be located.  This is just tweaked until things
    // look right.
    private static final Rectangle2D NEUTRON_SOURCE_OFF_LIMITS_RECT = 
        new Rectangle2D.Double( NEUTRON_SOURCE_POS_X - 70, NEUTRON_SOURCE_POS_Y - 20, 80, 50 );
    
    // Constants that control the behavior of fission products.
    private static final double FREED_NEUTRON_VELOCITY = 3;
    private static final double INITIAL_DAUGHTER_NUCLEUS_VELOCITY = 0;
    private static final double DAUGHTER_NUCLEUS_ACCELERATION = 0.2;
    
    // Constants for impact of collisions with containment vessel, arbitrary
    // values empirically determined.
    private static final double NEUTRON_COLLISION_IMPACT = 1;
    private static final double NUCLEUS_COLLISION_IMPACT = 10;
    
    // Constants for convenience and optimization.
    private static final Vector2D ZERO_ACCELERATION = new Vector2D.Double(0, 0);
    private static final double INITIAL_NEUTRON_SOURCE_ANGLE = -0.07;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private NuclearPhysicsClock _clock;
    private ArrayList _listeners = new ArrayList();
    private ArrayList _u235Nuclei = new ArrayList();
    private ArrayList _u238Nuclei = new ArrayList();
    private ArrayList _daughterNuclei = new ArrayList();
    private ArrayList _u239Nuclei = new ArrayList();
    private ArrayList _freeNeutrons = new ArrayList();
    private ArrayList _containedElements = new ArrayList();
    private Random _rand = new Random();
    private NeutronSource _neutronSource;
    private ContainmentVessel _containmentVessel;
    private int _ghostDaughterNuclei = 0; // Daughter nuclei that have been removed from the model in order to save
                                          // resources but haven't yet been reset.
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public ChainReactionModel(NuclearPhysicsClock clock)
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
                // Reset the model.
                reset();
            }
        });
        
        // Add the neutron source to the model.
        _neutronSource = new NeutronSource(NEUTRON_SOURCE_POS_X, NEUTRON_SOURCE_POS_Y);
        
        // Register as a listener to the neutron source so that we know when
        // new neutrons are generated.
        _neutronSource.addListener( new NeutronSource.Adapter (){
            public void neutronGenerated(Neutron neutron){
                // Add this new neutron to the list of free particles and let
                // any listeners know that it has come into existence.
                _freeNeutrons.add( neutron );
                notifyModelElementAdded( neutron );
            }
            public void positionChanged(){
                // Ignore this, since we don't really care about it.
            }
        });
        
        // There was a request by the team leaders for the neutron source to
        // be slightly tilted at init in this portion of the simulation in an
        // effort to tip off users that it can be repositioned.  This line of
        // code implements this request.
        _neutronSource.setFiringAngle( INITIAL_NEUTRON_SOURCE_ANGLE );
        
        // Add the containment vessel to the model.
        _containmentVessel = new ContainmentVessel(INITIAL_CONTAINMENT_VESSEL_RADIUS);
        
        // Register for notifications from the containment vessel.
        _containmentVessel.addListener( new ContainmentVessel.Adapter(){
            public void explosionOccurred(){
                handleContainmentVesselExplosion();
            }
            public void enableStateChanged(boolean isEnabled){
                handleContainmentVesselStateChange(isEnabled);
            };
            public void radiusChanged(double radius){
                handleContainmentVesselRadiusChanged(radius);
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
    
    /**
     * Get a reference to the containment vessel, of which there is only one
     * in this model.
     * 
     * @return - Reference to the containment vessel model element.
     */
    public ContainmentVessel getContainmentVessel(){
        return _containmentVessel;
    }

    /**
     * Get the number of U235 nuclei currently present.  Note that this does
     * NOT include nuclei that started out as U235 but that have undergone
     * fission.
     * 
     * @return
     */
    public int getNumU235Nuclei(){
        return _u235Nuclei.size();
    }

    /**
     * Gets the number of U238 nuclei present in the model.  Note that this
     * does NOT count nuclei that may have started off as U238 but have
     * absorbed a neutron.
     * 
     * @return
     */
    public int getNumU238Nuclei(){

        int total = 0;
        
        int numNuclei = _u238Nuclei.size();
        for (int i = 0; i < numNuclei; i++){
            Uranium238Nucleus nucleus = (Uranium238Nucleus)_u238Nuclei.get(i);
            if (nucleus.getNumNeutrons() == Uranium238Nucleus.ORIGINAL_NUM_NEUTRONS){
                // This one counts.
                total++;
            }
        }
        return total;
    }
    
    public ArrayList getNuclei(){
        ArrayList nucleiList = new ArrayList(_u235Nuclei.size() + _u238Nuclei.size() + _daughterNuclei.size() + 
                _u239Nuclei.size());
        nucleiList.addAll( _u235Nuclei );
        nucleiList.addAll( _u238Nuclei );
        nucleiList.addAll( _daughterNuclei );
        nucleiList.addAll( _u239Nuclei );
        return nucleiList;
    }
    
    /**
     * Get the percentage of U235 nuclei that have fissioned.
     */
    public double getPercentageU235Fissioned(){
        
        double unfissionedU235Nuclei = 0;
        double fissionedU235Nuclei = 0;
        
        // First check the collection of active U235 nuclei.
        for ( Iterator iterator = _u235Nuclei.iterator(); iterator.hasNext(); ) {
            
            // Increment the total count.
            unfissionedU235Nuclei++;
            
            Uranium235Nucleus nucleus = (Uranium235Nucleus) iterator.next();
            
            if ( nucleus.getNumNeutrons() < Uranium235Nucleus.ORIGINAL_NUM_NEUTRONS ){
                // Fission has occurred, so increment the counter.
                fissionedU235Nuclei++;
            }
        }
        
        // Now go through the daughter nuclei.
        for ( Iterator iterator = _daughterNuclei.iterator(); iterator.hasNext(); ) {
            
            AtomicNucleus nucleus = (AtomicNucleus) iterator.next();
            
            if (nucleus instanceof Uranium235Nucleus){
                if ( nucleus.getNumNeutrons() < Uranium235Nucleus.ORIGINAL_NUM_NEUTRONS ){
                    // This is a fissioned U235, so increment the counter.
                    fissionedU235Nuclei++;
                }
            }
        }
        
        // Add in the "ghost nuclei", which are nuclei that were removed from
        // the sim because they went out of visual range.
        fissionedU235Nuclei += _ghostDaughterNuclei / 2;
        
        if (unfissionedU235Nuclei + fissionedU235Nuclei == 0){
            // There are no U235 nuclei present, so return 0 and thereby
            // avoid any divide-by-zero issues.
            return 0;
        }
        
        return 100 * (fissionedU235Nuclei/(unfissionedU235Nuclei + fissionedU235Nuclei));
    }
        
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

    /**
     * Returns a boolean value that indicates whether any nuclei are present
     * in the model that have been changed by the chain reaction.  This can
     * essentially be used as an indicator of whether or not the chain
     * reaction has started.
     */
    public boolean getChangedNucleiExist(){
    	return (_daughterNuclei.size() > 0) || (_u239Nuclei.size() > 0) || _ghostDaughterNuclei > 0;
    }
    
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
     * Resets the model.
     */
    public void reset(){

    	// Remove the nuclei and free neutrons.
    	removeAllParticles();
    	
        // Reset the containment vessel.
        _containmentVessel.reset();
        
        // Set ourself back to the original state, which is with a single u235
        // nucleus in the center.
        setNumU235Nuclei( 1 );
        
        // Put the neutron source back to its original position.
        _neutronSource.setFiringAngle( INITIAL_NEUTRON_SOURCE_ANGLE );
        _neutronSource.setPosition( NEUTRON_SOURCE_POS_X, NEUTRON_SOURCE_POS_Y );
        
        // Let listeners know that things have changed.
        notifyPercentFissionedChanged();
        notifyResetOccurred();
    }
    
    /**
     * Reset the existing nuclei that have decayed.
     */
    public void resetNuclei(){
    	
    	// Reset the U235 nuclei that have decayed by determining how many
    	// daughter nuclei exist and adding back half that many U235 nuclei.
    	int numDecayedU235Nuclei = (_daughterNuclei.size() + _ghostDaughterNuclei) / 2;
    	setNumU235Nuclei( getNumU235Nuclei() + numDecayedU235Nuclei );
    	
    	// Remove the daughter nuclei, since the original U235 nuclei that
    	// they came from have been reset.
    	if (_daughterNuclei.size() > 0){
            for (int i = 0; i < _daughterNuclei.size(); i++){
                notifyModelElementRemoved( _daughterNuclei.get( i ) );
                ((AtomicNucleus)_daughterNuclei.get( i )).removedFromModel();
            }
            _daughterNuclei.clear();
        	notifyReativeNucleiNumberChanged();
    	}
    	if (_ghostDaughterNuclei > 0){
        	_ghostDaughterNuclei = 0;
        	notifyReativeNucleiNumberChanged();
    	}
    	
    	// This has probably changed the percentage of fissioned U235, so send
    	// a notification.
    	notifyPercentFissionedChanged();
    	
    	// Get rid of any free neutrons that are flying around.
        for (int i = 0; i < _freeNeutrons.size(); i++){
            notifyModelElementRemoved( _freeNeutrons.get( i ) );
        }
        _freeNeutrons.clear();
        
        // Reset any U238 nuclei that have absorbed a neutron (and thus
        // become U239).  We do it this way instead of removing the old ones
        // and adding back new ones (like we do for U235) so that they stay in
        // the same location.
        if (_u239Nuclei.size() > 0){
	        for (int i = 0; i < _u239Nuclei.size(); i++){
	        	Uranium238Nucleus u239Nucleus = (Uranium238Nucleus)_u239Nuclei.get(i);
	        	u239Nucleus.reset();
	        	_u238Nuclei.add(u239Nucleus);
	        }
	        _u239Nuclei.clear();
	        notifyReativeNucleiNumberChanged();
        }
        
        // Clear out any debris that had been captured by the containment
        // vessel.
        _containedElements.clear();
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
            int initialArraySize = _u235Nuclei.size();
            for (int i = 0; i < numU235Nuclei - initialArraySize; i++){
                Point2D position = null;
                if (_u235Nuclei.size() == 0){
                    // This is the first nucleus, so put it at the origin.
                    position = new Point2D.Double(0, 0);
                }
                else{
                    position = findOpenNucleusLocation();
                }
                if (position == null){
                    // We were unable to find a spot for this nucleus.
                    continue;
                }
                AtomicNucleus nucleus = new Uranium235Nucleus(_clock, position, 0);
                _u235Nuclei.add(nucleus);
                notifyModelElementAdded( nucleus );
                nucleus.addListener( new AtomicNucleus.Adapter(){
                    public void nucleusChangeEvent(AtomicNucleus nucleus, int numProtons, int numNeutrons,
                            ArrayList byProducts){
                        // Handle a potential fission event.
                        handleAtomicWeightChange(nucleus, numProtons, numNeutrons, byProducts);
                    }
                });
                notifyReativeNucleiNumberChanged();
            }
            
            // Reset any impacts that may have accumulated in the containment
            // vessel during previous reactions.
            _containmentVessel.resetImpactAccumulation();
        }
        else{
            // We need to remove some nuclei.  Take them from the back of the
            // list, since this leaves the nucleus at the origin for last.
            int numNucleiToRemove = _u235Nuclei.size() - numU235Nuclei;
            for (int i = 0; i < numNucleiToRemove; i++){
                if (_u235Nuclei.size() > 0){
                    Object nucleus = _u235Nuclei.get( _u235Nuclei.size() - 1 );
                    _u235Nuclei.remove( nucleus );
                    notifyModelElementRemoved( nucleus );
                    notifyReativeNucleiNumberChanged();
                }
            }
        }
        
        // This may have changed the percentage of fissioned nuclei, so send
        // an update just in case.
        notifyPercentFissionedChanged();
        
        // Return the number of U235 nuclei present.
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
                Point2D position = findOpenNucleusLocation();
                if (position == null){
                    // We were unable to find a spot for this nucleus.
                    continue;
                }
                AtomicNucleus nucleus = new Uranium238Nucleus(_clock, position);
                _u238Nuclei.add(nucleus);
                notifyModelElementAdded( nucleus );
                nucleus.addListener( new AtomicNucleus.Adapter(){
                    public void nucleusChangeEvent(AtomicNucleus nucleus, int numProtons, int numNeutrons,
                            ArrayList byProducts){
                        // Handle a potential fission event.
                        handleAtomicWeightChange(nucleus, numProtons, numNeutrons, byProducts);
                    }
                });
                notifyReativeNucleiNumberChanged();
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
                    notifyModelElementRemoved( nucleus );
                    notifyReativeNucleiNumberChanged();
                }
            }
        }
        
        return _u238Nuclei.size();
    }
    
    /**
     * Remove all the U235 nuclei that have decayed, which includes their
     * daughter nuclei.
     */
    public void removeDecayedU235Nuclei(){
    	
    	if (_daughterNuclei.size() > 0){
            for ( Iterator iterator = _daughterNuclei.iterator(); iterator.hasNext(); ) {
                
                AtomicNucleus nucleus = (AtomicNucleus) iterator.next();
                iterator.remove();
                notifyModelElementRemoved(nucleus);
                if (_containedElements.contains(nucleus)){
                	_containedElements.remove(nucleus);
                }
            }
            notifyReativeNucleiNumberChanged();
    	}
    	if (_ghostDaughterNuclei > 0){
            _ghostDaughterNuclei = 0;
            notifyReativeNucleiNumberChanged();
    	}
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent) {

        // Move any free particles that exist.
        int numFreeNeutrons = _freeNeutrons.size();
        for (int i = numFreeNeutrons - 1; i >= 0; i--){
            Nucleon freeNucleon = (Nucleon)_freeNeutrons.get( i );
            assert freeNucleon instanceof Nucleon;
            freeNucleon.translate();

            // Check if any of the free particles have collided with a nucleus
            // and, if so, give the nucleus the opportunity to absorb the
            // neutron (and possibly fission as a result).
            boolean particleAbsorbed = false;
            int j, numNuclei;
            numNuclei = _u235Nuclei.size();
            for (j = 0; (j < numNuclei) && (particleAbsorbed == false); j++){
                AtomicNucleus nucleus = (AtomicNucleus)_u235Nuclei.get( j );
                if (freeNucleon.getPositionReference().distance( nucleus.getPositionReference() ) <=
                    nucleus.getDiameter() / 2)
                {
                    // The particle is within capture range - see if the nucleus can capture it.
                    particleAbsorbed = nucleus.captureParticle( freeNucleon );
                }
            }
            numNuclei = _u238Nuclei.size();
            for (j = 0; (j < numNuclei) && (particleAbsorbed == false); j++){
                AtomicNucleus nucleus = (AtomicNucleus)_u238Nuclei.get( j );
                if (freeNucleon.getPositionReference().distance( nucleus.getPositionReference() ) <=
                    nucleus.getDiameter() / 2)
                {
                    // The particle is within capture range - see if the nucleus can capture it.
                    particleAbsorbed = nucleus.captureParticle( freeNucleon );
                }
            }
            
            if (particleAbsorbed){
                // The particle has become part of a larger nucleus, so we
                // need to take it off the list of free particles and let the
                // view know that it has disappeared as a separate entity.
                _freeNeutrons.remove( i );
                notifyModelElementRemoved( freeNucleon );
            }
            else if (!_containedElements.contains( freeNucleon ) &&
                     _containmentVessel.isPositionContained( freeNucleon.getPositionReference() )){
                // This particle is contained by the containment vessel, so we
                // remove it from the model, since it is no longer significant.
                _containmentVessel.recordImpact(NEUTRON_COLLISION_IMPACT);
                _freeNeutrons.remove( i );
                notifyModelElementRemoved( freeNucleon );
            }
        }
        
        if (_containmentVessel.getIsEnabled()){
            
            // The containment vessel is on, so we need to freeze any
            // particles that are contained by it.

            checkContainment(_u235Nuclei);
            checkContainment(_daughterNuclei);
        }
        
        // Check for model elements that have moved out of the simulation scope.
        removeOutOfRangeElements();
    }
    
    /**
     * Remove all nuclei and free neutrons from the model.
     */
    private void removeAllParticles(){

    	int i;
        
        for (i = 0; i < _freeNeutrons.size(); i++){
            notifyModelElementRemoved( _freeNeutrons.get( i ) );
        }
        _freeNeutrons.clear();
        
        for (i = 0; i < _u235Nuclei.size(); i++){
            notifyModelElementRemoved( _u235Nuclei.get( i ) );
            ((AtomicNucleus)_u235Nuclei.get( i )).removedFromModel();
        }
        _u235Nuclei.clear();
        
        for (i = 0; i < _u238Nuclei.size(); i++){
            notifyModelElementRemoved( _u238Nuclei.get( i ) );
            ((AtomicNucleus)_u238Nuclei.get( i )).removedFromModel();
        }
        _u238Nuclei.clear();
        
        for (i = 0; i < _daughterNuclei.size(); i++){
            notifyModelElementRemoved( _daughterNuclei.get( i ) );
            ((AtomicNucleus)_daughterNuclei.get( i )).removedFromModel();
        }
        _daughterNuclei.clear();
        
        for (i = 0; i < _u239Nuclei.size(); i++){
            notifyModelElementRemoved( _u239Nuclei.get( i ) );
            ((AtomicNucleus)_u239Nuclei.get( i )).removedFromModel();
        }
        _u239Nuclei.clear();
        
        // Zero out the counter that keeps track of daughter nuclei that have
        // been removed because they moved out of range of the model.
        _ghostDaughterNuclei = 0;
        
        // Remove any contained elements.  These will have already been
        // removed from the view.
        _containedElements.clear();
    }
    
    /**
     * Search for a location that is not already occupied by another nucleus.
     * 
     * @return
     */
    private Point2D findOpenNucleusLocation(){
        for (int i = 0; i < 100; i++){
            // Randomly select an x & y position
            double xPos = (MAX_NUCLEUS_RANGE_X / 2) * (_rand.nextDouble() - 0.5); 
            double yPos = (MAX_NUCLEUS_RANGE_Y / 2) * (_rand.nextDouble() - 0.5);
            Point2D position = new Point2D.Double(xPos, yPos);
            
            // Check if this point is available.
            boolean pointAvailable = true;
            if ((_containmentVessel.getIsEnabled() == true) && 
                    ((Point2D.distance( xPos, yPos, 0, 0 )) > (_containmentVessel.getRadius() - CONTAINMENT_VESSEL_MARGIN))){
                pointAvailable = false;
            }
            if (NEUTRON_SOURCE_OFF_LIMITS_RECT.contains( position )){
                // Too close to the neutron source.
                pointAvailable = false;
            }
            for (int j = 0; (j < _u235Nuclei.size()) && (pointAvailable == true); j++){
                if (position.distance( ((AtomicNucleus)_u235Nuclei.get(j)).getPositionReference()) < INTER_NUCLEUS_PROXIMITRY_LIMIT){
                    // This point is taken.
                    pointAvailable = false;
                }
            }
            for (int j = 0; (j < _u238Nuclei.size()) && (pointAvailable == true); j++){
                if (position.distance( ((AtomicNucleus)_u238Nuclei.get(j)).getPositionReference()) < INTER_NUCLEUS_PROXIMITRY_LIMIT){
                    // This point is taken.
                    pointAvailable = false;
                }
            }
            for (int j = 0; (j < _u239Nuclei.size()) && (pointAvailable == true); j++){
                if (position.distance( ((AtomicNucleus)_u239Nuclei.get(j)).getPositionReference()) < INTER_NUCLEUS_PROXIMITRY_LIMIT){
                    // This point is taken.
                    pointAvailable = false;
                }
            }
            
            if (pointAvailable == true){
                // We have found a usable location.  Return it.
                return position;
            }
        }
        
        // If we get to this point in the code, it means that we were unable
        // to locate a usable point.  Return null.
        return null;
    }

    /**
     * Checks an array of nuclei to see if any of them have come into contact
     * with the containment vessel and "freezes" any that have.
     * 
     * @param nuclei
     */
    private void checkContainment(ArrayList nuclei){
        AtomicNucleus nuke;
        int numNuclei = nuclei.size();
        for (int i = 0; i < numNuclei; i++){
            nuke = (AtomicNucleus)nuclei.get(i);
            if ((nuke.getVelocity().getMagnitude() != 0 ) && 
                (_containmentVessel.isPositionContained( nuke.getPositionReference() ))){
                // Freeze the nucleus at the edge of the containment vessel.
                nuke.setAcceleration( ZERO_ACCELERATION );
                nuke.setVelocity( 0, 0 );
                nuke.setPosition( _containmentVessel.getNearestContainmentPoint( nuke.getPositionReference() ));
                _containedElements.add( nuke );
                _containmentVessel.recordImpact(NUCLEUS_COLLISION_IMPACT);
                if (_containmentVessel.getIsExploded()){
                    // The last impacted caused the vessel to explode, so stop
                    // checking if nuclei are contained.
                    break;
                }
            }
        }
    }
    
    /**
     * Remove any model elements that have moved outside the range of the
     * simulation and have thus become irrelevant.
     */
    private void removeOutOfRangeElements(){
        
        int i, numElements;
        
        // NOTE: There is an important assumption built into the code below.
        // The assumption is that only neutrons and daughter nuclei move.  It
        // would be safer to test all collections of nuclei, but it would be
        // far less efficient, which is why this is done.  If the other
        // portions of this class are ever changed such that this assumption
        // becomes invalid, this code must be changed.
        
        numElements = _freeNeutrons.size();
        for (i = numElements - 1; i >= 0; i--){
            Neutron neutron = (Neutron)_freeNeutrons.get( i );
            if ( Math.abs( neutron.getPositionReference().getX() ) > (MAX_NUCLEUS_RANGE_X / 2) ||
                 Math.abs( neutron.getPositionReference().getY() ) > (MAX_NUCLEUS_RANGE_Y / 2)){
                // Get rid of this element.
                _freeNeutrons.remove( i );
                notifyModelElementRemoved( neutron );
            }
        }
        
        numElements = _daughterNuclei.size();
        for (i = numElements - 1; i >= 0; i--){
            AtomicNucleus nucleus = (AtomicNucleus)_daughterNuclei.get( i );
            if ( Math.abs( nucleus.getPositionReference().getX() ) > (MAX_NUCLEUS_RANGE_X / 2) ||
                 Math.abs( nucleus.getPositionReference().getY() ) > (MAX_NUCLEUS_RANGE_Y / 2)){
                // Get rid of this element.
                _daughterNuclei.remove( i );
                nucleus.removedFromModel();
                notifyModelElementRemoved( nucleus );
                _ghostDaughterNuclei++;
            }
        }
    }
    
    private void handleContainmentVesselExplosion(){
        // Remove all the nuclei that had been contained by the containment
        // vessel from the model.
        removeContainedParticles();
    }
    
    private void handleContainmentVesselStateChange(boolean isEnabled){
        if (isEnabled){
            // The containment vessel was just enabled, so we need to get rid
        	// of existing nuclei and set up the initial conditions.
        	removeAllParticles();
        	setNumU235Nuclei(1);
        }
        else{
        	// Containment vessel was turned off, so contained particles
        	// should go away.
        	removeContainedParticles();
        }
    }
    
    private void handleContainmentVesselRadiusChanged(double newRadius){

        // Remove any nuclei that might now be outside the containment vessel.
        removeNucleiOutsideContainmentVessel( _u235Nuclei );
        removeNucleiOutsideContainmentVessel( _u238Nuclei );
        
        // Remove any particles or nuclei that might have been captured (i.e.
        // contained) by the containment vessel.
        removeContainedParticles();
    }
    
    /**
     * Remove any nuclei on the supplied list that are outside of the
     * containment vessel.  This is generally used if and when the user
     * enables the containment vessel after having already added some nuclei.
     * 
     * @param nucleiList - List of the nuclei to be checked and possibly
     * removed.
     */
    
    private void removeNucleiOutsideContainmentVessel(ArrayList nucleiList){
        
        int i, numElements;
        
        numElements = nucleiList.size();
        for (i = numElements - 1; i >= 0; i--){
            AtomicNucleus nucleus = (AtomicNucleus)nucleiList.get( i );
            if (nucleus.getPositionReference().distance( 0, 0 ) > 
                    _containmentVessel.getRadius() - CONTAINMENT_VESSEL_MARGIN){
                // Remove this nucleus.
                nucleus.removedFromModel();
                nucleiList.remove( i );
                notifyModelElementRemoved( nucleus );
                notifyReativeNucleiNumberChanged();
            }
        }
    }
    
    /**
     * Removed any nuclei or free nucleons that have been contained by the
     * containment vessel.
     */
    private void removeContainedParticles(){
        int numContainedElements = _containedElements.size();
        for ( int i = numContainedElements - 1; i >= 0; i-- ) {
            
            Object modelElement = _containedElements.get( i );
            
            _containedElements.remove( i );
            notifyModelElementRemoved( modelElement );
            
            if (modelElement instanceof Uranium235Nucleus){
                if (!_u235Nuclei.remove( modelElement )){
                    _daughterNuclei.remove( modelElement );
                }
                _ghostDaughterNuclei++;
            }
            else if (modelElement instanceof DaughterNucleus){
                _daughterNuclei.remove( modelElement );
                _ghostDaughterNuclei++;
            }
            else{
                // This shouldn't happen, debug it if it does.
                System.err.println("Error: Unexpected model element type contained by containment vessel.");
                assert false;
            }
        }
    }
    
    /**
     * Notify listeners about the removal of an element from the model.
     * 
     * @param removedElement
     */
    private void notifyModelElementRemoved (Object removedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).modelElementRemoved( removedElement );
        }
    }
    
    /**
     * Notify listeners about the addition of an element to the model.
     * @param addedElement
     */
    private void notifyModelElementAdded (Object addedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).modelElementAdded( addedElement );
        }
    }

    /**
     * Notify listeners that the percentage of U235 that has fissioned has
     * changed.
     */
    private void notifyPercentFissionedChanged(){
        double percentU235Fissioned = getPercentageU235Fissioned();
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).percentageU235FissionedChanged(percentU235Fissioned);
        }
    }
    
    /**
     * Notify listeners that the number of nuclei that are capable of interacting
     * with neutrons has changed.
     */
    private void notifyReativeNucleiNumberChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).reactiveNucleiNumberChanged();
        }
    }
    
    /**
     * Notify listeners that the model has been reset.
     */
    private void notifyResetOccurred(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).resetOccurred();
        }
    }
    
    /**
     * Handle a change in atomic weight signaled by a U235 or U238 nucleus, which
     * may indicate that fission has occurred.
     * 
     * @param numProtons
     * @param numNeutrons
     * @param byProducts
     */
    private void handleAtomicWeightChange(AtomicNucleus nucleus, int numProtons, int numNeutrons, 
            ArrayList byProducts){
        
        if (byProducts != null){
            // There are some byproducts of this event that need to be
            // managed by this model.
            for (int i = 0; i < byProducts.size(); i++){
                Object byProduct = byProducts.get( i );
                if ((byProduct instanceof Neutron) || (byProduct instanceof Proton)){
                    // Let any listeners know that a new element has appeared
                    // separately in the model.
                    notifyModelElementAdded(byProduct);
                    
                    // Set a direction and velocity for this element.
                    double angle = (_rand.nextDouble() * Math.PI * 2);
                    double xVel = Math.sin( angle ) * FREED_NEUTRON_VELOCITY;
                    double yVel = Math.cos( angle ) * FREED_NEUTRON_VELOCITY;
                    ((Nucleon)byProduct).setVelocity( xVel, yVel );
                    
                    // Add this new particle to our list.
                    _freeNeutrons.add( byProduct );
                }
                else if (byProduct instanceof AtomicNucleus){
                    // Save the new daughter and let any listeners
                    // know that it exists.
                    AtomicNucleus daughterNucleus = (AtomicNucleus)byProduct;
                    _daughterNuclei.add( daughterNucleus );
                    notifyModelElementAdded( daughterNucleus );

                    // Set random but opposite directions for the produced
                    // nuclei.
                    double angle = (_rand.nextDouble() * Math.PI * 2);
                    double xVel = Math.sin( angle ) * INITIAL_DAUGHTER_NUCLEUS_VELOCITY;
                    double yVel = Math.cos( angle ) * INITIAL_DAUGHTER_NUCLEUS_VELOCITY;
                    double xAcc = Math.sin( angle ) * DAUGHTER_NUCLEUS_ACCELERATION;
                    double yAcc = Math.cos( angle ) * DAUGHTER_NUCLEUS_ACCELERATION;
                    nucleus.setVelocity( xVel, yVel );
                    nucleus.setAcceleration( xAcc, yAcc );
                    daughterNucleus.setVelocity( -xVel, -yVel );
                    daughterNucleus.setAcceleration( -xAcc, -yAcc );
                    
                    // Move the 'parent' nucleus to the list of daughter
                    // nuclei so that it doesn't continue to be involved in
                    // the fission detection calculations.
                    assert (nucleus instanceof Uranium235Nucleus);
                    _u235Nuclei.remove( nucleus );
                    _daughterNuclei.add( nucleus );
                    
                    // Signal any listeners that the percentage of fissioned
                    // U235 nuclei has probably changed.
                    notifyPercentFissionedChanged();
                    
                    // Signal any listeners that the number of reactive
                    // nuclei has changed.
                    notifyReativeNucleiNumberChanged();
                }
                else {
                    // We should never get here, debug it if it does.
                    System.err.println("Error: Unexpected byproduct of decay event.");
                    assert false;
                }
            }
        }
        else if (nucleus instanceof Uranium238Nucleus){
            
            // This event may indicate that a U238 nucleus absorbed a neutron,
            // which means that we no longer need to check if it wants to
            // absorb any more.
            if (nucleus.getNumNeutrons() == Uranium238Nucleus.ORIGINAL_NUM_NEUTRONS + 1){
                _u238Nuclei.remove( nucleus );
                _u239Nuclei.add( nucleus );
                notifyReativeNucleiNumberChanged();
            }
        }
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces
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
        
        /**
         * This is invoked when the number of reactive nuclei (i.e. those that
         * can interact with neutrons) has changed. 
         */
        public void reactiveNucleiNumberChanged();
        
        /**
         * This is invoked to signal that the model has been reset.
         * 
         */
        public void resetOccurred();
        
        /**
         * This method is called to inform listeners that a change has
         * occurred in the percentage of U235 that has fissioned.
         */
        public void percentageU235FissionedChanged(double percentU235Fissioned);
    }
    
    /**
     * Adapter for the interface described above.
     *
     * @author John Blanco
     */
    public static class Adapter implements Listener {
        public void modelElementAdded(Object modelElement){}
        public void modelElementRemoved(Object modelElement){}
        public void reactiveNucleiNumberChanged(){}
        public void percentageU235FissionedChanged(double percentU235Fissioned){}
        public void resetOccurred(){}
    }
}
