// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionStrings;

/**
 * Displays the relative time until a new generation is born
 *
 * @author Jonathan Olson
 */
public class GenerationProgressPanel extends JPanel {

    private JProgressBar generationProgressBar;

    public GenerationProgressPanel() {
        super( new GridLayout( 2, 1 ) );

        setOpaque( false );

        add( new JLabel( NaturalSelectionStrings.CLOCK_TIME_UNTIL_NEXT_GENERATION ) );

        generationProgressBar = new JProgressBar( SwingConstants.HORIZONTAL );
        generationProgressBar.setValue( 0 );

        add( generationProgressBar );
    }

    public void setGenerationProgressPercent( int value ) {
        generationProgressBar.setValue( 100 - value );
    }

}
