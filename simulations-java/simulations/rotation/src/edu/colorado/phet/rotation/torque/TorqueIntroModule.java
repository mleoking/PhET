package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationClock;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:57:07 AM
 */
public class TorqueIntroModule extends Module {
    private TorqueModel torqueModel;
    private VectorViewModel vectorViewModel = new VectorViewModel();
    private AngleUnitModel angleUnitModel = new AngleUnitModel( false );

    public TorqueIntroModule( JFrame parentFrame ) {
        super( RotationStrings.getString( "module.intro.torque" ), new RotationClock() );
        torqueModel = new TorqueModel( getConstantDtClock() );
        TorqueIntroSimulationPanel panel = new TorqueIntroSimulationPanel( this, parentFrame );
        setSimulationPanel( panel );
        addRepaintOnActivateBehavior();
    }

    public ConstantDtClock getConstantDtClock() {
        return (ConstantDtClock) getClock();
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
