/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.SolidLiquidGasDefaults;
import edu.colorado.phet.statesofmatter.model.AbstractMultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel1;

/**
 * This class is where the model and view classes for the "Solid, Liquid, and
 * Gas" tab of this simulation are created and contained. 
 *
 * @author John Blanco
 */
public class SolidLiquidGasModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private AbstractMultipleParticleModel m_model;
    private SolidLiquidGasCanvas  m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public SolidLiquidGasModule( Frame parentFrame ) {
        
        super(StatesOfMatterStrings.TITLE_SOLID_LIQUID_GAS_MODULE, 
                new ConstantDtClock(SolidLiquidGasDefaults.CLOCK_FRAME_DELAY, SolidLiquidGasDefaults.CLOCK_DT));

        // Model
        m_model = new MultipleParticleModel1( getClock() );

        // Canvas
        m_canvas = new SolidLiquidGasCanvas( m_model );
        setSimulationPanel( m_canvas );
        
        // Control panel
        setControlPanel( new SolidLiquidGasControlPanel( this, parentFrame ) );
        
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
    public AbstractMultipleParticleModel getMultiParticleModel(){
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
