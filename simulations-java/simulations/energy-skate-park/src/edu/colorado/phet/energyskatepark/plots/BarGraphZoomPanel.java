/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.view.bargraphs.BarGraphSet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 2, 2006
 * Time: 12:00:34 AM
 *
 */

public class BarGraphZoomPanel extends VerticalLayoutPanel {
    private double dy = 1.2;
    private BarGraphSet barGraphSet;
    private ModelViewTransform1D origTx;

    public BarGraphZoomPanel( BarGraphSet barGraphSet ) {
        this.barGraphSet = barGraphSet;
        origTx = barGraphSet.getTransform1D();
        try {
            JButton comp = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "energy-skate-park/images/icons/glass-20-plus.gif" ) ) );
            comp.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoom( +dy );
                }
            } );
            add( comp );
            JButton zoomIn = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "energy-skate-park/images/icons/glass-20-minus.gif" ) ) );
            zoomIn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoom( 1.0 / dy );
                }
            } );
            add( zoomIn );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void zoom( double value ) {
        ModelViewTransform1D transform1D = barGraphSet.getTransform1D();
        barGraphSet.setTransform1D( new ModelViewTransform1D( transform1D.getMinModel(), transform1D.getMaxModel() / value,
                                                              transform1D.getMinView(), transform1D.getMaxView() ) );
    }

    public void reset() {
        barGraphSet.setTransform1D( origTx );
    }
}
