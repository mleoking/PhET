package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.pedigree.PedigreeNode;

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
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add( generationCanvas, c );
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        add( bar, c );

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                checkSize();
            }
        } );

        generationCanvas.pedigreeNode.addListener( new PedigreeNode.Listener() {
            public void onGenerationAdded() {
                checkSize();
            }
        } );

        generationCanvas.addComponentListener( new ComponentListener() {
            public void componentResized( ComponentEvent componentEvent ) {
                //System.out.println( "Resized to: " + generationCanvas.getSize() );
                //generationCanvas.setCenterPoint( 0 );
                checkSize();
            }

            public void componentMoved( ComponentEvent componentEvent ) {

            }

            public void componentShown( ComponentEvent componentEvent ) {

            }

            public void componentHidden( ComponentEvent componentEvent ) {

            }
        } );

        bar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged( AdjustmentEvent adjustmentEvent ) {
                /*
                double bounds = generationCanvas.getRoot().computeFullBounds( null ).width;
                double size = NaturalSelectionDefaults.GENERATION_CHART_SIZE.getWidth();
                if ( bounds > size ) {
                    generationCanvas.setCenterPoint( ( (double) bar.getValue() ) * ( bounds - size ) / ( 2.0 * M ) );
                }
                else {
                    generationCanvas.setCenterPoint( 0 );
                }
                */
                checkSize();
            }
        } );
    }

    public void checkSize() {
        double bounds = generationCanvas.getRoot().computeFullBounds( null ).width;
        double size = generationCanvas.getSize().getWidth();

        if ( size >= bounds ) {
            bar.setVisible( false );
            generationCanvas.setCenterPoint( 0 );
        }
        else {
            if ( bar.isVisible() ) {
                generationCanvas.setCenterPoint( ( (double) bar.getValue() ) * ( bounds - size ) / ( 2.0 * M ) );
            }
            else {
                bar.setVisible( true );
                bar.setValue( 0 );
                generationCanvas.setCenterPoint( ( (double) bar.getValue() ) * ( bounds - size ) / ( 2.0 * M ) );
            }
        }

        generationCanvas.layoutNodes();
    }

    public void reset() {
        generationCanvas.reset();
    }
}
