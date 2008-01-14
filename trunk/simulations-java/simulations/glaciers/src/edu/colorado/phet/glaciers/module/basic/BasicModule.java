/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import java.awt.Frame;

import javax.swing.JPanel;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.defaults.BasicDefaults;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.persistence.BasicConfig;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.PlayArea;

/**
 * BasicModule is the "Basic" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // ModelViewTransform (MVT) parameters
    private static final double MVT_X_SCALE = 1; // scale x by this amount when going from model to view
    private static final double MVT_Y_SCALE = 1; // scale y by this amount when going from model to view
    private static final double MVT_X_OFFSET = 0; // translate x by this amount when going from model to view
    private static final double MVT_Y_OFFSET = 0; // translate y by this amount when going from model to view
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BasicModel _model;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BasicModule( Frame parentFrame ) {
        super( GlaciersStrings.TITLE_BASIC, BasicDefaults.CLOCK );
        setLogoPanel( null );

        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        Valley valley = new Valley();
        Glacier glacier = new Glacier();
        Climate climate = new Climate();
        _model = new BasicModel( clock, valley, glacier, climate );

        // Play Area
        ModelViewTransform mvt = new ModelViewTransform( MVT_X_SCALE, MVT_Y_SCALE, MVT_X_OFFSET, MVT_Y_OFFSET );
        JPanel playArea = new PlayArea( _model, mvt );
        setSimulationPanel( playArea );

        // Bottom panel goes when clock controls normally go
        JPanel controlPanel = new BasicControlPanel( clock );
        setClockControlPanel( controlPanel );

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

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setDt( BasicDefaults.CLOCK_DT_RANGE.getDefault() );
            setClockRunningWhenActive( BasicDefaults.CLOCK_RUNNING );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public BasicConfig save() {

        BasicConfig config = new BasicConfig();

        // Module
        config.setActive( isActive() );

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
        
        return config;
    }

    public void load( BasicConfig config ) {

        // Module
        if ( config.isActive() ) {
            GlaciersApplication.instance().setActiveModule( this );
        }

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setDt( config.getClockDt() );
            setClockRunningWhenActive( config.isClockRunning() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
}
