/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller;


import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.application.Module;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class LaserControlPanel extends PhetControlPanel {

    /**
     *
     * @param application
     */
    public LaserControlPanel( Module module, JPanel controls ) {
        super( module, controls );
//    public LaserControlPanel( PhetApplication application ) {
//        init();
    }

    /**
     *
     */
    private void init() {

        this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        this.setPreferredSize( new Dimension( 160, 300 ) );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );

//        LaserSystem laserSystem = (LaserSystem)PhetApplication.instance().getPhysicalSystem();
//        this.add( new StimulatingBeamControl( laserSystem.getStimulatingBeam() ) );
//        this.add( new PumpingBeamControl() );
//        this.add( new SpontaneousEmissionTimeControlPanel() );
//        this.add( new SimulationRateControlPanel( 1, 40, 10 ));
//        this.add( new RightMirrorReflectivityControlPanel() );
    }

    /**
     *
     */
    public void clear() {
    }
}
