/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:06:32 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class DetectorSheet extends GraphicLayerSet {
    private int width;
    private int height;
    private PhetShapeGraphic phetShapeGraphic;
    private BufferedImage bufferedImage;
    private PhetImageGraphic graphic;
    private PhetGraphic clearButtonJC;
    private JButton clearButton;
    private SchrodingerPanel schrodingerPanel;

    public DetectorSheet( SchrodingerPanel schrodingerPanel, int width, int height ) {
        super( schrodingerPanel );

        this.schrodingerPanel = schrodingerPanel;
        bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        graphic = new PhetImageGraphic( getComponent(), bufferedImage );
        addGraphic( graphic );

        phetShapeGraphic = new PhetShapeGraphic( schrodingerPanel, new Rectangle( width, height ), Color.white, new BasicStroke( 3 ), Color.black );
        phetShapeGraphic.paint( bufferedImage.createGraphics() );

        RenderingHints renderingHints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( renderingHints );

        clearButton = new JButton( "<html>Clear<br>Screen</html>" );
        clearButton.setMargin( new Insets( 2, 2, 2, 2 ) );
        clearButton.setFont( new Font( "Lucida Sans", Font.BOLD, 10 ) );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        clearButtonJC = PhetJComponent.newInstance( schrodingerPanel, clearButton );
        addGraphic( clearButtonJC );
        clearButtonJC.setVisible( false );
        clearButtonJC.setLocation( 5, 5 );
        this.width = width;
        this.height = height;

    }


    public void addDetectionEvent( int x, int y ) {
        clearButtonJC.setVisible( true );

//        addGraphic( new DetectionGraphic( this, x, y ) );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        new DetectionGraphic( this, x, y ).paint( g2 );
        repaint();
    }

    public void reset() {
//        clear();
        bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        phetShapeGraphic.paint( bufferedImage.createGraphics() );
        graphic.setImage( bufferedImage );
        clearButtonJC.setVisible( false );
//        addGraphic( graphic );
    }
}
