/**
 * Class: StarMapControlPanel
 * Class: edu.colorado.phet.distanceladder.controller
 * User: Ron LeMaster
 * Date: Apr 12, 2004
 * Time: 2:43:58 PM
 */
package edu.colorado.phet.distanceladder.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StarMapControlPanel extends JPanel {
    private JButton starshipCoordsBtn;
    private StarMapModule module;

    public StarMapControlPanel( final StarMapModule module ) {
        super( new GridBagLayout() );
        this.module = module;

        // Create controls
        starshipCoordsBtn = new JButton( new AbstractAction( "Show Starship Coords" ) {
                    private boolean coordsOn = false;
                    public void actionPerformed( ActionEvent e ) {
                        coordsOn = !coordsOn;
                        starshipCoordsBtn.setText( coordsOn ? "Hide Starship Coords" : "Show StarshipCoords");
                        module.setStarshipCoordsEnabled( coordsOn );                        
                    }
                });

        // Lay out controls
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, starshipCoordsBtn,
                                              0, rowIdx,
                                              1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }
}
