package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;

public class PedigreeChartPanel extends JPanel {

    private static final int M = 100;

    public PedigreeChartCanvas pedigreeCanvas;
    public JScrollBar bar;

    private NaturalSelectionModel model;

    public PedigreeChartPanel( NaturalSelectionModel model ) {
        super( new GridBagLayout() );

        this.model = model;

        pedigreeCanvas = new PedigreeChartCanvas( model );
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
        add( pedigreeCanvas, c );
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

        pedigreeCanvas.addComponentListener( new ComponentListener() {
            public void componentResized( ComponentEvent componentEvent ) {
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
                checkSize();
            }
        } );
    }

    public void checkSize() {
        double bounds = pedigreeCanvas.getRoot().computeFullBounds( null ).width;
        double size = pedigreeCanvas.getSize().getWidth();

        if ( size >= bounds ) {
            bar.setVisible( false );
            pedigreeCanvas.setCenterPoint( 0 );
        }
        else {
            if ( bar.isVisible() ) {
                pedigreeCanvas.setCenterPoint( ( (double) bar.getValue() ) * ( bounds - size ) / ( 2.0 * M ) );
            }
            else {
                bar.setVisible( true );
                bar.setValue( 0 );
                pedigreeCanvas.setCenterPoint( ( (double) bar.getValue() ) * ( bounds - size ) / ( 2.0 * M ) );
            }
        }

        pedigreeCanvas.layoutNodes();
    }

    public void reset() {
        pedigreeCanvas.reset();
    }

    public void displayBunny( Bunny bunny ) {
        pedigreeCanvas.displayBunny( bunny );
    }
}
