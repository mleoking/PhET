/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.control.GlaciersControlPanel;
import edu.colorado.phet.glaciers.defaults.AdvancedDefaults;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.persistence.AdvancedConfig;

/**
 * AdvancedModule is the "Advanced" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AdvancedModel _model;
    private AdvancedCanvas _canvas;
    private GlaciersControlPanel _bottomPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AdvancedModule( Frame parentFrame ) {
        super( GlaciersResources.getString( "title.advanced" ), AdvancedDefaults.CLOCK );
        setLogoPanel( null );

        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        _model = new AdvancedModel( clock );

        // Canvas
        _canvas = new AdvancedCanvas( _model );
        setSimulationPanel( _canvas );

        // Bottom panel goes when clock controls normally go
        _bottomPanel = new GlaciersControlPanel( clock );
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

    public AdvancedModel getAdvancedModel() {
        return _model;
    }

    public AdvancedCanvas getAdvancedCanvas() {
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
            clock.setDt( AdvancedDefaults.CLOCK_DT_RANGE.getDefault() );
            setClockRunningWhenActive( AdvancedDefaults.CLOCK_RUNNING );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public AdvancedConfig save() {

        AdvancedConfig config = new AdvancedConfig();
        AdvancedModel model = getAdvancedModel();

        // Module
        config.setActive( isActive() );

        // Model
        {
            // Clock
            GlaciersClock clock = model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
        
        return config;
    }

    public void load( AdvancedConfig config ) {

        AdvancedModel model = getAdvancedModel();

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
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
}
