/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.module;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.PHScaleModel;

/**
 * PHScaleModule is the sole module for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final IClock CLOCK = new ConstantDtClock( 100, 1 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PHScaleModel _model;
    private PHScaleCanvas _canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PHScaleModule() {
        super( "", CLOCK, false /* startsPaused */ );
        setLogoPanelVisible( false );

        // Model
        _model = new PHScaleModel( CLOCK );

        // Canvas
        _canvas = new PHScaleCanvas( _model, this );
        setSimulationPanel( _canvas );

        // Control Panel
        setControlPanel( null );
        
        // Clock controls
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
        
        System.out.println( "PHScaleModule.reset" );//XXX
        
        // Model
        _model.getLiquid().setLiquidDescriptor( LiquidDescriptor.LEMON_JUICE );
        
        // View-specific controls
        _canvas.getBeakerControlNode().setMoleculeCountSelected( true );
        _canvas.getBeakerControlNode().setRatioSelected( false );
    }
    
}
