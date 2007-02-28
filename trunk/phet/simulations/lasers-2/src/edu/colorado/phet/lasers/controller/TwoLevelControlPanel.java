/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.physics.LaserModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TwoLevelControlPanel extends LaserControlPanel {

    public TwoLevelControlPanel( Module module, AbstractClock clock ) {
        super( module, new ControlPanel( (LaserModel)module.getModel(), clock ) );
    }

    private static class ControlPanel extends JPanel {
        ControlPanel( LaserModel model, AbstractClock clock ) {

            this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
            this.setLayout( new GridLayout( 6, 1 ) );

            this.setPreferredSize( new Dimension( 160, 300 ) );

            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );

            this.add( new StimulatingBeamControl( model ) );
            this.add( new MiddleEnergyHalfLifeControl( model ) );
            this.add( new RightMirrorReflectivityControlPanel( model.getResonatingCavity() ) );
            this.add( new SimulationRateControlPanel( clock, 1, 40, 10 ) );
        }

    }
}
