/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import java.awt.Frame;

import javax.swing.JPanel;

import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.control.MiscControlPanel.MiscControlPanelAdapter;
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
    private static final double MVT_X_SCALE = 0.062; // scale x by this amount when going from model to view
    private static final double MVT_Y_SCALE = 0.1; // scale y by this amount when going from model to view
    private static final double MVT_X_OFFSET = 0; // translate x by this amount when going from model to view
    private static final double MVT_Y_OFFSET = 0; // translate y by this amount when going from model to view
    private static final boolean MVT_FLIP_SIGN_X = false;
    private static final boolean MVT_FLIP_SIGN_Y = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BasicModel _model;
    private BasicControlPanel _controlPanel;
    private BasicController _controller;

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
        ModelViewTransform mvt = new ModelViewTransform( MVT_X_SCALE, MVT_Y_SCALE, MVT_X_OFFSET, MVT_Y_OFFSET, MVT_FLIP_SIGN_X, MVT_FLIP_SIGN_Y );
        JPanel playArea = new PlayArea( _model, mvt );
        setSimulationPanel( playArea );

        // Bottom panel goes when clock controls normally go
        _controlPanel = new BasicControlPanel( clock );
        setClockControlPanel( _controlPanel );
        _controlPanel.getMiscControlPanel().addMiscControlPanelListener( new MiscControlPanelAdapter() {
            public void resetAllButtonPressed() {
                resetAll();
            }
            public void setHelpEnabled( boolean enabled ) {
                System.out.println( "BasicModule.setHelpEnabled " + enabled );
                BasicModule.this.setHelpEnabled( enabled );
            }
        });
        
        // Controller
        _controller = new BasicController( _model, _controlPanel );

        // Help
        if ( hasHelp() ) {
            setHelpPanel( null ); // get rid of the standard Help panel, we're using our own help button
            
            //XXX add help items
            HelpPane helpPane = getDefaultHelpPane();
            HelpBalloon equilibriumButtonHelp = new HelpBalloon( helpPane, GlaciersStrings.HELP_EQUILIBRIUM_BUTTON, HelpBalloon.BOTTOM_CENTER, 80 );
            helpPane.add( equilibriumButtonHelp );
            equilibriumButtonHelp.pointAt( _controlPanel.getMiscControlPanel().getEquilibriumButton() );
        }
        
        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * 
     */
    public boolean hasHelp() {
        return true;
    }
    
    /**
     * 
     */
    public void setHelpEnabled( boolean enabled ) {
        super.setHelpEnabled( enabled );
        _controlPanel.getMiscControlPanel().setHelpEnabled( enabled );
        GlaciersApplication.instance().getPhetFrame().getHelpMenu().setHelpSelected( enabled );
    }
    
    /**
     * Resets the module.
     */
    public void resetAll() {
        
        System.out.println( "BasicModule.resetAll" );//XXX

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setDt( BasicDefaults.CLOCK_DT_RANGE.getDefault() );
            clock.resetSimulationTime();
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
