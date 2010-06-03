/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse;

import java.awt.Frame;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.greenhouse.controlpanel.PhotonAbsorptionControlPanel;
import edu.colorado.phet.greenhouse.model.GreenhouseClock;
import edu.colorado.phet.greenhouse.view.PhotonAbsorptionCanvas;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that allows the user to shoot photons at various
 * molecules and see what happens.
 *
 * @author John Blanco
 */
public class PhotonAbsorptionModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetPCanvas canvas;
    private PhotonAbsorptionControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhotonAbsorptionModule( Frame parentFrame ) {
    	// TODO: i18n
        super( "Photon Absorption", new GreenhouseClock());
        
        // Physical model
//        _model = new XxxModel();

        // Canvas
        canvas = new PhotonAbsorptionCanvas();
        setSimulationPanel( canvas );
        
        // Control panel.
        controlPanel = new PhotonAbsorptionControlPanel(this);
        setControlPanel(controlPanel);
        
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
