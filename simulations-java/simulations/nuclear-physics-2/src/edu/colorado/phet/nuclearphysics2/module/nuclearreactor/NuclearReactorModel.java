/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.model.Nucleon;
import edu.colorado.phet.nuclearphysics2.model.Proton;
import edu.colorado.phet.nuclearphysics2.model.Uranium235Nucleus;

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
    // assumes that the 'center of the world' is at (0,0).
    private static final Point2D REACTOR_POSITION = new Point2D.Double(-(OVERALL_REACTOR_WIDTH / 2), 
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

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private NuclearPhysics2Clock _clock;
    private ArrayList _listeners;
    private ArrayList _u235Nuclei;
    private ArrayList _daughterNuclei;
    private ArrayList _freeNeutrons;
    private ArrayList _controlRods;
    private ArrayList _reactionChamberRects;
    private Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public NuclearReactorModel(NuclearPhysics2Clock clock)
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
        _daughterNuclei       = new ArrayList();
        _freeNeutrons         = new ArrayList();
        _reactionChamberRects = new ArrayList(NUMBER_OF_REACTION_CHAMBERS);
        _controlRods          = new ArrayList(NUMBER_OF_REACTION_CHAMBERS - 1);
        
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
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock(){
        return _clock;
    }
    
    public int getNumU235Nuclei(){
        return _u235Nuclei.size();
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
    
    /**
     * Get a reference to the array of Uranium 235 nuclei maintained by this
     * model.
     * 
     * @return
     */
    public ArrayList getU235NucleiReference(){
        return _u235Nuclei;
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

        // Get rid of all the existing nuclei and free nucleons.
        int i;
        
        for (i = 0; i < _freeNeutrons.size(); i++){
            notifyModelElementRemoved( _freeNeutrons.get( i ) );
        }
        _freeNeutrons.removeAll( _freeNeutrons );
        
        for (i = 0; i < _u235Nuclei.size(); i++){
            notifyModelElementRemoved( _u235Nuclei.get( i ) );
            ((AtomicNucleus)_u235Nuclei.get( i )).removedFromModel();
        }
        _u235Nuclei.removeAll( _u235Nuclei );
        
        for (i = 0; i < _daughterNuclei.size(); i++){
            notifyModelElementRemoved( _daughterNuclei.get( i ) );
            ((AtomicNucleus)_daughterNuclei.get( i )).removedFromModel();
        }
        _daughterNuclei.removeAll( _daughterNuclei );
        
        // Set ourself back to the original state.  The first step is to see
        // how many nuclei can fit in each chamber and their relative
        // positions.
        double reactionChamberWidth = ((Rectangle2D)_reactionChamberRects.get( 0 )).getWidth();
        double reactionChamberHeight = ((Rectangle2D)_reactionChamberRects.get( 0 )).getHeight();
        int numNucleiAcross = 
            (int)(((reactionChamberWidth - (2 * MIN_DISTANCE_FROM_NUCLEI_TO_WALLS)) / MIN_INTER_NUCLEI_DISTANCE) + 1);
        int numNucleiDown = 
            (int)(((reactionChamberHeight - (2 * MIN_DISTANCE_FROM_NUCLEI_TO_WALLS)) / MIN_INTER_NUCLEI_DISTANCE) + 1);
        
        // Add the nuclei to each chamber.
        Point2D nucleusPosition = new Point2D.Double();
        for (i = 0; i < _reactionChamberRects.size(); i++){
            
            Rectangle2D reactionChamberRect = (Rectangle2D)_reactionChamberRects.get( i );
            double xStartPos = reactionChamberRect.getX() + MIN_DISTANCE_FROM_NUCLEI_TO_WALLS;
            double yStartPos = reactionChamberRect.getY() + MIN_DISTANCE_FROM_NUCLEI_TO_WALLS;
            
            for (int j = 0; j < numNucleiAcross; j++){
                
                for (int k = 0; k < numNucleiDown; k++){
                    
                    nucleusPosition.setLocation( xStartPos + (j * MIN_INTER_NUCLEI_DISTANCE), 
                            yStartPos + (k * MIN_INTER_NUCLEI_DISTANCE) );
                    Uranium235Nucleus nucleus = new Uranium235Nucleus(_clock, nucleusPosition, 0);
                    _u235Nuclei.add( nucleus );
                    nucleus.addListener( new Uranium235Nucleus.Adapter(){
                        public void atomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                                ArrayList byProducts){
                            handleU235AtomicWeightChange( atomicNucleus, numProtons, numNeutrons, byProducts );
                        }
                    });
                }
            }
        }
        
        // Let listeners know that a reset has occurred.
        notifyResetOccurred();
    }
    
    /**
     * Get references to the nuclei currently maintained by the model.
     * 
     * @return An ArrayList containing references to the nuclei in the model.
     */
    public ArrayList getNuclei(){
        ArrayList nucleiList = new ArrayList(_u235Nuclei.size() + _daughterNuclei.size());
        nucleiList.addAll( _u235Nuclei );
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
            assert freeNucleon instanceof Nucleon; // Only neutrons are expected in this model.
            boolean particleAbsorbed = false;
            
            // Move the neutron.
            freeNucleon.translate();
            
            // Check if the particle has gone outside of the reactor and, if
            // so, remove it from the model.
            if (!(getReactorRect().contains( freeNucleon.getPositionReference()))){
                // Particle is out of bounds, so blow it away.
                notifyModelElementRemoved( freeNucleon );
                _freeNeutrons.remove( i );
                continue;
            }
            
            // Check if the particle has been absorbed by a control rod and, if
            // so, remove it.
            int numControlRods = _controlRods.size();
            for (int j = 0; (j < numControlRods) && (particleAbsorbed == false); j++){
                ControlRod controlRod = (ControlRod)_controlRods.get( j );
                if (controlRod.particleAbsorbed( freeNucleon )){
                    // The particle is absorbed, so delete it from the model.
                    notifyModelElementRemoved( freeNucleon );
                    _freeNeutrons.remove( i );
                    continue;
                }
            }

            // Check if any of the free particles have collided with a nucleus
            // and, if so, give the nucleus the opportunity to absorb the
            // neutron (and possibly fission as a result).
            particleAbsorbed = false;
            int numNuclei = _u235Nuclei.size();
            for (int j = 0; (j < numNuclei) && (particleAbsorbed == false); j++){
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
    }
}
