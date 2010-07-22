/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse;

import java.awt.Frame;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.greenhouse.controlpanel.PhotonAbsorptionControlPanel;
import edu.colorado.phet.greenhouse.developer.PhotonAbsorptionParamsDlg;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
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
    // Class Data
    //----------------------------------------------------------------------------
    
    // Constants that control the clock.  Note that since the true time scale
    // is unreasonable (since we are working with flying photons and
    // oscillating atoms) that we just use real time for the clock time and
    // expect the model to move things at speeds that appear reasonable to the
    // users.
    private static final int CLOCK_DELAY = 1000 / GreenhouseDefaults.CLOCK_FRAME_RATE;
    private static final double CLOCK_DT = 1000 / GreenhouseDefaults.CLOCK_FRAME_RATE;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private PhotonAbsorptionModel model;
    private PhetPCanvas canvas;
    private PhotonAbsorptionControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;
    
    // Developer controls
    private PhotonAbsorptionParamsDlg photonAbsorptionParamsDlg;
    private boolean photonAbsorptionParamsDlgVisible;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhotonAbsorptionModule( Frame parentFrame ) {
    	// TODO: i18n
        super( "Photon Absorption", new ConstantDtClock( CLOCK_DELAY, CLOCK_DT ));
        
        // Physical model
        model = new PhotonAbsorptionModel( (ConstantDtClock)getClock());

        // Canvas
        canvas = new PhotonAbsorptionCanvas(model);
        setSimulationPanel( canvas );
        
        // Control panel.
        controlPanel = new PhotonAbsorptionControlPanel(this, model);
        setControlPanel(controlPanel);
        
        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }
        
        // Developer controls.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            photonAbsorptionParamsDlg = new PhotonAbsorptionParamsDlg( model );
            photonAbsorptionParamsDlgVisible = false;
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
	@Override
    protected JComponent createClockControlPanel( IClock clock ) {
		clockControlPanel = new PiccoloClockControlPanel( clock );
        return clockControlPanel;
    }

    @Override
    public void reset() {
        model.reset();
    }

    @Override
    public void activate() {
        super.activate();
        if ( photonAbsorptionParamsDlg != null && photonAbsorptionParamsDlgVisible ) {
            photonAbsorptionParamsDlg.setVisible( true );
        }
    }
    
    @Override
    public void deactivate() {
        if ( photonAbsorptionParamsDlg != null ) {
            photonAbsorptionParamsDlg.setVisible( false );
        }
        super.deactivate();
    }

    public void setPhotonAbsorptionParamsDlgVisible(boolean visible){
        photonAbsorptionParamsDlgVisible = visible;
        if ( isActive() && photonAbsorptionParamsDlg != null ) {
            photonAbsorptionParamsDlg.setVisible( visible );
        }
    }
}
