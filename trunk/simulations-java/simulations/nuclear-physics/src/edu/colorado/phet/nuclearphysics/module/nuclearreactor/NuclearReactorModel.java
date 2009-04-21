/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.nuclearreactor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.model.Nucleon;
import edu.colorado.phet.nuclearphysics.model.Proton;
import edu.colorado.phet.nuclearphysics.model.Uranium235Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate the basic behavior of a nuclear
 * reactor.
 *
 * @author John Blanco
 */
public class NuclearReactorModel {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    // Constants that control the overall size of the nuclear reactor.  Note
    // that these dimensions are in femtometers in order to be consistent with
    // the nuclei size in this and the other models, but of course a real
    // nuclear reactor would have much larger dimensions.
    private static final double OVERALL_REACTOR_WIDTH = 650;
    private static final double OVERALL_REACTOR_HEIGHT = OVERALL_REACTOR_WIDTH * 0.42;
    private static final double REACTOR_WALL_WIDTH = 20;
    
    // Constant that controls where in model space the reactor resides.  This
    // assumes that the 'center of the world' is at (0,0).  It is shifted
    // slightly to the left to account for the control rod handle on the right
    // hand side.
    private static final Point2D REACTOR_POSITION = new Point2D.Double(-(OVERALL_REACTOR_WIDTH / 1.9),
            -(OVERALL_REACTOR_HEIGHT / 2));
    
    // Constant that controls the number of chambers, between which are the
    // control rods.  There will always be one less control rod than there are
    // chambers.  It is assumed that the chambers are of equal size.
    private static final int NUMBER_OF_REACTION_CHAMBERS = 6;
    
    // Constant that controls the size relationship between the chambers and
    // the control rods.  This is a ratio, and can be though of as
    // (chamber width)/(control rod width).
    private static final double CHAMBER_TO_CONTROL_ROD_WIDTH_RATIO = 5;
    
    // Constants that control the initial placement of nuclei within the
    // reaction chambers.
    private static final double MIN_DISTANCE_FROM_NUCLEI_TO_WALLS  = 18;
    private static final double MIN_INTER_NUCLEI_DISTANCE          = 15;
    
    // Constants that control the behavior of neutrons fired into reaction chambers.
    private static final double NUMBER_OF_NEUTRONS_TO_FIRE = 2;
    private static final double NEUTRON_VELOCITY = 2;
    
    // Constants that control the behavior of fission products.
    private static final double FREED_NEUTRON_VELOCITY = 3;
    private static final double DAUGHTER_NUCLEI_SPLIT_DISTANCE = 10;
    
    // Constants that control the monitoring of fission events, which
    // allow us to determine the average energy released.
    private static final double MAX_TEMP_CHANGE_PER_TICK = 1.0;
    private static double JOULES_PER_FISSION_EVENT = 3.2E-11;

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private NuclearPhysicsClock _clock;
    private ArrayList _listeners;
    private ArrayList _u235Nuclei;
    private ArrayList _u238Nuclei;
    private ArrayList _daughterNuclei;
    private ArrayList _freeNeutrons;
    private ArrayList _controlRods;
    private ArrayList _reactionChamberRects;
    private int       _u235FissionEventCount;
    private int []    _fissionEventBins;
    private int       _currentBin;
    private int       _clockTicksPerSecond;
    private Rectangle2D _innerReactorRect;
    private double    _currentTemperature;
    private double    _totalEnergyReleased;
    private double    _energyReleasedPerSecond;
    private Random _rand = new Random();
    private boolean _reactionStarted;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public NuclearReactorModel(NuclearPhysicsClock clock)
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
        
        // Allocate the array lists that we will need.
        _listeners            = new ArrayList();
        _u235Nuclei           = new ArrayList();
        _u238Nuclei           = new ArrayList();
        _daughterNuclei       = new ArrayList();
        _freeNeutrons         = new ArrayList();
        _reactionChamberRects = new ArrayList(NUMBER_OF_REACTION_CHAMBERS);
        _controlRods          = new ArrayList(NUMBER_OF_REACTION_CHAMBERS - 1);
        
