/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.control.BottomPanel;
import edu.colorado.phet.glaciers.control.GlaciersClockControlPanel;
import edu.colorado.phet.glaciers.defaults.ExampleDefaults;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.module.GlaciersAbstractModule;
import edu.colorado.phet.glaciers.persistence.ExampleConfig;

/**
 * ExampleModule is the "Example" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModule extends GlaciersAbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ExampleModel _model;
    private ExampleCanvas _canvas;
    private BottomPanel _bottomPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExampleModule( Frame parentFrame ) {
        super( GlaciersResources.getString( "title.exampleModule" ), ExampleDefaults.CLOCK );

        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        _model = new ExampleModel( clock );

        // Canvas
        _canvas = new ExampleCanvas( _model );
        setSimulationPanel( _canvas );

        // Bottom panel goes when clock controls normally go
        _bottomPanel = new BottomPanel( clock );
        setClockControlPanel( _bottomPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    public ExampleModel getExampleModel() {
        return _model;
    }

    public ExampleCanvas getExampleCanvas() {
        return _canvas;
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setDt( ExampleDefaults.CLOCK_DT_RANGE.getDefault() );
            setClockRunningWhenActive( ExampleDefaults.CLOCK_RUNNING );
            
            // ExampleModelElement
            ExampleModelElement exampleModelElement = _model.getExampleModelElement();
            exampleModelElement.setSize( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_SIZE );
            exampleModelElement.setPosition( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION );
            exampleModelElement.setOrientation( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ExampleConfig save() {

        ExampleConfig config = new ExampleConfig();
        ExampleModel model = getExampleModel();

        // Module
        config.setActive( isActive() );

        // Model
        {
            // Clock
            GlaciersClock clock = model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );

            // ExampleModelElement
            ExampleModelElement exampleModelElement = model.getExampleModelElement();
            config.setExampleModelElementWidth( exampleModelElement.getSizeReference().getWidth() );
            config.setExampleModelElementHeight( exampleModelElement.getSizeReference().getHeight() );
            config.setExampleModelElementPosition( exampleModelElement.getPositionReference() );
            config.setExampleModelElementOrientation( exampleModelElement.getOrientation() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
        
        return config;
    }

    public void load( ExampleConfig config ) {

        ExampleModel model = getExampleModel();

        // Module
        if ( config.isActive() ) {
            GlaciersApplication.instance().setActiveModule( this );
        }

        // Model
        {
            // Clock
            GlaciersClock clock = model.getClock();
            clock.setDt( config.getClockDt() );
            setClockRunningWhenActive( config.isClockRunning() );

            // ExampleModelElement
            ExampleModelElement exampleModelElement = model.getExampleModelElement();
            exampleModelElement.setSize( new Dimension( (int) config.getExampleModelElementWidth(), (int) config.getExampleModelElementHeight() ) );
            exampleModelElement.setPosition( config.getExampleModelElementPosition() );
            exampleModelElement.setOrientation( config.getExampleModelElementOrientation() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
}
