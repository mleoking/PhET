/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.defaults.InteractionPotentialDefaults;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.model.particle.UserDefinedAtom;

/**
 * This is the model for two particles interacting with a Lennard-Jones
 * interaction potential.
 *
 * @author John Blanco
 */
public class DualParticleModel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final int DEFAULT_MOLECULE = StatesOfMatterConstants.NEON;
    public static final double DEFAULT_SIGMA = NeonAtom.getSigma();
    public static final double DEFAULT_EPSILON = NeonAtom.getEpsilon();
    public static final int CALCULATIONS_PER_TICK = 8;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    IClock m_clock;
    private ArrayList m_listeners = new ArrayList();
    private StatesOfMatterAtom m_fixedParticle;
    private StatesOfMatterAtom m_movableParticle;
    private StatesOfMatterAtom m_shadowMovableParticle;
    private double m_attractiveForce;
    private double m_repulsiveForce;
    private double m_epsilon;  // Epsilon represents the interaction strength.
    private double m_sigma;    // Sigma represents the diameter of the molecule, roughly speaking.
    private int m_currentMoleculeID;
    private boolean m_particleMotionPaused;
    private LjPotentialCalculator m_ljPotentialCalculator;
    private double m_timeStep;
    private StatesOfMatterAtom.Adapter m_movableParticleListener;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public DualParticleModel(IClock clock) {
        
        m_clock = clock;
        m_timeStep = InteractionPotentialDefaults.CLOCK_DT / 1000 / CALCULATIONS_PER_TICK;
        m_epsilon = DEFAULT_EPSILON;
        m_sigma = DEFAULT_SIGMA;
        m_particleMotionPaused = false;
        m_ljPotentialCalculator = new LjPotentialCalculator( m_epsilon, m_sigma );
        
        // Register as a clock listener.
        clock.addClockListener(new ClockAdapter(){
            
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset( ClockEvent clockEvent ) {
                reset();
            }
        });
        
        // Create a listener for detecting when the movable particle is moved
        // directly by the user.
        m_movableParticleListener = new StatesOfMatterAtom.Adapter() {
            public void positionChanged(){
                if (m_particleMotionPaused == true) {
                    // The user must be moving the particle from the view.
                    // Update the forces correspondingly.
                    m_shadowMovableParticle = (StatesOfMatterAtom)m_movableParticle.clone();
                    updateForces();
                }
            };
        };
        
        // Don't bother adding molecules since the model will be reset as part
        // of the initialization process.
    }

    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    
    public StatesOfMatterAtom getFixedParticleRef(){
        return m_fixedParticle;
    }
    
    public StatesOfMatterAtom getMovableParticleRef(){
        return m_movableParticle;
    }
    
    public double getAttractiveForce(){
        return m_attractiveForce;
    }
    
    public double getRepulsiveForce(){
        return m_repulsiveForce;
    }
    
    public int getMoleculeType(){
        return m_currentMoleculeID;
    }
    
    public void setMoleculeType(int atomID){
        
        // Verify that this is a supported value.
        if ((atomID != StatesOfMatterConstants.USER_DEFINED_MOLECULE) &&
            (atomID != StatesOfMatterConstants.ARGON) &&
            (atomID != StatesOfMatterConstants.NEON)){
            
            System.err.println("Error: Unsupported molecule type.");
            assert false;
            atomID = StatesOfMatterConstants.NEON;
        }
        
        // Inform any listeners of the removal of existing particles.
        if (m_fixedParticle != null){
            notifyFixedParticleRemoved( m_fixedParticle );
            m_fixedParticle = null;
        }
        if (m_movableParticle != null){
            notifyMovableParticleRemoved( m_movableParticle );
            m_movableParticle.removeListener( m_movableParticleListener );
            m_movableParticle = null;
        }
        
        // Set the new atoms based on the requested type..
        switch (atomID){
        case StatesOfMatterConstants.USER_DEFINED_MOLECULE:
            m_fixedParticle = new UserDefinedAtom(0, 0);
            m_movableParticle = new UserDefinedAtom(0, 0);
            m_sigma = UserDefinedAtom.getSigma();
            m_epsilon = UserDefinedAtom.getEpsilon();
            break;
        case StatesOfMatterConstants.ARGON:
            m_fixedParticle = new ArgonAtom(0, 0);
            m_movableParticle = new ArgonAtom(0, 0);
            m_sigma = ArgonAtom.getSigma();
            m_epsilon = ArgonAtom.getEpsilon();
            break;
        case StatesOfMatterConstants.NEON:
            m_fixedParticle = new NeonAtom(0, 0);
            m_movableParticle = new NeonAtom(0, 0);
            m_sigma = NeonAtom.getSigma();
            m_epsilon = NeonAtom.getEpsilon();
            break;
        }
        
        m_currentMoleculeID = atomID;
        
        // Register to listen to motion of the movable particle so that we can
        // tell when the user is moving it.
        m_movableParticle.addListener( m_movableParticleListener );
        
        // Update our Lennard-Jones force calculator.
        m_ljPotentialCalculator.setEpsilon( m_epsilon );
        m_ljPotentialCalculator.setSigma( m_sigma );
        
        // Let listeners know about the new molecules.
        notifyFixedParticleAdded( m_fixedParticle );
        notifyMovableParticleAdded( m_movableParticle );
        
        // Let listeners know about parameter changes.
        notifyInteractionPotentialChanged();
        
        // Let listeners know about the diameter change.
        notifyParticleDiameterChanged();
        
        // Move the particles to their initial positions.
        m_fixedParticle.setPosition( 0, 0 );
        resetMovableParticlePos();
        
        // Let listeners know that the molecule type has changed.
        notifyMoleculeTypeChanged();
    }
    
    /**
     * Set the sigma value, a.k.a. the Molecular Diameter Parameter, which is
     * one of the two parameters that are used for calculating the Lennard-
     * Jones potential.
     * 
     * @param sigma - distance parameter
     */
    public void setSigma( double sigma ){
        if (sigma > StatesOfMatterConstants.MAX_SIGMA){
            m_sigma = StatesOfMatterConstants.MAX_SIGMA;
        }
        else if ( sigma < StatesOfMatterConstants.MIN_SIGMA ){
            m_sigma = StatesOfMatterConstants.MIN_SIGMA;
        }
        else{
            m_sigma = sigma;
        }
        
        m_ljPotentialCalculator.setSigma( m_sigma );
        notifyInteractionPotentialChanged();
        m_fixedParticle.setRadius( m_sigma / 2 );
        m_movableParticle.setRadius( m_sigma / 2 );
        notifyParticleDiameterChanged();
    }
    
    /**
     * Get the sigma value, a.k.a. the Molecular Diameter Parameter, which is
     * one of the two parameters that describes the Lennard-Jones potential.
     * 
     * @return
     */
    public double getSigma(){
        return m_sigma;
    }
    
    /**
     * Set the epsilon value, a.k.a. the Interaction Strength Paramter, which 
     * is one of the two parameters that describes the Lennard-Jones potential.
     * 
     * @param sigma - distance parameter
     */
    public void setEpsilon( double epsilon ){
        
        if (epsilon > StatesOfMatterConstants.MAX_EPSILON){
            m_epsilon = StatesOfMatterConstants.MAX_EPSILON;
        }
        else if ( epsilon < StatesOfMatterConstants.MIN_EPSILON ){
            m_epsilon = StatesOfMatterConstants.MIN_EPSILON;
        }
        else{
            m_epsilon = epsilon;
        }
        
        m_ljPotentialCalculator.setEpsilon( m_epsilon );
        notifyInteractionPotentialChanged();
    }
    
    /**
     * Get the epsilon value, a.k.a. the Interaction Strength Paramter, which 
     * is one of the two parameters that describes the Lennard-Jones potential.
     * 
     * @return
     */
    public double getEpsilon(){
        return m_epsilon;
    }
    
    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Reset the model.
     */
    public void reset() {
        
        // Initialize the system parameters.
        m_particleMotionPaused = false;
        setMoleculeType( DEFAULT_MOLECULE );
    }
    
    /**
     * Put the movable particle back to the location where the force is
     * minimized, and reset the velocity and acceleration to 0.
     */
    public void resetMovableParticlePos() {
        m_movableParticle.setPosition( m_ljPotentialCalculator.calculateMinimumForceDistance(), 0 );
        m_movableParticle.setVx( 0 );
        m_movableParticle.setAx( 0 );
    }
    
    public void addListener(Listener listener){
        
        if (m_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        m_listeners.add( listener );
    }
    
    public boolean removeListener(Listener listener){
        return m_listeners.remove( listener );
    }
    
    public void setParticleMotionPaused(boolean paused){
        m_particleMotionPaused = paused;
        m_movableParticle.setVx( 0 );
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent) {

        m_shadowMovableParticle = (StatesOfMatterAtom)m_movableParticle.clone();
        
        for (int i = 0; i < CALCULATIONS_PER_TICK; i++) {

            // Execute the force calculation.
            updateForces();
            
            // Update the motion information.
            updateParticleMotion();
        }
        
        // Update the particle that is visible to the view.
        syncMovableParticleWithDummy();
    }

    /**
     * 
     */
    private void syncMovableParticleWithDummy() {
        m_movableParticle.setAx( m_shadowMovableParticle.getAx() );
        m_movableParticle.setVx( m_shadowMovableParticle.getVx() );
        m_movableParticle.setPosition( m_shadowMovableParticle.getX(), m_shadowMovableParticle.getY() );
    }
    
    private void updateForces(){
        
        double distance = m_shadowMovableParticle.getPositionReference().distance( m_fixedParticle.getPositionReference() );
        
        if (distance < m_sigma / 2){
            // The particles are too close together, and calculating the force
            // will cause unusable levels of speed later, so we limit it.
            distance = m_sigma / 2;
        }
        
        // Calculate the force.  The result should be in newtons.
        m_attractiveForce = m_ljPotentialCalculator.calculateAttractiveLjForce( distance );
        m_repulsiveForce = m_ljPotentialCalculator.calculateRepulsiveLjForce( distance );
    }
    
    /**
     * Update the position, velocity, and acceleration of the dummy movable particle.
     */
    private void updateParticleMotion(){
        
        double mass = m_shadowMovableParticle.getMass() * 1.6605402E-27;  // Convert mass to kilograms.
        double acceleration = (m_repulsiveForce - m_attractiveForce) / mass;
        
        // Update the acceleration for the movable particle.  We do this
        // regardless of whether movement is paused so that the force vectors
        // can be shown appropriately if the user moves the particles.
        m_shadowMovableParticle.setAx( acceleration );
        
        if (!m_particleMotionPaused){
            // Update the position and velocity of the particle.
            m_shadowMovableParticle.setVx( m_shadowMovableParticle.getVx() + (acceleration * m_timeStep) );
            double xPos = m_shadowMovableParticle.getPositionReference().getX() + (m_shadowMovableParticle.getVx() * m_timeStep);
            m_shadowMovableParticle.setPosition( xPos, 0 );
        }
    }
    
    private void notifyFixedParticleAdded(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).fixedParticleAdded( particle );
        }        
    }
    
    private void notifyMovableParticleAdded(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).movableParticleAdded( particle );
        }        
    }
    
    private void notifyFixedParticleRemoved(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).fixedParticleRemoved( particle );
        }        
    }
    
    private void notifyMovableParticleRemoved(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).movableParticleRemoved( particle );
        }        
    }
    
    private void notifyInteractionPotentialChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).interactionPotentialChanged();
        }        
    }
    
    private void notifyParticleDiameterChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).particleDiameterChanged();
        }        
    }
    
    private void notifyMoleculeTypeChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).moleculeTypeChanged();
        }        
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------
    
    public static interface Listener {
        
        /**
         * Inform listeners that the model has been reset.
         */
        public void fixedParticleAdded(StatesOfMatterAtom particle);
        public void movableParticleAdded(StatesOfMatterAtom particle);
        public void fixedParticleRemoved(StatesOfMatterAtom particle);
        public void movableParticleRemoved(StatesOfMatterAtom particle);
        public void interactionPotentialChanged();
        public void particleDiameterChanged();
        public void moleculeTypeChanged();
    }
    
    public static class Adapter implements Listener {
        public void fixedParticleAdded(StatesOfMatterAtom particle){}
        public void movableParticleAdded(StatesOfMatterAtom particle){}
        public void fixedParticleRemoved(StatesOfMatterAtom particle){}
        public void movableParticleRemoved(StatesOfMatterAtom particle){}
        public void interactionPotentialChanged(){};
        public void particleDiameterChanged(){};
        public void moleculeTypeChanged(){};
    }
}
