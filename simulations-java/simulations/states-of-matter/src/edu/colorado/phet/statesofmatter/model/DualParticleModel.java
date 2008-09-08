/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
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

    private static final double TIME_STEP = Math.pow( 0.5, 5.0 );
    
    public static final int DEFAULT_MOLECULE = StatesOfMatterConstants.MONATOMIC_OXYGEN;
    public static final double DEFAULT_SIGMA = OxygenAtom.getSigma();
    public static final double DEFAULT_EPSILON = OxygenAtom.getEpsilon();
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    IClock m_clock;
    private ArrayList m_listeners = new ArrayList();
    private StatesOfMatterAtom m_fixedParticle;
    private StatesOfMatterAtom m_movableParticle;
    private double m_fixedParticleHorizForce;
    private double m_movableParticleHorizForce;
    private double m_epsilon;  // Epsilon represents the interaction strength.
    private double m_sigma;    // Sigma represents the diameter of the molecule, roughly speaking.
    private int m_currentMoleculeID;
    private boolean m_particleMotionPaused;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public DualParticleModel(IClock clock) {
        
        m_clock = clock;
        m_epsilon = DEFAULT_EPSILON;
        m_sigma = DEFAULT_SIGMA;
        m_particleMotionPaused = true;
        
        // Register as a clock listener.
        clock.addClockListener(new ClockAdapter(){
            
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset( ClockEvent clockEvent ) {
                reset();
            }
        });
        
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
    
    public double getFixedParticleHorizForce(){
        return m_fixedParticleHorizForce;
    }

    public double getMovableParticleHorizForce(){
        return m_movableParticleHorizForce;
    }
    
    public int getMoleculeType(){
        return m_currentMoleculeID;
    }
    
    public void setMoleculeType(int atomID){
        
        // Verify that this is a supported value.
        if ((atomID != StatesOfMatterConstants.USER_DEFINED_MOLECULE) &&
            (atomID != StatesOfMatterConstants.ARGON) &&
            (atomID != StatesOfMatterConstants.MONATOMIC_OXYGEN)){
            
            System.err.println("Error: Unsupported molecule type.");
            assert false;
            atomID = StatesOfMatterConstants.MONATOMIC_OXYGEN;
        }
        
        // Inform any listeners of the removal of existing particles.
        if (m_fixedParticle != null){
            notifyFixedParticleRemoved( m_fixedParticle );
            m_fixedParticle = null;
        }
        if (m_movableParticle != null){
            notifyMovableParticleRemoved( m_movableParticle );
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
        case StatesOfMatterConstants.MONATOMIC_OXYGEN:
            m_fixedParticle = new OxygenAtom(0, 0);
            m_movableParticle = new OxygenAtom(0, 0);
            m_sigma = OxygenAtom.getSigma();
            m_epsilon = OxygenAtom.getEpsilon();
            break;
        }
        
        m_currentMoleculeID = atomID;
        
        // Let listeners know about the new molecules.
        notifyFixedParticleAdded( m_fixedParticle );
        notifyMovableParticleAdded( m_movableParticle );
        
        // Let listeners know about parameter changes.
        notifyInteractionPotentialChanged();
        
        // Let listeners know about the diameter change.
        notifyParticleDiameterChanged();
        
        // Move them to be initially separated.
        double diameter = m_fixedParticle.getRadius() * 2;
        m_fixedParticle.setPosition( 0, 0 );
        m_movableParticle.setPosition( 2 * diameter, 0 );
        
        // Let listeners know that the molecule type has changed.
        notifyMoleculeTypeChanged();
    }
    
    /**
     * Set the sigma value, a.k.a. the Molecular Diameter Parameter, which is
     * one of the two parameters that describes the Lennard-Jones potential.
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
        
        notifyInteractionPotentialChanged();
        m_fixedParticle.setRadius( m_sigma / 2 );
        m_movableParticle.setRadius( m_sigma / 2 );
        notifyInteractionPotentialChanged();
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
        m_particleMotionPaused = true;
        setMoleculeType( DEFAULT_MOLECULE );
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
        
        // Execute the force calculation.
        updateForce();

        // Update the position of the particle.
        updatePosition();
    }
    
    private void updateForce(){
        
        double distance = m_movableParticle.getPositionReference().distance( m_fixedParticle.getPositionReference() );
        
        if (distance < m_sigma){
            // The particles are too close together, and calculating the force
            // will cause unusable levels of speed later, so we limit it.
            distance = m_sigma;
        }
        
        // Calculate the force.  The result should be in newtons.
        m_movableParticleHorizForce = 
            ((48 * (m_epsilon * StatesOfMatterConstants.K_BOLTZMANN) * Math.pow( m_sigma, 12 ) / Math.pow( distance, 13 )) -
             (24 * (m_epsilon * StatesOfMatterConstants.K_BOLTZMANN) * Math.pow( m_sigma, 6 ) / Math.pow( distance, 7 )));
    }
    
    private void updatePosition(){
        
        double mass = m_movableParticle.getMass() * 1.6605402E-27;  // Convert mass to kilograms.
        double acceleration = m_movableParticleHorizForce / mass;
        
        // Update the acceleration for the movable particle.  We do this
        // regardless of whether movement is paused so that the force vectors
        // can be shown appropriately if the user moves the particles.
        m_movableParticle.setAx( acceleration );
        
        if (!m_particleMotionPaused){
            // Update the position and velocity of the particle.
            m_movableParticle.setVx( m_movableParticle.getVx() + (acceleration * TIME_STEP) );
            double xPos = m_movableParticle.getPositionReference().getX() + (m_movableParticle.getVx() * TIME_STEP);
            m_movableParticle.setPosition( xPos, 0 );
        }
        
        // Update the acceleration of the fixed particle so that the force
        // acting on it can be displayed.
        // TODO JPB TBD - This seems a little odd to me, but I believe that
        // it is what was specified.  Isn't it likely to confuse people if
        // they see that a force is acting on the particle but it doesn't move?
        m_fixedParticle.setAx( -acceleration );
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
