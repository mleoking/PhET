/**
 * Class: EnergyLevelsDialog
 * Package: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 10:41:27 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.physics.LaserModel;

import javax.swing.*;
import java.awt.*;

public class EnergyLevelsDialog extends JDialog {

    private ThreeEnergyLevelPanel energyLevelsPanel;

    public EnergyLevelsDialog( Frame parent, LaserModel model ) {
        super( parent, "Energy Level Populations");
        energyLevelsPanel = new ThreeEnergyLevelPanel( model );
        this.getContentPane().add( energyLevelsPanel );
        Dimension d = energyLevelsPanel.getSize();
        setSize( energyLevelsPanel.getSize() );
        pack();
    }
}
