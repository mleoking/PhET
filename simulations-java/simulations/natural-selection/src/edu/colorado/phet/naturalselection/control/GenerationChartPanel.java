package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class GenerationChartPanel extends JPanel {

    private static final int M = 100;

    public GenerationChartCanvas generationCanvas;
    public JScrollBar bar;

    private NaturalSelectionModel model;

    public GenerationChartPanel( NaturalSelectionModel model ) {
        super( new GridBagLayout() );

        this.model = model;

        generationCanvas = new GenerationChartCanvas( model );
        bar = new JScrollBar( JScrollBar.HORIZONTAL );
        bar.setMaximum( M );
        bar.setMinimum( -M );
        bar.setValue( 0 );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        add( generationCanvas, c );
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        add( bar, c );

        bar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged( AdjustmentEvent adjustmentEvent ) {
                double bounds = generationCanvas.getRoot().computeFullBounds( null ).width;
                double size = NaturalSelectionDefaults.GENERATION_CHART_SIZE.getWidth();
                if ( bounds > size ) {
                    generationCanvas.setCenterPoint( ( (double) bar.getValue() ) * ( bounds - size ) / ( 2.0 * M ) );
                }
                else {
                    generationCanvas.setCenterPoint( 0 );
                }
            }
        } );
    }

    public void reset() {
        generationCanvas.reset();
    }
}
