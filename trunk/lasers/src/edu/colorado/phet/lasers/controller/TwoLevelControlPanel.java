/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.PhetControlPanel;
import edu.colorado.phet.lasers.view.*;
import edu.colorado.phet.lasers.physics.LaserSystem;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TwoLevelControlPanel extends PhetControlPanel {

    /**
     *
     */
    public TwoLevelControlPanel() {
        init();
    }

    /**
     *
     */
    private void init() {

        this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        this.setLayout( new GridLayout( 6, 1 ) );

        this.setPreferredSize( new Dimension( 160, 300 ) );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );

        LaserSystem laserSystem = (LaserSystem)PhetApplication.instance().getPhysicalSystem();
        this.add( new StimulatingBeamControl( laserSystem.getStimulatingBeam() ) );
        this.add( new MiddleEnergyHalfLifeControl() );
        this.add( new RightMirrorReflectivityControlPanel( laserSystem.getResonatingCavity() ) );
        this.add( new SimulationRateControlPanel( 1, 40, 10 ));
    }

    /**
     *
     */
    public void clear() {
    }
}
