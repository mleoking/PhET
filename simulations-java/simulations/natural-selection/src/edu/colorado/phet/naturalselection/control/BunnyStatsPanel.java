package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;

public class BunnyStatsPanel extends JPanel {
    private BunnyStatsCanvas bunnyStatsCanvas;

    public BunnyStatsPanel( NaturalSelectionModel model ) {
        super( new GridLayout( 1, 1 ) );

        bunnyStatsCanvas = new BunnyStatsCanvas( model );
        add( bunnyStatsCanvas );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void reset() {
        bunnyStatsCanvas.reset();
    }
}