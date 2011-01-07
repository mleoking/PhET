// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module.matchinggame;

import java.awt.Frame;

import edu.colorado.phet.advancedacidbasesolutions.AABSStrings;
import edu.colorado.phet.advancedacidbasesolutions.AdvancedAcidBaseSolutionsApplication;
import edu.colorado.phet.advancedacidbasesolutions.model.AABSClock;
import edu.colorado.phet.advancedacidbasesolutions.module.AABSAbstractModule;
import edu.colorado.phet.advancedacidbasesolutions.persistence.MatchingGameConfig;

/**
 * MatchingGameModule is the "Matching Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameModule extends AABSAbstractModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = AABSStrings.TITLE_MATCHING_GAME_MODULE;
    private static final AABSClock CLOCK = new AABSClock();

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MatchingGameModel model;
    private MatchingGameCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MatchingGameModule( Frame parentFrame ) {
        super( TITLE, CLOCK );

        // Model
        model = new MatchingGameModel( CLOCK );

        // Canvas
        canvas = new MatchingGameCanvas( model, this );
        setSimulationPanel( canvas );

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
        super.reset();
        model.reset();
        canvas.reset();
        load( MatchingGameDefaults.getInstance().getConfig() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public MatchingGameConfig save() {

        MatchingGameConfig config = new MatchingGameConfig();

        // Module
        config.setActive( isActive() );

        // this module has no other persistent state
        
        return config;
    }

    public void load( MatchingGameConfig config ) {

        // Module
        if ( config.isActive() ) {
            AdvancedAcidBaseSolutionsApplication.getInstance().setActiveModule( this );
        }

       // this module has no other persistent state
    }
}
