// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.view.BunnyStatsCanvas;

/**
 * Panel that holds the bunny population statistics. Will expand the plot to the maximum size available.
 *
 * @author Jonathan Olson
 */
public class BunnyStatsPanel extends JPanel {
    private BunnyStatsCanvas bunnyStatsCanvas;

    public BunnyStatsPanel() {
        super( new GridLayout( 1, 1 ) );

        bunnyStatsCanvas = new BunnyStatsCanvas();
        add( bunnyStatsCanvas );

        setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
    }

    public void reset() {
        bunnyStatsCanvas.reset();
    }

    public BunnyStatsCanvas getBunnyStatsCanvas() {
        return bunnyStatsCanvas;
    }
}