/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThreeLevelControlPanel extends LaserControlPanel {

    /**
     *
     */
    public ThreeLevelControlPanel( Module module, AbstractClock clock ) {
        super( module, new ControlPanel( (LaserModel)module.getModel(), clock ));
    }

    private static class ControlPanel extends JPanel {
        ControlPanel( LaserModel model, AbstractClock clock ) {
            this.setLayout( new GridLayout( 7, 1 ) );
            this.setPreferredSize( new Dimension( 160, 300 ) );

            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );

            this.add( new PumpingBeamControl( model.getPumpingBeam() ) );
            this.add( new StimulatingBeamControl( model ) );
            this.add( new HighEnergyHalfLifeControl( model ) );
            this.add( new MiddleEnergyHalfLifeControl( model ) );
            ResonatingCavity cavity = model.getResonatingCavity();
            this.add( new RightMirrorReflectivityControlPanel( cavity ) );
            this.add( new SimulationRateControlPanel( clock, 1, 40, 10 ) );

            String s = GraphicsUtil.formatMessage( "Show high to\nmid emissions" );
            final JCheckBox showHighToMidEmissionCB = new JCheckBox( s );
            this.add( showHighToMidEmissionCB );
            showHighToMidEmissionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (LaserApplication)PhetApplication.instance() ).displayHighToMidEmission( showHighToMidEmissionCB.isSelected() );
                }
            } );
        }

        /**
         *
         */
        public void clear() {
        }
    }
}
