package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.view.RotationSimulationPanel;

import javax.swing.*;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:05:16 AM
 */
public class RotationModule extends AbstractRotationModule {


    public RotationModule( JFrame parentFrame ) {
        super( parentFrame );
    }

    protected RotationModel createModel( ConstantDtClock clock ) {
        return new RotationModel( clock );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame ) {
        return new RotationSimulationPanel( this, parentFrame );
    }


}
