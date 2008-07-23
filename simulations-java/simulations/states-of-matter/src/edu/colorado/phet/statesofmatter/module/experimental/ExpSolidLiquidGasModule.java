/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.experimental;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.defaults.SolidLiquidGasDefaults;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class is where the model and view classes for the "Solid, Liquid, and
 * Gas" tab of this simulation are created and contained. 
 *
 * @author John Blanco
 */
public class ExpSolidLiquidGasModule extends Module {
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private MultipleParticleModel m_model;
    private ExpSolidLiquidGasCanvas  m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ExpSolidLiquidGasModule( Frame parentFrame ) {
        
        super("3D Cylinder", new ConstantDtClock(10, StatesOfMatterConstants.DELTA_T));

        // Model
        m_model = new MultipleParticleModel( getClock() );

        // Canvas
        m_canvas = new ExpSolidLiquidGasCanvas( m_model );
        setSimulationPanel( m_canvas );
        
        // Control panel
        setControlPanel( new ExpSolidLiquidGasControlPanel( this, parentFrame ) );
        
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
