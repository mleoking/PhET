// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactionsandrates.view.factories;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class TextureImageFactory {
    private static final int DEFAULT_SIZE = 400;

    public static Paint createTexturePaint( Color background, Color foreground, double lineWidth ) {
        return newTexturePaint( createTextureImage( background, foreground, DEFAULT_SIZE, lineWidth ) );
    }

    public static BufferedImage createTextureImage( Color background, Color foreground, double lineWidth ) {
        return createTextureImage( background, foreground, DEFAULT_SIZE, lineWidth );
    }

    public static BufferedImage createTextureImage( Color background, Color foreground, int size, double lineWidth ) {
        BufferedImage image = new BufferedImage( size, size, BufferedImage.TYPE_INT_RGB );

        Graphics2D g2d = image.createGraphics();

        g2d.setColor( background );
        g2d.fillRect( 0, 0, size, size );

        Stroke stroke = new BasicStroke( (float)lineWidth );

        g2d.setColor( foreground );
        g2d.setStroke( stroke );
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        double numLines = size / ( lineWidth * Math.sqrt( 2.0 ) );

        double distBetweenLines = 2.0 * size / numLines;

        for( int i = 0; i < numLines + 1; i++ ) {
            double xStart = i * distBetweenLines - size - lineWidth,
                    yStart = 0.0 - lineWidth,
                    xEnd = xStart + size + lineWidth,
                    yEnd = size + lineWidth;

            g2d.draw( new Line2D.Double( xStart, yStart,
                                         xEnd, yEnd ) );
        }

        return image;
    }

    private static Paint newTexturePaint( BufferedImage texture ) {
        return new TexturePaint( texture,
                                 new Rectangle( 0, 0, texture.getWidth(),
                                                texture.getHeight() ) );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();

        frame.setContentPane( new JLabel( new ImageIcon( createTextureImage( Color.BLUE, Color.RED, 400, 5 ) ) ) );

        frame.pack();
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
