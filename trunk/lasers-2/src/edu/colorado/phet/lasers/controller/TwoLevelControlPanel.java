/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.lasers.physics.LaserSystem;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TwoLevelControlPanel extends LaserControlPanel {

    public TwoLevelControlPanel( Module module ) {
        super( module, new ControlPanel( (LaserSystem)module.getModel() ) );
    }

    private static class ControlPanel extends JPanel {
        ControlPanel( LaserSystem laserSystem ) {

            this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
            this.setLayout( new GridLayout( 6, 1 ) );

            this.setPreferredSize( new Dimension( 160, 300 ) );

            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );

            this.add( new StimulatingBeamControl( laserSystem.getStimulatingBeam() ) );
            this.add( new MiddleEnergyHalfLifeControl() );
            this.add( new RightMirrorReflectivityControlPanel( laserSystem.getResonatingCavity() ) );
            this.add( new SimulationRateControlPanel( 1, 40, 10 ) );
        }

    }
}
