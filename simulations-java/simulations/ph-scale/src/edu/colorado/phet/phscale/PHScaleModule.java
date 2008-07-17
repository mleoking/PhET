/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.PHScaleModel;
import edu.colorado.phet.phscale.view.beaker.ParticlesNode;

/**
 * PHScaleModule is the sole module for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int CLOCK_FRAME_RATE = 25; // fps
    private static final double CLOCK_DT = 1;
    private static final IClock CLOCK = new ConstantDtClock( 1000 / CLOCK_FRAME_RATE, CLOCK_DT );
    
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
        
        // Model
        _model.getLiquid().setLiquidDescriptor( LiquidDescriptor.getDefaultLiquid() );
        
        // View-specific controls
        _canvas.getBeakerControlNode().setMoleculeCountSelected( false );
        _canvas.getBeakerControlNode().setRatioSelected( false );
        _canvas.getGraphControlNode().setConcentrationSelected( true );
        _canvas.getGraphControlNode().setLogSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    // for attaching developer controls
    public ParticlesNode dev_getParticlesNode() {
       return _canvas.dev_getParticlesNode();
    }
}
