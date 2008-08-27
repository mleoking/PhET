/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.interactionpotential;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.SolidLiquidGasDefaults;
import edu.colorado.phet.statesofmatter.model.DualParticleModel;

/**
 * This class is where the model and view classes for the "Interaction
 * Potential" tab of this simulation are created and contained. 
 *
 * @author John Blanco
 */
public class InteractionPotentialModule extends Module {
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private DualParticleModel m_model;
    private InteractionPotentialCanvas  m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public InteractionPotentialModule( Frame parentFrame ) {
        
        super(StatesOfMatterStrings.TITLE_INTERACTION_POTENTIAL_MODULE, 
                new ConstantDtClock(10, StatesOfMatterConstants.DELTA_T));

        // Model
        m_model = new DualParticleModel( getClock() );

        // Canvas
        m_canvas = new InteractionPotentialCanvas( m_model );
        setSimulationPanel( m_canvas );
        
        // Control panel
        setControlPanel( new InteractionPotentialControlPanel( this, parentFrame ) );
        
        // Clock controls
        setClockControlPanel( new ClockControlPanel( getClock() ) );
        
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
    
    public DualParticleModel getDualParticleModel(){
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
        setClockRunningWhenActive( SolidLiquidGasDefaults.CLOCK_RUNNING );
    }
}
