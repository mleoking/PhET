/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.exp2;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.control.StatesOfMatterClockControlPanel;
import edu.colorado.phet.statesofmatter.defaults.SolidLiquidGasDefaults;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class is where the model and view classes for the "Solid, Liquid, and
 * Gas" tab of this simulation are created and contained. 
 *
 * @author John Blanco
 */
public class Exp2SolidLiquidGasModule extends Module {
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private MultipleParticleModel m_model;
    private Exp2SolidLiquidGasCanvas  m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public Exp2SolidLiquidGasModule( Frame parentFrame ) {
        
        super("Simple Box", new ConstantDtClock(10, StatesOfMatterConstants.DELTA_T));

        // Model
        m_model = new MultipleParticleModel( getClock() );

        // Canvas
        m_canvas = new Exp2SolidLiquidGasCanvas( m_model );
        setSimulationPanel( m_canvas );
        
        // Control panel
        setControlPanel( new Exp2SolidLiquidGasControlPanel( this, parentFrame ) );
        
        // Clock controls
        setClockControlPanel( new StatesOfMatterClockControlPanel( getClock() ) );
        
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
    public MultipleParticleModel getMultiParticleModel(){
        return m_model;
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
