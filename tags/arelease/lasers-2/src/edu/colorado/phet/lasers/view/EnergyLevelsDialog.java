/**
 * Class: EnergyLevelsDialog
 * Package: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 10:41:27 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.lasers.view;

import javax.swing.*;
import java.awt.*;

public class EnergyLevelsDialog extends JDialog {

    private ThreeEnergyLevelPanel energyLevelsPanel = new ThreeEnergyLevelPanel();

    public EnergyLevelsDialog( Frame parent ) {
        super( parent, "Energy Level Populations");
        this.getContentPane().add( energyLevelsPanel );
        Dimension d = energyLevelsPanel.getSize();
        setSize( energyLevelsPanel.getSize() );
        pack();
    }
}
