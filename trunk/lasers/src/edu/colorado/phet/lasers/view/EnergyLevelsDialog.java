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

    public EnergyLevelsDialog( Frame parent, JPanel energyLevelsPanel ) {
        super( parent, "Energy Level Populations" );
        //        this.setUndecorated( true );
        this.getContentPane().add( energyLevelsPanel );
        setSize( energyLevelsPanel.getSize() );
        pack();
    }
}
