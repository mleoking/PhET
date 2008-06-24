/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.module;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.PHScaleModel;

/**
 * PHScaleModule is the sole module for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PHScaleModel _model;
    private PHScaleCanvas _canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PHScaleModule() {
        super( "", new ConstantDtClock( 1000, 1 ), true /* startsPaused */ );
        setLogoPanelVisible( false );

        // Model
        _model = new PHScaleModel();

        // Canvas
        _canvas = new PHScaleCanvas( _model, this );
        setSimulationPanel( _canvas );

        // Control Panel
        setControlPanel( null );
        
        // Clock controls
        setClockControlPanel( null );

        // Controller
        PHScaleController controller = new PHScaleController( _model, _canvas );
        
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
        //XXX
        
        // View-specific controls
        _canvas.getBeakerControlNode().setMoleculeCountSelected( true );
        _canvas.getBeakerControlNode().setRatioSelected( true );
    }
    
}