        // Perform other internal initialization.
        _clockTicksPerSecond = (int)(Math.round(1000/_clock.getDt()));
        _fissionEventBins = new int [_clockTicksPerSecond];
        _currentBin = 0;
        _totalEnergyReleased = 0;
        _energyReleasedPerSecond = 0;
        _reactionStarted = false;
        
        // Create the reaction chambers (which are modeled as simple 
        // rectangles) and the control rods.
        double reactionChamberWidth = 
            (OVERALL_REACTOR_WIDTH - (2 * REACTOR_WALL_WIDTH)) / NUMBER_OF_REACTION_CHAMBERS;
        double controlRodWidth = reactionChamberWidth / CHAMBER_TO_CONTROL_ROD_WIDTH_RATIO;
        reactionChamberWidth -= controlRodWidth * (NUMBER_OF_REACTION_CHAMBERS - 1)/NUMBER_OF_REACTION_CHAMBERS;
        double reactionChamberHeight = (OVERALL_REACTOR_HEIGHT - (REACTOR_WALL_WIDTH * 2)); 
        for ( int i = 0; i < NUMBER_OF_REACTION_CHAMBERS; i++ ) {
            double xPos = 
                REACTOR_POSITION.getX() + REACTOR_WALL_WIDTH + (i * (reactionChamberWidth + controlRodWidth));
            double yPos = REACTOR_POSITION.getY() + REACTOR_WALL_WIDTH;
            Rectangle2D chamberRect = new Rectangle2D.Double(xPos, yPos, reactionChamberWidth, reactionChamberHeight);
            _reactionChamberRects.add( chamberRect );
            
            if (i < NUMBER_OF_REACTION_CHAMBERS - 1){
                // Create a control rod.
                ControlRod controlRod = 
                    new ControlRod(xPos + reactionChamberWidth, yPos, controlRodWidth, OVERALL_REACTOR_HEIGHT);
                _controlRods.add( controlRod );
            }
        }
        
        // Create a rectangle that represents the inner boundary of the
        // reactor.  This is used to test when particles have effectively gone
        // outside the bounds of the reactor.
        _innerReactorRect = new Rectangle2D.Double(REACTOR_POSITION.getX() + REACTOR_WALL_WIDTH,
                REACTOR_POSITION.getY() + REACTOR_WALL_WIDTH, OVERALL_REACTOR_WIDTH - (2 * REACTOR_WALL_WIDTH),
                OVERALL_REACTOR_HEIGHT - (2 * REACTOR_WALL_WIDTH));
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock(){
        return _clock;
    }
    
    public double getReactorWallWidth(){
        return REACTOR_WALL_WIDTH;
    }
    
    /**
     * Get a rectangle that represents the size and position of the reactor in
     * model coordinates.  Note that this allocates a new rectangle object, so
     * it should not be called too frequently or performance issues could
     * result.
     * 
     * @return rectangle representing the position of the nuclear reactor.
     */
    public Rectangle2D getReactorRect(){
        return new Rectangle2D.Double(REACTOR_POSITION.getX(), REACTOR_POSITION.getY(), OVERALL_REACTOR_WIDTH,
                OVERALL_REACTOR_HEIGHT);    
    }
    
