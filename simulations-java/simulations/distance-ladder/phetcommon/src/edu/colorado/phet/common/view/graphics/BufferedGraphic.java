/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.GraphicsSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Feb 21, 2004
 * Time: 1:15:56 AM
 * Copyright (c) Feb 21, 2004 by Sam Reid
 */
public class BufferedGraphic implements Graphic {
    BufferedImage buffer;
    Graphic graphic;
    Graphics2D bufferGraphics;
    private AffineTransform transform = new AffineTransform();
    private Color backgroundColor;
    private GraphicsSetup setup;

    public BufferedGraphic( BufferedImage buffer, Graphic graphic, Color backgroundColor, GraphicsSetup setup ) {
        this.buffer = buffer;
        this.graphic = graphic;
        this.backgroundColor = backgroundColor;
        this.setup = setup;
        this.bufferGraphics = buffer.createGraphics();
    }

    public void repaintBuffer() {
        setup.setup( bufferGraphics );
        bufferGraphics.setColor( backgroundColor );
        bufferGraphics.fillRect( 0, 0, buffer.getWidth(), buffer.getHeight() );
        graphic.paint( bufferGraphics );
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( buffer, transform );
    }

    public static void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel();
        Ellipse2D.Double ellipse = new Ellipse2D.Double( 10, 10, 40, 40 );
        ShapeGraphic sg = new ShapeGraphic( ellipse, Color.blue );
        BufferedImage buffer = new BufferedImage( 50, 50, BufferedImage.TYPE_INT_RGB );
        GraphicsSetup setup = new BasicGraphicsSetup();
        BufferedGraphic bufferedGraphic = new BufferedGraphic( buffer, sg, Color.white, setup );
        bufferedGraphic.repaintBuffer();
        ap.addGraphic( bufferedGraphic );
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( ap );
        jf.setSize( 400, 400 );
        jf.setVisible( true );
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
    }
}
