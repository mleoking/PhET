// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationClock;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * Created by: Sam
 * Nov 30, 2007 at 4:27:51 PM
 */
public class RotationIntroModule extends Module {

    private RotationModel rotationModule;
    private VectorViewModel vectorViewModel = new VectorViewModel();
    private AngleUnitModel angleUnitModel = new AngleUnitModel( false );
    private RotationIntroSimulationPanel rotationIntroSimulationPanel;

    public RotationIntroModule( JFrame parentFrame ) {
        super( RotationStrings.getString( "module.intro.rotation" ), new RotationClock() );
        rotationModule = new RotationModel( (ConstantDtClock) getClock() );
        rotationIntroSimulationPanel = new RotationIntroSimulationPanel( this, parentFrame );
        setSimulationPanel( rotationIntroSimulationPanel );
        addRepaintOnActivateBehavior();
    }

    public RotationIntroSimulationPanel getRotationIntroSimulationPanel() {
        return rotationIntroSimulationPanel;
    }

    public RotationModel getRotationModel() {
        return rotationModule;
    }

    public VectorViewModel getVectorViewModel() {
        return vectorViewModel;
    }

    public AngleUnitModel getAngleUnitModel() {
        return angleUnitModel;
    }

    public void reset() {
        super.reset();
        rotationModule.resetAll();
    }

    public RotationClock getRotationClock() {
        return (RotationClock) getClock();
    }
}
