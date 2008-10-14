/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample;

import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.mvcexample.control.BConnectionsManager;
import edu.colorado.phet.mvcexample.control.BControlPanel;
import edu.colorado.phet.mvcexample.control.MVCControlPanel;
import edu.colorado.phet.mvcexample.model.*;
import edu.colorado.phet.mvcexample.view.BNode;
import edu.colorado.phet.mvcexample.view.MVCCanvas;

/**
 * MVCModule is the module for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MVCModule extends Module {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    private static final double CLOCK_DT = 1;
    private static final boolean CLOCK_RUNNING = true;

    private static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );
    
    private static final String TITLE = MVCExampleApplication.RESOURCE_LOADER.getLocalizedString( "MVCModule.title" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MVCModel _model;
    private MVCCanvas _canvas;
    private MVCControlPanel _controlPanel;
    private ClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MVCModule( Frame parentFrame ) {
        super( TITLE, new MVCClock( CLOCK_FRAME_RATE, CLOCK_DT, CLOCK_RUNNING ) );
        setLogoPanel( null );
        
        // Model
        MVCClock clock = (MVCClock) getClock();
        _model = new MVCModel( clock );

        // Canvas
        _canvas = new MVCCanvas( VIEW_SIZE, _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new MVCControlPanel( this, _model, parentFrame );
        setControlPanel( _controlPanel );

        // Connect MVC components for BModelElement
        BModelElement bModelElement = _model.getBModelElement();
        BNode bNode = _canvas.getBNode();
        BControlPanel bControlPanel = _controlPanel.getBControlPanel();
        BConnectionsManager bControlManager = new BConnectionsManager( bModelElement, bNode, bControlPanel );
        bControlManager.connect();

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

        // Clock
        MVCClock clock = _model.getClock();
        clock.setDt( CLOCK_DT );
        setClockRunningWhenActive( CLOCK_RUNNING );

        // A
        AModelElement aModelElement = _model.getAModelElement();
        aModelElement.setPosition( MVCModel.A_POSITION );
        aModelElement.setOrientation( MVCModel.A_ORIENTATION );
        
        // B
        BModelElement bModelElement = _model.getBModelElement();
        bModelElement.setPosition( MVCModel.B_POSITION );
        bModelElement.setOrientation( MVCModel.B_ORIENTATION );
        
        // C
        CModelElement cModelElement = _model.getCModelElement();
        cModelElement.setPosition( MVCModel.C_POSITION );
        cModelElement.setOrientation( MVCModel.C_ORIENTATION );
    }
}
