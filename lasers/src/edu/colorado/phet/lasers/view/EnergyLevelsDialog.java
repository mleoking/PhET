/**
 * Class: EnergyLevelsDialog
 * Package: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 10:41:27 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.controller.HighEnergyHalfLifeControl;
import edu.colorado.phet.lasers.controller.MiddleEnergyHalfLifeControl;
import edu.colorado.phet.lasers.model.LaserModel;

import javax.swing.*;
import java.awt.*;

public class EnergyLevelsDialog extends JDialog {

    public EnergyLevelsDialog( Frame parent, JPanel energyLevelsPanel, LaserModel model ) {
        super( parent, "Energy Level Populations" );
        //        this.setUndecorated( true );
        Container contentPane = this.getContentPane();
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        JPanel controlPanel = new JPanel( new GridBagLayout() );

        if( energyLevelsPanel instanceof ThreeEnergyLevelMonitorPanel ) {
            controlPanel.add( new HighEnergyHalfLifeControl( model ), gbc );
            gbc.gridy++;
        }
        controlPanel.add( new MiddleEnergyHalfLifeControl( model ), gbc );
        //        gbc.gridy++;
        //        controlPanel.add( new

        gbc.gridy = 0;
        contentPane.add( controlPanel, gbc );
        gbc.gridx++;
        if( energyLevelsPanel instanceof TwoEnergyLevelMonitorPanel ) {
            gbc.gridheight = 2;
        }
        else if( energyLevelsPanel instanceof ThreeEnergyLevelMonitorPanel ) {
            gbc.gridheight = 3;
        }

        contentPane.add( energyLevelsPanel, gbc );
        pack();
    }
}
