/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.model.LaserModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TwoLevelControlPanel extends LaserControlPanel {

    public TwoLevelControlPanel( Module module, AbstractClock clock ) {
        super( module, new ControlPanel( (LaserModel)module.getModel(), clock ) );
    }

    private static class ControlPanel extends JPanel {
        ControlPanel( LaserModel model, AbstractClock clock ) {

//            this.setLayout( new GridLayout( 6, 1 ) );
            this.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0,0,1,1,1,1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0,0,0,0 ),
                                                             0,0 );

//            this.setPreferredSize( new Dimension( 160, 300 ) );

            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );

            this.add( new StimulatingBeamControl( model ), gbc );
            gbc.gridy++;
            this.add( new MiddleEnergyHalfLifeControl( model ), gbc );
            gbc.gridy++;
            this.add( new RightMirrorReflectivityControlPanel( model.getResonatingCavity() ), gbc );
            gbc.gridy++;
            this.add( new SimulationRateControlPanel( clock, 1, 40, 10 ), gbc );
        }

    }
}
