/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.lasers.LaserSimulation;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThreeLevelControlPanel extends PhetControlPanel {

    public ThreeLevelControlPanel( Module module, AbstractClock clock ) {
        super( module, new ControlPanel( (LaserModel)module.getModel(), clock ) );
    }

    private static class ControlPanel extends JPanel {
        ControlPanel( LaserModel model, AbstractClock clock ) {
            this.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL,
                                                             new Insets( 0, 0, 0, 0 ),
                                                             0, 0 );
            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );

            this.add( new PumpingBeamControl( model.getPumpingBeam() ), gbc );
            gbc.gridy++;
            this.add( new StimulatingBeamControl( model ), gbc );
            gbc.gridy++;
            this.add( new HighEnergyHalfLifeControl( model ), gbc );
            gbc.gridy++;
            this.add( new MiddleEnergyHalfLifeControl( model ), gbc );
            gbc.gridy++;
            ResonatingCavity cavity = model.getResonatingCavity();
            this.add( new RightMirrorReflectivityControlPanel( cavity ), gbc );
            gbc.gridy++;
            this.add( new SimulationRateControlPanel( clock, 1, 40, 10 ), gbc );

            String s = GraphicsUtil.formatMessage( "Show high to\nmid emissions" );
            final JCheckBox showHighToMidEmissionCB = new JCheckBox( s );
            gbc.gridy++;
            this.add( showHighToMidEmissionCB, gbc );
            showHighToMidEmissionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (LaserSimulation)PhetApplication.instance() ).displayHighToMidEmission( showHighToMidEmissionCB.isSelected() );
                }
            } );
        }
    }
}
