package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 18, 2007
 * Time: 12:51:21 PM
 */
public class TorqueModule extends AbstractTorqueModule {
    public TorqueModule( PhetFrame phetFrame ) {
        super( "Torque", phetFrame );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame ) {
        return new TorqueSimulationPanel( this, parentFrame );
    }

}
