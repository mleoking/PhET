/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.defaults.ComparingDefaults;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.ExampleModelElement;
import edu.colorado.phet.acidbasesolutions.persistence.ExampleConfig;
import edu.colorado.phet.acidbasesolutions.view.ExampleNode;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;

/**
 * ComparingModule is the "Comparing Solutions" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ComparingModel _model;
    private ComparingCanvas _canvas;
    private ComparingControlPanel _controlPanel;
    private PiccoloClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ComparingModule( Frame parentFrame ) {
        super( ABSStrings.TITLE_COMPARING_MODULE, new ABSClock( ComparingDefaults.CLOCK_FRAME_RATE, ComparingDefaults.CLOCK_DT ) );

        // Model
        ABSClock clock = (ABSClock) getClock();
        _model = new ComparingModel( clock );

        // Canvas
        _canvas = new ComparingCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new ComparingControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new PiccoloClockControlPanel( getClock() );
        _clockControlPanel.setRestartButtonVisible( true );
        _clockControlPanel.setTimeDisplayVisible( true );
        _clockControlPanel.setUnits( ABSStrings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( ComparingDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );

        // Controller
        ComparingController controller = new ComparingController( _model, _canvas, _controlPanel );
        
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

        // Clock
        ABSClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( ComparingDefaults.CLOCK_DT );
        setClockRunningWhenActive( ComparingDefaults.CLOCK_RUNNING );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = _model.getExampleModelElement();
        exampleModelElement.setPosition( ComparingDefaults.EXAMPLE_MODEL_ELEMENT_POSITION );
        exampleModelElement.setOrientation( ComparingDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        
        // ExampleNode
        ExampleNode exampleNode = _canvas.getExampleNode();
        exampleNode.setSize( exampleModelElement.getWidth(), exampleModelElement.getHeight() );
        exampleNode.setPosition( exampleModelElement.getPosition() );
        exampleNode.setOrientation( exampleModelElement.getOrientation() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ExampleConfig save() {

        ExampleConfig config = new ExampleConfig();

        // Module
        config.setActive( isActive() );

        // Clock
        ABSClock clock = _model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = _model.getExampleModelElement();
        config.setExampleModelElementPosition( exampleModelElement.getPositionReference() );
        config.setExampleModelElementOrientation( exampleModelElement.getOrientation() );

        // Control panel settings that are specific to the view
        //XXX
        
        return config;
    }

    public void load( ExampleConfig config ) {

        // Module
        if ( config.isActive() ) {
            AcidBaseSolutionsApplication.instance().setActiveModule( this );
        }

        // Clock
        ABSClock clock = _model.getClock();
        clock.setDt( config.getClockDt() );
        setClockRunningWhenActive( config.isClockRunning() );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = _model.getExampleModelElement();
        exampleModelElement.setPosition( config.getExampleModelElementPosition() );
        exampleModelElement.setOrientation( config.getExampleModelElementOrientation() );

        // Control panel settings that are specific to the view
        //XXX
    }
}
