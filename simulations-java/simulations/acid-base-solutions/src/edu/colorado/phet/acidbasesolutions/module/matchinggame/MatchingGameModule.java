/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractModule;
import edu.colorado.phet.acidbasesolutions.persistence.MatchingGameConfig;

/**
 * MatchingGameModule is the "Matching Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameModule extends ABSAbstractModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = ABSStrings.TITLE_MATCHING_GAME_MODULE;
    private static final ABSClock CLOCK = new ABSClock();

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MatchingGameModel _model;
    private MatchingGameCanvas _canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MatchingGameModule( Frame parentFrame ) {
        super( TITLE, CLOCK );

        // Model
        _model = new MatchingGameModel( CLOCK );

        // Canvas
        _canvas = new MatchingGameCanvas( _model, this );
        setSimulationPanel( _canvas );

        // No control Panel
        setControlPanel( null );
        
        //  No clock controls
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
        System.out.println( getClass().getName() + ".reset" );//XXX
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public MatchingGameConfig save() {

        MatchingGameConfig config = new MatchingGameConfig();

        // Module
        config.setActive( isActive() );

        //XXX call config setters
        
        return config;
    }

    public void load( MatchingGameConfig config ) {

        // Module
        if ( config.isActive() ) {
            AcidBaseSolutionsApplication.getInstance().setActiveModule( this );
        }

        //XXX call config getters
    }
}
