/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.defaults.InteractionPotentialDefaults;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

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

    public static final AtomType DEFAULT_ATOM_TYPE = AtomType.NEON;
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
    private AtomType m_fixedMoleculeType = DEFAULT_ATOM_TYPE;
    private AtomType m_movableMoleculeType = DEFAULT_ATOM_TYPE;
    private boolean m_particleMotionPaused;
    private LjPotentialCalculator m_ljPotentialCalculator;
    private double m_timeStep;
    private StatesOfMatterAtom.Adapter m_movableParticleListener;
    private boolean m_settingBothAtomsToAdjustable = false;  // Flag used to prevent getting in disallowed state.
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public DualParticleModel(IClock clock) {
        
        m_clock = clock;
        m_timeStep = InteractionPotentialDefaults.CLOCK_DT / 1000 / CALCULATIONS_PER_TICK;
        m_particleMotionPaused = false;
        m_ljPotentialCalculator = new LjPotentialCalculator( StatesOfMatterConstants.MIN_SIGMA, 
        		StatesOfMatterConstants.MIN_EPSILON ); // Initial values arbitrary, will be set during reset.
        
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

        // Put the model into its initial state.
        reset();
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
    
    public AtomType getFixedMoleculeType(){
        return m_fixedMoleculeType;
    }
    
    public AtomType getMovableMoleculeType(){
        return m_movableMoleculeType;
    }
    
    public void setFixedMoleculeType(AtomType atomType){
    	
    	if (((atomType == AtomType.ADJUSTABLE && m_movableMoleculeType != AtomType.ADJUSTABLE) ||
    		 (atomType != AtomType.ADJUSTABLE && m_movableMoleculeType == AtomType.ADJUSTABLE)) &&
        	  !m_settingBothAtomsToAdjustable){
    		System.err.println(this.getClass().getName() + " - Error: Cannot set just one atom to be adjustable, ignoring request.");
    		return;
    	}
    	ensureValidMoleculeType( atomType );

    	// Inform any listeners of the removal of existing particles.
        if (m_fixedParticle != null){
            notifyFixedParticleRemoved( m_fixedParticle );
            m_fixedParticle = null;
        }

        m_fixedMoleculeType = atomType;
        m_fixedParticle = AtomFactory.createAtom(atomType);

        // TODO: Setting sigma as the average of the two molecules.  Not sure
        // if this is valid, need to check with the physicists.
        if (m_movableParticle != null){
            m_ljPotentialCalculator.setSigma( (m_movableParticle.getSigma() + m_fixedParticle.getSigma()) / 2 );
        }
        else{
            m_ljPotentialCalculator.setSigma( m_fixedParticle.getSigma() );
        }

        notifyFixedParticleAdded( m_fixedParticle );
        notifyInteractionPotentialChanged();
        notifyFixedParticleDiameterChanged();
        m_fixedParticle.setPosition( 0, 0 );

        resetMovableParticlePos();
    }

    public void setMovableMoleculeType(AtomType atomType){
    	
    	if (((atomType == AtomType.ADJUSTABLE && m_fixedMoleculeType != AtomType.ADJUSTABLE) ||
       		 (atomType != AtomType.ADJUSTABLE && m_fixedMoleculeType == AtomType.ADJUSTABLE)) &&
           	  !m_settingBothAtomsToAdjustable){
    		System.err.println(this.getClass().getName() + " - Error: Cannot set just one atom to be adjustable, ignoring request.");
    		return;
    	}
    	ensureValidMoleculeType( atomType );

    	if (m_movableParticle != null){
            notifyMovableParticleRemoved( m_movableParticle );
            m_movableParticle.removeListener( m_movableParticleListener );
            m_movableParticle = null;
        }
    	
        m_movableMoleculeType = atomType;
    	m_movableParticle = AtomFactory.createAtom(atomType);
    	
        // Register to listen to motion of the movable particle so that we can
        // tell when the user is moving it.
        m_movableParticle.addListener( m_movableParticleListener );
        
        // TODO: Setting sigma as the average of the two molecules.  Not sure
        // if this is valid, need to check with the physicists.
        if (m_fixedParticle != null){
            m_ljPotentialCalculator.setSigma( (m_movableParticle.getSigma() + m_fixedParticle.getSigma()) / 2 );
        }
        else{
            m_ljPotentialCalculator.setSigma( m_movableParticle.getSigma() );
        }

        m_ljPotentialCalculator.setEpsilon(determineEpsilon());
        
        resetMovableParticlePos();

        notifyMovableParticleAdded( m_movableParticle );
        notifyInteractionPotentialChanged();
        notifyMovableParticleDiameterChanged();
        resetMovableParticlePos();
    }

	private void ensureValidMoleculeType(AtomType moleculeType) {
		// Verify that this is a supported value.
        if ((moleculeType != AtomType.NEON) &&
            (moleculeType != AtomType.ARGON) &&
            (moleculeType != AtomType.OXYGEN) &&
            (moleculeType != AtomType.ADJUSTABLE)){
            
            System.err.println("Error: Unsupported molecule type.");
            assert false;
            moleculeType = AtomType.NEON;
        }
	}
	
	/**
	 * Determine the value that should be used for epsilon (the interaction
	 * potential) based on the two atoms being used.
	 * @return
	 */
	private double determineEpsilon(){
		
		double epsilon = 0;
		
		if ( m_fixedMoleculeType == m_movableMoleculeType ){
			epsilon = m_fixedParticle.getEpsilon();
		}
		else{
			// This is a heterogeneous situation, and epsilon is unique for each combination.
			if (((m_fixedMoleculeType == AtomType.ARGON) && (m_movableMoleculeType == AtomType.NEON)) ||
				((m_fixedMoleculeType == AtomType.NEON) && (m_movableMoleculeType == AtomType.ARGON))){
				epsilon = 54.12;
			}
			else{
				// TODO: For not the epsilon value with be the average of the values for two interacting atoms of
				// the same type.  This is almost certainly not physically valid, so we need to work with the
				// physicists to get better values.
				epsilon = (m_fixedParticle.getEpsilon() + m_movableParticle.getEpsilon()) / 2;
			}
		}
		
		return epsilon;
	}
    
    public void setBothMoleculeTypes(AtomType atomType){
        
    	if (atomType == AtomType.ADJUSTABLE){
        	m_settingBothAtomsToAdjustable = true;
    	}
        setFixedMoleculeType(atomType);
        setMovableMoleculeType(atomType);
    	m_settingBothAtomsToAdjustable = false;
    }
    
    /**
     * Set the sigma value, a.k.a. the Molecular Diameter Parameter, for the
     * adjustable molecule.  This is one of the two parameters that are used
     * for calculating the Lennard-Jones potential.  If an attempt is made to
     * set this value when the adjustable atom is not selected, it is ignored.
     * 
     * @param sigma - distance parameter
     */
    public void setAdjustableParticleSigma( double sigma ){
    	if ((m_fixedMoleculeType == AtomType.ADJUSTABLE) && 
    		(m_movableMoleculeType == AtomType.ADJUSTABLE) &&
    		(sigma != m_ljPotentialCalculator.getSigma())){
    		
    		// TODO - Need to work out how this works, commenting out for now.
//    		m_fixedParticle.setSigma(sigma);
//    		m_movableMoleculeType.setSigma(sigma);
            m_ljPotentialCalculator.setSigma( sigma );
            notifyInteractionPotentialChanged();
            m_fixedParticle.setRadius( sigma / 2 );
            notifyFixedParticleDiameterChanged();
            m_movableParticle.setRadius( sigma / 2 );
            notifyMovableParticleDiameterChanged();
    	}
    }
    
    /**
     * Get the value of the sigma parameter that is being used for the motion
     * calculations.  If the molecules are the same, it will be the diameter
     * of one particle.  If they are not, it will be a function of the
     * diameters.
     * 
     * @return
     */
    public double getSigma(){
        return m_ljPotentialCalculator.getSigma();
    }
    
    /**
     * Set the epsilon value, a.k.a. the Interaction Strength Parameter, which 
     * is one of the two parameters that describes the Lennard-Jones potential.
     * 
     * @param sigma - distance parameter
     */
    public void setEpsilon( double epsilon ){
        
    	if ((m_fixedMoleculeType == AtomType.ADJUSTABLE) && 
       		(m_movableMoleculeType == AtomType.ADJUSTABLE)){

    		// TODO: Do I adjust the atoms themselves, or just the overall value?
    		
            m_ljPotentialCalculator.setEpsilon( determineEpsilon() );
            notifyInteractionPotentialChanged();
       	}
    }
    
    /**
     * Get the epsilon value, a.k.a. the Interaction Strength Paramter, which 
     * is one of the two parameters that describes the Lennard-Jones potential.
     * 
     * @return
     */
    public double getEpsilon(){
        return determineEpsilon();
    }
    
    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Reset the model.
     */
    public void reset() {

        if ( m_fixedParticle == null || m_fixedParticle.getType() != DEFAULT_ATOM_TYPE ||
        	 m_movableParticle == null || m_movableParticle.getType() != DEFAULT_ATOM_TYPE){
        	setBothMoleculeTypes(DEFAULT_ATOM_TYPE);
        }
        else{
        	resetMovableParticlePos();
        }

        // Make sure we are not paused.
        m_particleMotionPaused = false;
    }
    
    /**
     * Put the movable particle back to the location where the force is
     * minimized, and reset the velocity and acceleration to 0.
     */
    public void resetMovableParticlePos() {
    	if ( m_movableParticle != null ){
	        m_movableParticle.setPosition( m_ljPotentialCalculator.calculateMinimumForceDistance(), 0 );
	        m_movableParticle.setVx( 0 );
	        m_movableParticle.setAx( 0 );
    	}
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

    public boolean getParticleMotionPaused(){
        return m_particleMotionPaused;
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
        
        if (distance < (m_fixedParticle.getSigma() + m_movableParticle.getSigma()) / 4){
            // The particles are too close together, and calculating the force
            // will cause unusable levels of speed later, so we limit it.
            distance = (m_fixedParticle.getSigma() + m_movableParticle.getSigma()) / 4;
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
    
    private void notifyFixedParticleDiameterChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).fixedParticleDiameterChanged();
        }        
    }
    
    private void notifyMovableParticleDiameterChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).movableParticleDiameterChanged();
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
        public void fixedParticleDiameterChanged();
        public void movableParticleDiameterChanged();
    }
    
    public static class Adapter implements Listener {
        public void fixedParticleAdded(StatesOfMatterAtom particle){}
        public void movableParticleAdded(StatesOfMatterAtom particle){}
        public void fixedParticleRemoved(StatesOfMatterAtom particle){}
        public void movableParticleRemoved(StatesOfMatterAtom particle){}
        public void interactionPotentialChanged(){};
        public void fixedParticleDiameterChanged(){};
        public void movableParticleDiameterChanged(){};
    }
}
