// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.SolidLiquidGasDefaults;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

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

    private final MultipleParticleModel m_model;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public SolidLiquidGasModule() {

        super( StatesOfMatterStrings.TITLE_SOLID_LIQUID_GAS_MODULE,
               new ConstantDtClock( SolidLiquidGasDefaults.CLOCK_FRAME_DELAY, SolidLiquidGasDefaults.CLOCK_DT ) );

        // Model
        m_model = new MultipleParticleModel( (ConstantDtClock) getClock() );

        // Canvas
        setSimulationPanel( new SolidLiquidGasCanvas( m_model ) );

        // Control panel
        setControlPanel( new SolidLiquidGasControlPanel( this ) );

        // Turn off the clock control panel - a floating node is used for clock control.
        setClockControlPanel( null );

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
    public MultipleParticleModel getMultiParticleModel() {
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
