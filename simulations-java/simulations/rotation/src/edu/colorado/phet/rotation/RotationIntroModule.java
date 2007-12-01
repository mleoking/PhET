package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.torque.TorqueModel;

/**
 * Created by: Sam
 * Nov 30, 2007 at 4:27:51 PM
 */
public class RotationIntroModule extends Module {

    private RotationModel torqueModel;
    private VectorViewModel vectorViewModel = new VectorViewModel();
    private AngleUnitModel angleUnitModel = new AngleUnitModel( false );

    public RotationIntroModule( JFrame parentFrame ) {
        super( "Intro", new RotationClock() );
        torqueModel = new TorqueModel( (ConstantDtClock) getClock() );
        RotationIntroSimulationPanel panel = new RotationIntroSimulationPanel( this, parentFrame );
        setSimulationPanel( panel );
        addRepaintOnActivateBehavior();
    }

    public RotationModel getRotationModel() {
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

    public RotationClock getRotationClock() {
        return (RotationClock) getClock();
    }
}
