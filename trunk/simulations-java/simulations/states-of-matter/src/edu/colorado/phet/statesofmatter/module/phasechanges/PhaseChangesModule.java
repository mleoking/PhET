// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.module.phasechanges;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.PhaseChangesDefaults;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;


public class PhaseChangesModule extends PiccoloModule {
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private MultipleParticleModel m_model;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public PhaseChangesModule( boolean advanced ) {

        super( StatesOfMatterStrings.TITLE_PHASE_CHANGES_MODULE,
               new ConstantDtClock( PhaseChangesDefaults.CLOCK_FRAME_DELAY, PhaseChangesDefaults.CLOCK_DT ) );

        // Model
        m_model = new MultipleParticleModel( (ConstantDtClock) getClock() );

        // Canvas
        setSimulationPanel( new PhaseChangesCanvas( m_model ) );

        // Control panel
        setControlPanel( new PhaseChangesControlPanel( this, advanced ) );

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
        setClockRunningWhenActive( PhaseChangesDefaults.CLOCK_RUNNING );
    }
}
