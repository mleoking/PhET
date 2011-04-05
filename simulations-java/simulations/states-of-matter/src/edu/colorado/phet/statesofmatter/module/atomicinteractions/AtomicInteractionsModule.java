// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.module.atomicinteractions;

import java.awt.Frame;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.clock.SimSpeedControl;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.AtomicInteractionDefaults;
import edu.colorado.phet.statesofmatter.model.DualAtomModel;

/**
 * This class is where the model and view classes for the "Interaction
 * Potential" tab of this simulation are created and contained.
 *
 * @author John Blanco
 */
public class AtomicInteractionsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private DualAtomModel m_model;
    private AtomicInteractionsCanvas  m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public AtomicInteractionsModule( Frame parentFrame, boolean enableHeterogeneousAtoms ) {

        super(StatesOfMatterStrings.TITLE_INTERACTION_POTENTIAL_MODULE,
                new ConstantDtClock(AtomicInteractionDefaults.CLOCK_FRAME_DELAY,
                AtomicInteractionDefaults.CLOCK_DT));

        // Remove the PhET logo.  This is done because the control panel was
        // getting pushed off the bottom in some cases, and removing the
        // logo creates more room.
        setLogoPanel(null);

        // Model
        m_model = new DualAtomModel( (ConstantDtClock) getClock() );

        // Canvas
        m_canvas = new AtomicInteractionsCanvas( m_model );
        setSimulationPanel( m_canvas );

        // Control panel
        setControlPanel( new AtomicInteractionsControlPanel( this, parentFrame, enableHeterogeneousAtoms ) );

        // Add a slider for controlling speed to the clock controls.
    	PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final SimSpeedControl timeSpeedSlider = new SimSpeedControl(AtomicInteractionDefaults.CLOCK_DT / 5,
    			AtomicInteractionDefaults.CLOCK_DT, (ConstantDtClock)getClock(),
    			StatesOfMatterStrings.CLOCK_SPEED_CONTROL_CAPTION);
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ((ConstantDtClock)getClock()).setDt( timeSpeedSlider.getValue() );
            }
        } );
    	clockControlPanel.addBetweenTimeDisplayAndButtons(timeSpeedSlider);
        setClockControlPanel( clockControlPanel );

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

    public AtomicInteractionsCanvas getCanvas(){
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
        setClockRunningWhenActive( AtomicInteractionDefaults.CLOCK_RUNNING );
    }
}
