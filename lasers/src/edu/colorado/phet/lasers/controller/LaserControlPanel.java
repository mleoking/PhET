/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Oct 27, 2004
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import javax.swing.*;
import java.awt.*;

public class LaserControlPanel extends PhetControlPanel {
    private GridBagConstraints gbc;
    private JPanel laserControlPane;

    public LaserControlPanel( BaseLaserModule module ) {
        super( module );
        laserControlPane = new JPanel( new GridBagLayout() );
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.HORIZONTAL,
                                      new Insets( 0, 0, 0, 0 ),
                                      0, 0 );

        super.setControlPane( laserControlPane );
    }

    public void addControl( Component component ) {
        gbc.gridy++;
        laserControlPane.add( component, gbc );
    }
}
