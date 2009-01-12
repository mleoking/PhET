/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.defaults.SolutionsDefaults;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.ExampleModelElement;
import edu.colorado.phet.acidbasesolutions.persistence.ExampleConfig;
import edu.colorado.phet.acidbasesolutions.view.ExampleNode;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;

/**
 * SolutionsModule is the "Solutions" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private SolutionsModel _model;
    private SolutionsCanvas _canvas;
    private SolutionsControlPanel _controlPanel;
    private PiccoloClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public SolutionsModule( Frame parentFrame ) {
        super( ABSStrings.TITLE_SOLUTIONS_MODULE, new ABSClock( SolutionsDefaults.CLOCK_FRAME_RATE, SolutionsDefaults.CLOCK_DT ) );

        // Model
        ABSClock clock = (ABSClock) getClock();
        _model = new SolutionsModel( clock );

        // Canvas
        _canvas = new SolutionsCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new SolutionsControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new PiccoloClockControlPanel( getClock() );
        _clockControlPanel.setRestartButtonVisible( true );
        _clockControlPanel.setTimeDisplayVisible( true );
        _clockControlPanel.setUnits( ABSStrings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( SolutionsDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );

        // Controller
        SolutionsController controller = new SolutionsController( _model, _canvas, _controlPanel );
        
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
        clock.setDt( SolutionsDefaults.CLOCK_DT );
        setClockRunningWhenActive( SolutionsDefaults.CLOCK_RUNNING );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = _model.getExampleModelElement();
        exampleModelElement.setPosition( SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_POSITION );
        exampleModelElement.setOrientation( SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        
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
