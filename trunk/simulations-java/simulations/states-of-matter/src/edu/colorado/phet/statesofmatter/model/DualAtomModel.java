/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.defaults.InteractionPotentialDefaults;
import edu.colorado.phet.statesofmatter.model.particle.ConfigurableStatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

/**
 * This is the model for two atoms interacting with a Lennard-Jones
 * interaction potential.
 *
 * @author John Blanco
 */
public class DualAtomModel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final int BONDING_STATE_UNBONDED = 0;
    public static final int BONDING_STATE_BONDING = 1;
    public static final int BONDING_STATE_BONDED = 2;

    private static final AtomType DEFAULT_ATOM_TYPE = AtomType.NEON;
    private static final int CALCULATIONS_PER_TICK = 8;
    private static final double BONDED_VELOCITY = 20;  // Velocity assigned to atom after bond forms. 
    private static final double THRESHOLD_VELOCITY = 100;  // Used to distinguish small oscillations from real movement. 
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    IClock m_clock;
    private ArrayList m_listeners = new ArrayList();
    private StatesOfMatterAtom m_fixedAtom;
    private StatesOfMatterAtom m_movableAtom;
    private StatesOfMatterAtom m_shadowMovableAtom;
    private double m_attractiveForce;
    private double m_repulsiveForce;
    private AtomType m_fixedMoleculeType = DEFAULT_ATOM_TYPE;
    private AtomType m_movableMoleculeType = DEFAULT_ATOM_TYPE;
    private boolean m_motionPaused;
    private LjPotentialCalculator m_ljPotentialCalculator;
    private double m_timeStep;
    private StatesOfMatterAtom.Adapter m_movableAtomListener;
    private boolean m_settingBothAtomTypes = false;  // Flag used to prevent getting in disallowed state.
    private int m_bondingState = BONDING_STATE_UNBONDED; // Tracks whether the atoms have formed a chemical bond.
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public DualAtomModel(IClock clock) {
        
        m_clock = clock;
        m_timeStep = InteractionPotentialDefaults.CLOCK_DT / 1000 / CALCULATIONS_PER_TICK;
        m_motionPaused = false;
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
        
        // Create a listener for detecting when the movable atom is moved
        // directly by the user.
        m_movableAtomListener = new StatesOfMatterAtom.Adapter() {
            public void positionChanged(){
                if (m_motionPaused == true) {
                    // The user must be moving the atom from the view.
                    // Update the forces correspondingly.
                    m_shadowMovableAtom = (StatesOfMatterAtom)m_movableAtom.clone();
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
    
    public StatesOfMatterAtom getFixedAtomRef(){
        return m_fixedAtom;
    }
    
    public StatesOfMatterAtom getMovableAtomRef(){
        return m_movableAtom;
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
        	  !m_settingBothAtomTypes){
    		System.err.println(this.getClass().getName() + " - Error: Cannot set just one atom to be adjustable, ignoring request.");
    		return;
    	}
    	ensureValidMoleculeType( atomType );
    	m_bondingState = BONDING_STATE_UNBONDED;

    	// Inform any listeners of the removal of existing atoms.
        if (m_fixedAtom != null){
            notifyFixedAtomRemoved( m_fixedAtom );
            m_fixedAtom = null;
        }

        m_fixedMoleculeType = atomType;
        m_fixedAtom = AtomFactory.createAtom(atomType);

        // TODO: Setting sigma as the average of the two molecules.  Not sure
        // if this is valid, need to check with the physicists.
        if (m_movableAtom != null){
            m_ljPotentialCalculator.setSigma( m_movableAtom.getRadius() + m_fixedAtom.getRadius() );
        }
        else{
            m_ljPotentialCalculator.setSigma( m_fixedAtom.getRadius() * 2 );
        }
        
        // Set the value of epsilon.
        m_ljPotentialCalculator.setEpsilon(InteractionStrengthTable.getInteractionPotential(m_fixedMoleculeType, m_movableMoleculeType));

        notifyFixedAtomAdded( m_fixedAtom );
        notifyInteractionPotentialChanged();
        notifyFixedAtomDiameterChanged();
        m_fixedAtom.setPosition( 0, 0 );

        resetMovableAtomPos();
    }

    public void setMovableMoleculeType(AtomType atomType){
    	
    	if (((atomType == AtomType.ADJUSTABLE && m_fixedMoleculeType != AtomType.ADJUSTABLE) ||
       		 (atomType != AtomType.ADJUSTABLE && m_fixedMoleculeType == AtomType.ADJUSTABLE)) &&
           	  !m_settingBothAtomTypes){
    		System.err.println(this.getClass().getName() + " - Error: Cannot set just one atom to be adjustable, ignoring request.");
    		return;
    	}
    	ensureValidMoleculeType( atomType );
    	m_bondingState = BONDING_STATE_UNBONDED;

    	if (m_movableAtom != null){
            notifyMovableAtomRemoved( m_movableAtom );
            m_movableAtom.removeListener( m_movableAtomListener );
            m_movableAtom = null;
        }
    	
        m_movableMoleculeType = atomType;
    	m_movableAtom = AtomFactory.createAtom(atomType);
    	
        // Register to listen to motion of the movable atom so that we can
        // tell when the user is moving it.
        m_movableAtom.addListener( m_movableAtomListener );
        
        // TODO: Setting sigma as the average of the two molecules.  Not sure
        // if this is valid, need to check with the physicists.
        if (m_fixedAtom != null){
            m_ljPotentialCalculator.setSigma( m_movableAtom.getRadius() + m_fixedAtom.getRadius() );
        }
        else{
            m_ljPotentialCalculator.setSigma( m_movableAtom.getRadius() * 2 );
        }

        // Set the value of epsilon.
        m_ljPotentialCalculator.setEpsilon(InteractionStrengthTable.getInteractionPotential(m_fixedMoleculeType, m_movableMoleculeType));

        resetMovableAtomPos();

        notifyMovableAtomAdded( m_movableAtom );
        notifyInteractionPotentialChanged();
        notifyMovableAtomDiameterChanged();
        resetMovableAtomPos();
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
	
	public void setBothMoleculeTypes(AtomType atomType){
        
        m_settingBothAtomTypes = true;
        setFixedMoleculeType(atomType);
        setMovableMoleculeType(atomType);
    	m_settingBothAtomTypes = false;
    }
    
    /**
     * Set the sigma value, a.k.a. the Molecular Diameter Parameter, for the
     * adjustable molecule.  This is one of the two parameters that are used
     * for calculating the Lennard-Jones potential.  If an attempt is made to
     * set this value when the adjustable atom is not selected, it is ignored.
     * 
     * @param sigma - distance parameter
     */
    public void setAdjustableAtomSigma( double sigma ){
    	if ((m_fixedMoleculeType == AtomType.ADJUSTABLE) && 
    		(m_movableMoleculeType == AtomType.ADJUSTABLE) &&
    		(sigma != m_ljPotentialCalculator.getSigma())){

    		if (sigma > StatesOfMatterConstants.MAX_SIGMA){
    			sigma = StatesOfMatterConstants.MAX_SIGMA;
    		}
    		else if (sigma < StatesOfMatterConstants.MIN_SIGMA){
    			sigma = StatesOfMatterConstants.MIN_SIGMA;
    		}
            m_ljPotentialCalculator.setSigma( sigma );
            notifyInteractionPotentialChanged();
            ((ConfigurableStatesOfMatterAtom)m_fixedAtom).setRadius( sigma / 2 );
            notifyFixedAtomDiameterChanged();
            ((ConfigurableStatesOfMatterAtom)m_movableAtom).setRadius( sigma / 2 );
            notifyMovableAtomDiameterChanged();
    	}
    }
    
    /**
     * Get the value of the sigma parameter that is being used for the motion
     * calculations.  If the molecules are the same, it will be the diameter
     * of one atom.  If they are not, it will be a function of the
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
        
    	if (epsilon < StatesOfMatterConstants.MIN_EPSILON){
    		epsilon = StatesOfMatterConstants.MIN_EPSILON;
    	}
    	else if (epsilon > StatesOfMatterConstants.MAX_EPSILON){
    		epsilon = StatesOfMatterConstants.MAX_EPSILON;
    	}
    	
    	if ((m_fixedMoleculeType == AtomType.ADJUSTABLE) && 
       		(m_movableMoleculeType == AtomType.ADJUSTABLE)){

            m_ljPotentialCalculator.setEpsilon( epsilon );
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
        return m_ljPotentialCalculator.getEpsilon();
    }
    
    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Reset the model.
     */
    public void reset() {

        if ( m_fixedAtom == null || m_fixedAtom.getType() != DEFAULT_ATOM_TYPE ||
        	 m_movableAtom == null || m_movableAtom.getType() != DEFAULT_ATOM_TYPE){
        	setBothMoleculeTypes(DEFAULT_ATOM_TYPE);
        }
        else{
        	resetMovableAtomPos();
        }

        // Make sure we are not paused.
        m_motionPaused = false;
    }
    
    /**
     * Put the movable atom back to the location where the force is
     * minimized, and reset the velocity and acceleration to 0.
     */
    public void resetMovableAtomPos() {
    	if ( m_movableAtom != null ){
	        m_movableAtom.setPosition( m_ljPotentialCalculator.calculateMinimumForceDistance(), 0 );
	        m_movableAtom.setVx( 0 );
	        m_movableAtom.setAx( 0 );
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
    
    public void setMotionPaused(boolean paused){
        m_motionPaused = paused;
        m_movableAtom.setVx( 0 );
    }

    public boolean getMotionPaused(){
        return m_motionPaused;
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent) {

        m_shadowMovableAtom = (StatesOfMatterAtom)m_movableAtom.clone();
        
        for (int i = 0; i < CALCULATIONS_PER_TICK; i++) {

            // Execute the force calculation.
            updateForces();
            
            // Update the motion information.
            updateAtomMotion();
        }
        
        // Update the atom that is visible to the view.
        syncMovableAtomWithDummy();
        
        // Check if a bond has started to form.
        if (m_movableMoleculeType == AtomType.OXYGEN && m_fixedMoleculeType == AtomType.OXYGEN){
        	switch ( m_bondingState ){
        	
        	case BONDING_STATE_UNBONDED:
        		if ( ( m_movableAtom.getVx() > THRESHOLD_VELOCITY ) &&
        			 ( m_movableAtom.getPositionReference().distance( m_fixedAtom.getPositionReference() ) < m_fixedAtom.getRadius() * 2.5 ) ){
        			// The atoms are close together and the movable one is
        			// starting to move away, which is the point at which we
        			// consider the bond to start forming.
        			m_bondingState = BONDING_STATE_BONDING;
        		}
        		break;
        		
        	case BONDING_STATE_BONDING:
        		if ( m_attractiveForce > m_repulsiveForce ){
        			// A bond is forming and the force just exceeded the
        			// repulsive force, meaning that the atom is starting
        			// to pass the bottom of the well.  Reduce its velocity
        			// so that it remains stuck in the bottom of the well.
        			m_movableAtom.setVx( BONDED_VELOCITY );
        			m_bondingState = BONDING_STATE_BONDED;
        		}
        		break;
        		
        	case BONDING_STATE_BONDED:
        		if ( Math.abs( m_movableAtom.getVx() ) > BONDED_VELOCITY ){
        			// The atom must have gotten accelerated by the potential.
        			// Slow it back down.
        			m_movableAtom.setVx( m_movableAtom.getVx() > 0 ? BONDED_VELOCITY : -BONDED_VELOCITY );
        		}
        		break;
        		
        	default:
        		System.err.println(this.getClass().getName() + " - Error: Unrecognized bonding state.");
        		assert false;
        		m_bondingState = BONDING_STATE_UNBONDED;
        		break;
        	}
        }
    }

    /**
     * 
     */
    private void syncMovableAtomWithDummy() {
        m_movableAtom.setAx( m_shadowMovableAtom.getAx() );
        m_movableAtom.setVx( m_shadowMovableAtom.getVx() );
        m_movableAtom.setPosition( m_shadowMovableAtom.getX(), m_shadowMovableAtom.getY() );
    }
    
    private void updateForces(){
        
        double distance = m_shadowMovableAtom.getPositionReference().distance( m_fixedAtom.getPositionReference() );
        
        if (distance < (m_fixedAtom.getRadius() + m_movableAtom.getRadius()) / 2){
            // The atoms are too close together, and calculating the force
            // will cause unusable levels of speed later, so we limit it.
            distance = (m_fixedAtom.getRadius() + m_movableAtom.getRadius()) / 2;
        }
        
        // Calculate the force.  The result should be in newtons.
        m_attractiveForce = m_ljPotentialCalculator.calculateAttractiveLjForce( distance );
        m_repulsiveForce = m_ljPotentialCalculator.calculateRepulsiveLjForce( distance );
    }
    
    /**
     * Update the position, velocity, and acceleration of the dummy movable atom.
     */
    private void updateAtomMotion(){
        
        double mass = m_shadowMovableAtom.getMass() * 1.6605402E-27;  // Convert mass to kilograms.
        double acceleration = (m_repulsiveForce - m_attractiveForce) / mass;
        
        // Update the acceleration for the movable atom.  We do this
        // regardless of whether movement is paused so that the force vectors
        // can be shown appropriately if the user moves the atoms.
        m_shadowMovableAtom.setAx( acceleration );
        
        if (!m_motionPaused){
            // Update the position and velocity of the atom.
            m_shadowMovableAtom.setVx( m_shadowMovableAtom.getVx() + (acceleration * m_timeStep) );
            double xPos = m_shadowMovableAtom.getPositionReference().getX() + (m_shadowMovableAtom.getVx() * m_timeStep);
            m_shadowMovableAtom.setPosition( xPos, 0 );
        }
    }
    
    private void notifyFixedAtomAdded(StatesOfMatterAtom atom){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).fixedAtomAdded( atom );
        }        
    }
    
    private void notifyMovableAtomAdded(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).movableAtomAdded( particle );
        }        
    }
    
    private void notifyFixedAtomRemoved(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).fixedAtomRemoved( particle );
        }        
    }
    
    private void notifyMovableAtomRemoved(StatesOfMatterAtom atom){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).movableAtomRemoved( atom );
        }        
    }
    
    private void notifyInteractionPotentialChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).interactionPotentialChanged();
        }        
    }
    
    private void notifyFixedAtomDiameterChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).fixedAtomDiameterChanged();
        }        
    }
    
    private void notifyMovableAtomDiameterChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).movableAtomDiameterChanged();
        }        
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------
    
    public static interface Listener {
        
        /**
         * Inform listeners that the model has been reset.
         */
        public void fixedAtomAdded(StatesOfMatterAtom atom);
        public void movableAtomAdded(StatesOfMatterAtom atom);
        public void fixedAtomRemoved(StatesOfMatterAtom atom);
        public void movableAtomRemoved(StatesOfMatterAtom atom);
        public void interactionPotentialChanged();
        public void fixedAtomDiameterChanged();
        public void movableAtomDiameterChanged();
    }
    
    public static class Adapter implements Listener {
        public void fixedAtomAdded(StatesOfMatterAtom atom){}
        public void movableAtomAdded(StatesOfMatterAtom atom){}
        public void fixedAtomRemoved(StatesOfMatterAtom atom){}
        public void movableAtomRemoved(StatesOfMatterAtom atom){}
        public void interactionPotentialChanged(){};
        public void fixedAtomDiameterChanged(){};
        public void movableAtomDiameterChanged(){};
    }
}