    /**
     * Get a reference to the array of control rods maintained by this model.
     * 
     * @return
     */
    public ArrayList getControlRodsReference(){
        return _controlRods;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Fires neutrons into the reaction chambers, which is how the reaction
     * can be initiated.
     * 
     */
    public void fireNeutrons() {
        ArrayList chambersUsed = new ArrayList();
        for( int i = 0; i < NUMBER_OF_NEUTRONS_TO_FIRE; i++ ) {
            
            // Select the chamber into which this neutron will be fired.
            Integer chamberNumber = null;
            do {
                chamberNumber = new Integer( _rand.nextInt( NUMBER_OF_REACTION_CHAMBERS ) );
            } while( chambersUsed.contains( chamberNumber ) );
            
            chambersUsed.add( chamberNumber );
            Rectangle2D chamberRect = (Rectangle2D)_reactionChamberRects.get( chamberNumber.intValue() );
            
            // Select the initial position along the edge of the chamber.
            double startPosX;
            double startPosY;
            if (_rand.nextBoolean()){
                // Launch neutron from the side of the chamber.
                if (_rand.nextBoolean()){
                    // Left side.
                    startPosX = chamberRect.getX();
                }
                else{
                    // Right side.
                    startPosX = chamberRect.getX() + chamberRect.getWidth();
                }
                startPosY = chamberRect.getY() + (_rand.nextDouble() * chamberRect.getHeight());   
            }
            else{
                // Launch neutron from top or bottom of chamber.
                if (_rand.nextBoolean()){
                    // Top.
                    startPosY = chamberRect.getY();
                }
                else{
                    // Bottom.
                    startPosY = chamberRect.getY() + chamberRect.getHeight();
                }
                startPosX = chamberRect.getX() + (_rand.nextDouble() * chamberRect.getWidth());   
            }
            
            // Calculate a path toward the center of the chamber.
            double angle = Math.atan2( chamberRect.getCenterY() - startPosY, chamberRect.getCenterX() - startPosX );
            double startVelX = NEUTRON_VELOCITY * Math.cos( angle );
            double startVelY = NEUTRON_VELOCITY * Math.sin( angle );
            
            // Create the neutron and let any listeners know about it.
            Neutron firedNeutron = new Neutron(startPosX, startPosY, startVelX, startVelY, false);
            _freeNeutrons.add( firedNeutron );
            notifyModelElementAdded( firedNeutron );
            
            // Make sure we don't get stuck in this loop if we need to fire
            // more neutrons than we have reaction chambers.
            if ((NUMBER_OF_NEUTRONS_TO_FIRE > NUMBER_OF_REACTION_CHAMBERS) &&
                (chambersUsed.size() == NUMBER_OF_REACTION_CHAMBERS))
            {
                // Clear the list of chambers used.
                chambersUsed.clear();
            }
        }
    }
    
    /**
     * Adjust the position of the control rods.  The control rods can only be
     * moved up or down.
     * 
     * @param yDelta - Amount to move in the Y direction.
     */
    public void moveControlRods(double yDelta){
        
        int numControlRods = _controlRods.size();
        
        // Make sure that we don't move the control rods where they can't go.
        double topPosY = getReactorRect().getY() + REACTOR_WALL_WIDTH;
        double bottomPosY = getReactorRect().getY() + getReactorRect().getHeight() - REACTOR_WALL_WIDTH;
        if (numControlRods > 0){
            ControlRod controlRod = (ControlRod)_controlRods.get( 0 );
            if (controlRod.getPosition().getY() + yDelta < topPosY){
                yDelta = topPosY - controlRod.getPosition().getY();
            }
            else if (controlRod.getPosition().getY() + yDelta > bottomPosY){
                yDelta = bottomPosY - controlRod.getPosition().getY();
            }
        }
        
        // Set the actual position.
        for (int i = 0; i < numControlRods; i++){
            ControlRod controlRod = (ControlRod)_controlRods.get( i );
            controlRod.setPosition( controlRod.getPosition().getY() + yDelta );
        }
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

        int i, numNuclei;

        // Remove the free neutrons from the model and the view.
        for (i = 0; i < _freeNeutrons.size(); i++){
            notifyModelElementRemoved( _freeNeutrons.get( i ) );
        }
        _freeNeutrons.clear();
        
        // Remove the U235 nuclei from the model.
        numNuclei = _u235Nuclei.size();
        for (i = 0; i < numNuclei; i++){
            notifyModelElementRemoved( _u235Nuclei.get( i ) );
            ((AtomicNucleus)_u235Nuclei.get( i )).removedFromModel();
        }
        _u235Nuclei.clear();
        
        // Remove the U238 nuclei from the model.
        numNuclei = _u238Nuclei.size();
        for (i = 0; i < numNuclei; i++){
            notifyModelElementRemoved( _u238Nuclei.get( i ) );
            ((AtomicNucleus)_u238Nuclei.get( i )).removedFromModel();
        }
        _u238Nuclei.clear();
        
        // Remove the daughter nuclei from the model and the view.
        numNuclei = _daughterNuclei.size();
        for (i = 0; i < numNuclei; i++){
            notifyModelElementRemoved( _daughterNuclei.get( i ) );
            ((AtomicNucleus)_daughterNuclei.get( i )).removedFromModel();
        }
        _daughterNuclei.clear();
        
        // Clear out the energy accumulators.
        for (i = 0; i < _clockTicksPerSecond; i++){
            _fissionEventBins[i] = 0;
        }
        _currentBin = 0;
        _currentTemperature = 0;
        _totalEnergyReleased = 0;
        _energyReleasedPerSecond = 0;
        notifyEnergyChanged();
        
        // Add the unfissioned nuclei to the model.  The first step is to see
        // how many nuclei can fit in each chamber and their relative
        // positions.
        double reactionChamberWidth = ((Rectangle2D)_reactionChamberRects.get( 0 )).getWidth();
        double reactionChamberHeight = ((Rectangle2D)_reactionChamberRects.get( 0 )).getHeight();
        int numNucleiAcross =
            (int)(((reactionChamberWidth - (2 * MIN_DISTANCE_FROM_NUCLEI_TO_WALLS)) / MIN_INTER_NUCLEI_DISTANCE) + 1);
        int numNucleiDown =
            (int)(((reactionChamberHeight - (2 * MIN_DISTANCE_FROM_NUCLEI_TO_WALLS)) / MIN_INTER_NUCLEI_DISTANCE) + 1);
        
        // Add the U235 and U238 nuclei to each chamber.  Note that the U238
        // nuclei are present to moderate the reaction, but the educators have
        // requested that they don't appear visually to the user since this
        // makes the reactor look too cluttered, so they are not added to the
        // view.
        Point2D nucleusPosition = new Point2D.Double();
        for (i = 0; i < _reactionChamberRects.size(); i++){
            
            Rectangle2D reactionChamberRect = (Rectangle2D)_reactionChamberRects.get( i );
            double xStartPos = reactionChamberRect.getX() + MIN_DISTANCE_FROM_NUCLEI_TO_WALLS;
            double yStartPos = reactionChamberRect.getY() + MIN_DISTANCE_FROM_NUCLEI_TO_WALLS;
            
            for (int j = 0; j < numNucleiDown; j++){
                
                for (int k = 0; k < numNucleiAcross; k++){
                    
                    // Add the U235 nucleus.
                    nucleusPosition.setLocation( xStartPos + (k * MIN_INTER_NUCLEI_DISTANCE), 
                            yStartPos + (j * MIN_INTER_NUCLEI_DISTANCE) );
                    Uranium235Nucleus u235Nucleus = new Uranium235Nucleus(_clock, nucleusPosition, 0);
                    _u235Nuclei.add( u235Nucleus );
                    u235Nucleus.addListener( new Uranium235Nucleus.Adapter(){
                        public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                                ArrayList byProducts){
                            handleU235AtomicWeightChange( atomicNucleus, numProtons, numNeutrons, byProducts );
                        }
                    });
                    
                    // Add the U238 nucleus.  We don't need to listen for
                    // changes to atomic weight.  These exist primarily to
                    // moderate the overall reaction.
                    if (k < numNucleiAcross - 1){
                        nucleusPosition.setLocation( xStartPos + ((k + 0.5) * MIN_INTER_NUCLEI_DISTANCE), 
                                yStartPos + ((j + 0.5) * MIN_INTER_NUCLEI_DISTANCE) );
                        Uranium238Nucleus u238Nucleus = new Uranium238Nucleus(_clock, nucleusPosition);
                        _u238Nuclei.add( u238Nucleus );
                    }
                }
            }
        }
        
        // Reset the reaction started flag.
        _reactionStarted = false;
        
        // Let listeners know that things have been reset.
        notifyResetOccurred();
        notifyTemperatureChanged();
        notifyEnergyChanged();
    }
    
