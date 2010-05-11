/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse;

import java.awt.Frame;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.greenhouse.model.GreenhouseClock;
import edu.colorado.phet.greenhouse.view.GreenhouseEffectCanvas;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that presents the greenhouse effect within the
 * atmosphere.
 *
 * @author John Blanco
 */
public class GreenhouseEffectModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetPCanvas canvas;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GreenhouseEffectModule( Frame parentFrame ) {
    	// TODO: i18n
        super( GreenhouseResources.getString("ModuleTitle.GreenHouseModule"), new GreenhouseClock());
        
        // Physical model
//        _model = new XxxModel();

        // Canvas
        canvas = new GreenhouseEffectCanvas();
        setSimulationPanel( canvas );
        
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
    
	@Override
    protected JComponent createClockControlPanel( IClock clock ) {
		clockControlPanel = new PiccoloClockControlPanel( clock );
        return clockControlPanel;
    }
}
