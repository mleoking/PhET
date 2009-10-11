package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;

/**
 * Panel that holds the bunny population statistics. Will expand the plot to the maximum size available.
 *
 * @author Jonathan Olson
 */
public class BunnyStatsPanel extends JPanel {
    private BunnyStatsCanvas bunnyStatsCanvas;

    public BunnyStatsPanel( NaturalSelectionModel model ) {
        super( new GridLayout( 1, 1 ) );

        bunnyStatsCanvas = new BunnyStatsCanvas( model );
        add( bunnyStatsCanvas );

        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
    }

    public void reset() {
        bunnyStatsCanvas.reset();
    }

    public BunnyStatsCanvas getBunnyStatsCanvas() {
        return bunnyStatsCanvas;
    }
}