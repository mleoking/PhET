package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationClock;
import edu.colorado.phet.rotation.view.RotationSimulationPanel;
import edu.colorado.phet.rotation.view.AbstractRotationSimulationPanel;

/**
 * Created by: Sam
 * Nov 30, 2007 at 4:27:51 PM
 */
public class IntroRotationModule extends AbstractRotationModule {
    public IntroRotationModule( JFrame parentFrame ) {
        super( "Rotation", parentFrame );
    }

    protected RotationModel createModel( ConstantDtClock clock ) {
        return new RotationModel( clock );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame ) {
        return new RotationSimulationPanel( this, parentFrame );
    }

    public RotationClock getRotationClock() {
        return (RotationClock) getConstantDTClock();
    }
}
