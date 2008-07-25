/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel.Listener;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;
import edu.colorado.phet.statesofmatter.model.engine.EngineFacade;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyAdjuster;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyCapper;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
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

    private static final double TIME_STEP = Math.pow( 0.5, 6.0 );
    private static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    
    // Supported molecules.
    public static final int NEON = 1;
    public static final int ARGON = 2;
    public static final int MONATOMIC_OXYGEN = 3;
    public static final int DEFAULT_MOLECULE = NEON;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    IClock m_clock;
    private ArrayList m_listeners = new ArrayList();
    private StatesOfMatterAtom m_leftParticle;
    private StatesOfMatterAtom m_rightParticle;
    private double m_leftParticleHorizForce;
    private double m_rightParticleHorizForce;
    private double m_epsilon;  // Epsilon represents interaction strength. 
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public DualParticleModel(IClock clock) {
        
        m_clock = clock;
        
        // Register as a clock listener.
        clock.addClockListener(new ClockAdapter(){
            
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset( ClockEvent clockEvent ) {
                reset();
            }
        });
        
        // Set the default particle type.
        setMolecule( NEON );
    }

    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    
    public StatesOfMatterAtom getLeftParticleRef(){
        return m_leftParticle;
    }
    
    public StatesOfMatterAtom getRightParticleRef(){
        return m_rightParticle;
    }
    
    public double getLeftParticleHorizForce(){
        return m_leftParticleHorizForce;
    }

    public double getRightParticleHorizForce(){
        return m_rightParticleHorizForce;
    }
    
    public void setMolecule(int atomID){
        
        // Verify that this is a supported value.
        if ((atomID != NEON) &&
            (atomID != ARGON) &&
            (atomID != MONATOMIC_OXYGEN)){
            
            System.err.println("Error: Unsupported molecule type.");
            assert false;
            atomID = NEON;
        }
        
        // Inform any listeners of the removal of existing particles.
        if (m_leftParticle != null){
            notifyParticleRemoved( m_leftParticle );
            m_leftParticle = null;
        }
        if (m_rightParticle != null){
            notifyParticleRemoved( m_rightParticle );
            m_rightParticle = null;
        }
        
        // Set the new atoms based on the requested type..
        switch (atomID){
        case NEON:
            m_leftParticle = new NeonAtom(0, 0);
            m_rightParticle = new NeonAtom(0, 0);
            break;
        case ARGON:
            m_leftParticle = new ArgonAtom(0, 0);
            m_rightParticle = new ArgonAtom(0, 0);
            break;
        case MONATOMIC_OXYGEN:
            m_leftParticle = new OxygenAtom(0, 0);
            m_rightParticle = new OxygenAtom(0, 0);
            break;
        }
        
        // Let listeners know about the new molecules.
        notifyParticleAdded( m_leftParticle );
        notifyParticleAdded( m_rightParticle );
    }
    
    public void setInteractionStrength( double epsilon ){
        m_epsilon = epsilon;
    }
    
    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Reset the model.
     */
    public void reset() {
        
        // Initialize the system parameters.
        setMolecule( DEFAULT_MOLECULE );
        
        // Let any listeners know that the model has been reset.
        notifyResetOccurred();
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

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent) {
        
        // Execute the Verlet algorithm.
        // TODO: JPB TBD.
    }
    
    private void notifyResetOccurred(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).resetOccurred();
        }        
    }

    private void notifyParticleAdded(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).particleAdded( particle );
        }        
    }
    
    private void notifyParticleRemoved(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).particleRemoved( particle );
        }        
    }
    
    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of particles.
     */
    private void verletMonotomic(){
        
    }

    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------
    
    public static interface Listener {
        
        /**
         * Inform listeners that the model has been reset.
         */
        public void resetOccurred();
        public void particleAdded(StatesOfMatterAtom particle);
        public void particleRemoved(StatesOfMatterAtom particle);
    }
    
    public static class Adapter implements Listener {
        public void resetOccurred(){}
        public void particleAdded(StatesOfMatterAtom particle){}
        public void particleRemoved(StatesOfMatterAtom particle){}
    }
}
