package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:57:07 AM
 */
public class AbstractTorqueModule extends AbstractRotationModule {
    public AbstractTorqueModule( String name,JFrame parentFrame ) {
        super( name,parentFrame );
    }

    protected RotationModel createModel( ConstantDtClock clock ) {
        return new TorqueModel( clock );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame ) {
        return new TorqueSimulationPanel( this, parentFrame );
    }

    public TorqueModel getTorqueModel() {
        return (TorqueModel) getRotationModel();
    }
}