    /**
     * Get references to the nuclei currently maintained by the model.
     * 
     * @return An ArrayList containing references to the nuclei in the model.
     */
    public ArrayList getNuclei(){
        ArrayList nucleiList = new ArrayList(_u235Nuclei.size() + _u238Nuclei.size() + _daughterNuclei.size());
        nucleiList.addAll( _u235Nuclei );
        nucleiList.addAll( _u238Nuclei );
        nucleiList.addAll( _daughterNuclei );
        return nucleiList;
    }
    
    /**
     * Get a reference to the list of rectangles that represent the reaction
     * chambers.
     * 
     * @return
     */
    public ArrayList getChamberRectsReference(){
        return _reactionChamberRects;
    }
    
    public double getTotalEnergyReleased(){
        return _totalEnergyReleased;
    }
    
    public double getEnergyReleasedPerSecond(){
        return _energyReleasedPerSecond;
    }
    
    public double getTemperature(){
        return _currentTemperature;
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent){
        
        // Move any free particles that exist and check them for absorption.
        // We work from back to front to avoid any problems with removing
        // particles as we go along.
        int numFreeNeutrons = _freeNeutrons.size();
        for (int i = numFreeNeutrons - 1; i >= 0; i--){
            Nucleon freeNucleon = (Nucleon)_freeNeutrons.get( i );
            assert freeNucleon instanceof Nucleon; // Only neutrons are expected to be free in this model.
            boolean particleAbsorbed = false;
            
            // Move the neutron.
            freeNucleon.translate();
            
            // Check if the particle has gone outside the bounds of the
            // reactor and, if so, remove it from the model.
            if (!(_innerReactorRect.contains( freeNucleon.getPositionReference()))){
                // Particle is outside the bounds of the reactor, so consider
                // it to be absorbed by the wall.
                particleAbsorbed = true;
            }
            
            // Check if the particle has been absorbed by a control rod and, if
            // so, remove it.
            int numControlRods = _controlRods.size();
            for (int j = 0; (j < numControlRods) && (particleAbsorbed == false); j++){
                ControlRod controlRod = (ControlRod)_controlRods.get( j );
                if (controlRod.particleAbsorbed( freeNucleon )){
                    // The particle is absorbed by the control rod.
                    particleAbsorbed = true;
                }
            }
            
            // Check if any of the free particles have collided with a U238
            // nucleus.
            int numU238Nuclei = _u238Nuclei.size();
            for (int j = 0; (j < numU238Nuclei) && (particleAbsorbed == false); j++){
                AtomicNucleus nucleus = (AtomicNucleus)_u238Nuclei.get( j );
                if (freeNucleon.getPositionReference().distance( nucleus.getPositionReference() ) <=
                    nucleus.getDiameter() / 2)
                {
                    // The particle is within capture range - see if the nucleus can capture it.
                    particleAbsorbed = nucleus.captureParticle( freeNucleon );
                }
            }

            // Check if any of the free particles have collided with a U235
            // nucleus and, if so, give the nucleus the opportunity to absorb
            // the neutron (and possibly fission as a result).
            int numU235Nuclei = _u235Nuclei.size();
            for (int j = 0; (j < numU235Nuclei) && (particleAbsorbed == false); j++){
                AtomicNucleus nucleus = (AtomicNucleus)_u235Nuclei.get( j );
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
        }
        
        // Accumulate the total amount of energy release so far.
        double totalEnergyReleased = _totalEnergyReleased + (_u235FissionEventCount * JOULES_PER_FISSION_EVENT);
        
        // Update the bins used for calculating the energy produced per second.
        double energyPerSecond = _energyReleasedPerSecond + 
                ((_u235FissionEventCount - _fissionEventBins[_currentBin]) * JOULES_PER_FISSION_EVENT);
        _fissionEventBins[_currentBin] = _u235FissionEventCount;
        _currentBin = (_currentBin + 1) % _clockTicksPerSecond;
        
        // Clear out any accumulated errors.
        if (energyPerSecond < JOULES_PER_FISSION_EVENT){
            energyPerSecond = 0;
        }
        
        // Reset the fission event counter.
        _u235FissionEventCount = 0;
        
        // See if the internal temperature has changed and, if so, notify any
        // listeners.
        double temperature = energyPerSecond * (1/JOULES_PER_FISSION_EVENT);
        if (_currentTemperature != temperature){
            // Adjust the temperature, but not instantaneously.
            if (Math.abs( _currentTemperature - temperature ) < MAX_TEMP_CHANGE_PER_TICK){
                _currentTemperature = temperature;
            }
            else if (_currentTemperature < temperature){
                _currentTemperature += MAX_TEMP_CHANGE_PER_TICK;
            }
            else{
                _currentTemperature -= MAX_TEMP_CHANGE_PER_TICK;
            }
            notifyTemperatureChanged();
        }
        
        if ((energyPerSecond != _energyReleasedPerSecond) || (totalEnergyReleased != _totalEnergyReleased)){
            // Update our energy-related variables.
            _energyReleasedPerSecond = energyPerSecond;
            _totalEnergyReleased = totalEnergyReleased;
            notifyEnergyChanged();
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
     * Notify listeners that the model has been reset.
     */
    private void notifyResetOccurred(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).resetOccurred();
        }
    }
    
    /**
     * Notify listeners that the reaction has been started.
     */
    private void notifyReactionStarted(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).reactionStarted();
        }
    }
    
    /**
     * Notify listeners that the energy output has changed.
     */
    private void notifyEnergyChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).energyChanged();
        }
    }
    
    /**
     * Notify listeners that the internal temperature has changed.
     */
    private void notifyTemperatureChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).temperatureChanged();
        }
    }
    
    /**
     * Handle a change in atomic weight signaled by a U235 nucleus, which
     * may indicate that fission has occurred.
     * 
     * @param numProtons
     * @param numNeutrons
     * @param byProducts
     */
    private void handleU235AtomicWeightChange(AtomicNucleus nucleus, int numProtons, int numNeutrons, 
            ArrayList byProducts){
        
        if (byProducts != null){
            // There are some byproducts of this event that need to be
            // managed by this object.
        	
        	if (!_reactionStarted){
        		// Signal any listeners that the reaction has started.
        		notifyReactionStarted();
        		_reactionStarted = true;
        	}
        	
        	// Handle the by products.
            for (int i = 0; i < byProducts.size(); i++){
                Object byProduct = byProducts.get( i );
                if ((byProduct instanceof Neutron) || (byProduct instanceof Proton)){
                    // Let any listeners know that a new element has appeared
                    // separately in the model.
                    notifyModelElementAdded(byProduct);
                    
                    // Set a direction and velocity for this neutron.
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
                    notifyModelElementAdded( daughterNucleus );

                    // In this model, we just move the daughter nuclei a
                    // little and then stop them, creating an effect that
                    // illustrates the split but doesn't have too much stuff
                    // flying around.
                    double angle = (_rand.nextDouble() * Math.PI * 2);
                    double xDistance = Math.sin( angle ) * DAUGHTER_NUCLEI_SPLIT_DISTANCE;
                    double yDistance = Math.cos( angle ) * DAUGHTER_NUCLEI_SPLIT_DISTANCE;
                    nucleus.setPosition( daughterNucleus.getPositionReference().getX() + xDistance,
                            daughterNucleus.getPositionReference().getY() + yDistance );
                    daughterNucleus.setPosition( daughterNucleus.getPositionReference().getX() - xDistance,
                            daughterNucleus.getPositionReference().getY() - yDistance );
                    
                    // Add the daughter nucleus to our collection.
                    _daughterNuclei.add( daughterNucleus );
                    
                    // Move the 'parent' nucleus to the list of daughter
                    // nuclei so that it doesn't continue to be involved in
                    // the fission detection calculations.
                    assert (nucleus instanceof Uranium235Nucleus);
                    _u235Nuclei.remove( nucleus );
                    _daughterNuclei.add( nucleus );
                    
                    // Increment the count of fission events.  This will be
                    // cleared when a total tally is made.
                    _u235FissionEventCount++;
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
         * This is invoked to signal that the model has been reset.
         *
         */
        public void resetOccurred();
        
        /**
         * This signals that some aspect of the amount of energy being
         * produced by the reactor has changed.  This includes changes to the
         * internal reactor temperature.
         */
        public void energyChanged();
        
        /**
         * This signals that the internal temperature of the reactor has
         * changed.
         */
        public void temperatureChanged();
        
        /**
         * This signals that the reactor has been started.  Note that this
         * notification will not be sent until at least one nucleus has
         * decayed.
         */
        public void reactionStarted();
    }
    
    /**
     * Adapter for the interface described above.
     *
     * @author John Blanco
     */
    public static class Adapter implements Listener {
        public void modelElementAdded(Object modelElement){}
        public void modelElementRemoved(Object modelElement){}
        public void resetOccurred(){}
        public void energyChanged(){}
        public void temperatureChanged(){}
        public void reactionStarted(){}
    }
}
