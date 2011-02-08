// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculesandlight;

import java.awt.Frame;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.moleculesandlight.view.MoleculesAndLightCanvas;
import edu.colorado.phet.moleculesandlight.view.MoleculesAndLightControlPanel;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that allows the user to shoot photons at various
 * molecules and see what happens.
 *
 * @author John Blanco
 */
public class MoleculesAndLightModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Constants that control the clock.  Note that since the true time scale
    // is unreasonable (since we are working with flying photons and
    // vibrating atoms) that we just use real time for the clock time and
    // expect the model to move things at speeds that appear reasonable to the
    // users.

    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    private static final int CLOCK_DELAY = 1000 / CLOCK_FRAME_RATE;
    private static final double CLOCK_DT = 1000 / CLOCK_FRAME_RATE;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // The usual suspects.
    private final PhotonAbsorptionModel model;
    private final PhetPCanvas canvas;
    private final MoleculesAndLightControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    // The property controls whether the background should be white instead of
    // the usual black.  This was requested to make it easier to photocopy
    // activities that contain screens shots of this simulation.
    private final Property<Boolean> whiteBackgroundProperty = new Property<Boolean>( false );

    // Developer controls
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MoleculesAndLightModule( Frame parentFrame ) {
        super( MoleculesAndLightResources.getString( "ModuleTitle.PhotonAbsorptionModule" ),
                new ConstantDtClock( CLOCK_DELAY, CLOCK_DT ) );

        // Physical model
        model = new PhotonAbsorptionModel( (ConstantDtClock) getClock(), PhotonAbsorptionModel.PhotonTarget.SINGLE_CO_MOLECULE );

        // Canvas
        canvas = new MoleculesAndLightCanvas( parentFrame, this, model );
        setSimulationPanel( canvas );

        // Control panel.
        controlPanel = new MoleculesAndLightControlPanel( this, model );
        setControlPanel( controlPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
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

    public Property<Boolean> getWhiteBackgroundProperty() {
        return whiteBackgroundProperty;
    }
}
