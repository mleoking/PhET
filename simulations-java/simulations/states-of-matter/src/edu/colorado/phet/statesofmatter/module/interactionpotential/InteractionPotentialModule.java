/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.interactionpotential;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.InteractionPotentialDefaults;
import edu.colorado.phet.statesofmatter.model.DualAtomModel;

/**
 * This class is where the model and view classes for the "Interaction
 * Potential" tab of this simulation are created and contained. 
 *
 * @author John Blanco
 */
public class InteractionPotentialModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private DualAtomModel m_model;
    private InteractionPotentialCanvas  m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public InteractionPotentialModule( Frame parentFrame, boolean enableHeterogeneousAtoms ) {
        
        super(StatesOfMatterStrings.TITLE_INTERACTION_POTENTIAL_MODULE, 
                new ConstantDtClock(InteractionPotentialDefaults.CLOCK_FRAME_DELAY, 
                InteractionPotentialDefaults.CLOCK_DT));

        // Model
        m_model = new DualAtomModel( getClock() );

        // Canvas
        m_canvas = new InteractionPotentialCanvas( m_model );
        setSimulationPanel( m_canvas );
        
        // Control panel
        setControlPanel( new InteractionPotentialControlPanel( this, parentFrame, enableHeterogeneousAtoms ) );
        
        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }
    
    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    
    public DualAtomModel getDualParticleModel(){
        return m_model;
    }
    
    public InteractionPotentialCanvas getCanvas(){
        return m_canvas;
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Reset the clock, which ultimately resets the model too.
        getClock().resetSimulationTime();
        setClockRunningWhenActive( InteractionPotentialDefaults.CLOCK_RUNNING );
    }
}
