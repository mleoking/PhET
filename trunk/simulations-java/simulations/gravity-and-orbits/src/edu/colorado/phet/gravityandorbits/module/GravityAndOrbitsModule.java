/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsStrings;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Module template.
 */
public class GravityAndOrbitsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GravityAndOrbitsModel model;
    private GravityAndOrbitsCanvas canvas;
    private PiccoloClockControlPanel clockControlPanel;
    private Property<Boolean> forcesProperty = new Property<Boolean>( false );
    private Property<Boolean> tracesProperty = new Property<Boolean>( false );
    private Property<Boolean> velocityProperty = new Property<Boolean>( false );
    private Property<Boolean> showMassesProperty = new Property<Boolean>( false );
    private Property<Boolean> moonProperty = new Property<Boolean>( false );
    private Property<Boolean> toScaleProperty = new Property<Boolean>( false );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GravityAndOrbitsModule( Frame parentFrame ) {
        super( GravityAndOrbitsStrings.TITLE_EXAMPLE_MODULE, new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.CLOCK_DT ) );

        // Model
        GravityAndOrbitsClock clock = (GravityAndOrbitsClock) getClock();
        model = new GravityAndOrbitsModel( clock );

        // Canvas
        canvas = new GravityAndOrbitsCanvas( model, this );
        setSimulationPanel( canvas );

        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        clockControlPanel.setRewindButtonVisible( true );
        clockControlPanel.setTimeDisplayVisible( true );
        clockControlPanel.setUnits( GravityAndOrbitsStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( GravityAndOrbitsDefaults.CLOCK_TIME_COLUMNS );

        //Show the clock controls in the play area, not in a separate panel
        canvas.addChild( new PSwing( clockControlPanel ) {{
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );
        setClockControlPanel( null );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // reset the clock
        GravityAndOrbitsClock clock = model.getClock();
        clock.resetSimulationTime();
    }

    public Property<Boolean> getForcesProperty() {
        return forcesProperty;
    }

    public Property<Boolean> getTracesProperty() {
        return tracesProperty;
    }

    public Property<Boolean> getVelocityProperty() {
        return velocityProperty;
    }

    public Property<Boolean> getShowMassesProperty() {
        return showMassesProperty;
    }

    public Property<Boolean> getMoonProperty() {
        return moonProperty;
    }

    public Property<Boolean> getToScaleProperty() {
        return toScaleProperty;
    }

    public void resetAll() {
        forcesProperty.reset();
        tracesProperty.reset();
        velocityProperty.reset();
        showMassesProperty.reset();
        moonProperty.reset();
        toScaleProperty.reset();
        model.resetAll();
    }
}
