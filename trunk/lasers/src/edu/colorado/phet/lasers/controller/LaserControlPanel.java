/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Oct 27, 2004
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

public class LaserControlPanel extends PhetControlPanel {
    private boolean threeEnergyLevels;
    private PumpingBeamControl pumpingBeamControl;
    private HighEnergyHalfLifeControl highEnergyLifetimeControl;
    private StimulatingBeamControl stimulatingBeamControl;

    public LaserControlPanel( Module module, AbstractClock clock ) {
        super( module );
        super.setControlPane( new ControlPanel( (LaserModel)module.getModel(), clock ) );
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        this.threeEnergyLevels = threeEnergyLevels;
        pumpingBeamControl.setVisible( threeEnergyLevels );
        highEnergyLifetimeControl.setVisible( threeEnergyLevels );
    }

    private class ControlPanel extends JPanel {

        ControlPanel( LaserModel model, AbstractClock clock ) {
            this.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL,
                                                             new Insets( 0, 0, 0, 0 ),
                                                             0, 0 );
            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );

            stimulatingBeamControl = new StimulatingBeamControl( model );
            this.add( stimulatingBeamControl, gbc );
            gbc.gridy++;
            this.add( new MiddleEnergyHalfLifeControl( model ), gbc );
            gbc.gridy++;
            pumpingBeamControl = new PumpingBeamControl( model.getPumpingBeam() );
            this.add( pumpingBeamControl, gbc );
            gbc.gridy++;
            highEnergyLifetimeControl = new HighEnergyHalfLifeControl( model );
            this.add( highEnergyLifetimeControl, gbc );
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
            pumpingBeamControl.setVisible( threeEnergyLevels );
            highEnergyLifetimeControl.setVisible( threeEnergyLevels );
        }
    }

    public void setMaxPhotonRate( double photonsPerSecond ) {
        stimulatingBeamControl.setMaxPhotonRate( (int)photonsPerSecond );
    }
}
