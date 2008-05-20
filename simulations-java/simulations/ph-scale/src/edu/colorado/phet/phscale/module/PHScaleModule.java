/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.phscale.module;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.simtemplate.SimTemplateStrings;
import edu.colorado.phet.simtemplate.defaults.ExampleDefaults;

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
        super( SimTemplateStrings.TITLE_EXAMPLE_MODULE, ExampleDefaults.CLOCK );

        // Model
        _model = new PHScaleModel();

        // Canvas
        _canvas = new PHScaleCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        setControlPanel( null );
        
        // Clock controls
        setClockControlPanel( null );

        // Controller
        PHScaleController controller = new PHScaleController();
        
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
        //XXX
    }
    
}
