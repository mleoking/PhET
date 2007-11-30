package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationClock;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:57:07 AM
 */
public class IntroModule extends Module {
    private TorqueModel torqueModel;
    private VectorViewModel vectorViewModel = new VectorViewModel();
    private AngleUnitModel angleUnitModel = new AngleUnitModel( false );

    public IntroModule( JFrame parentFrame ) {
        super( "Intro", new RotationClock() );
        torqueModel = new TorqueModel( (ConstantDtClock) getClock() );
        IntroSimulationPanel panel = new IntroSimulationPanel( this );
        setSimulationPanel( panel );
    }

    public TorqueModel getTorqueModel() {
        return torqueModel;
    }

    public VectorViewModel getVectorViewModel() {
        return vectorViewModel;
    }

    public AngleUnitModel getAngleUnitModel() {
        return angleUnitModel;
    }

    public void reset() {
        super.reset();
        torqueModel.resetAll();
    }
}
