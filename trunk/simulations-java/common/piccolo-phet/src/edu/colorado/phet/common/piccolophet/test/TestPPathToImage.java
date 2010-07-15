package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Unfuddle #558.
 * Tests PPath.toImage.
 * The right and bottom edges of the PPath are clipped by a few pixels.
 */
public class TestPPathToImage {

    private static final float STROKE_WIDTH = 5f;

    public static void main( String[] args ) throws IOException {

        // Create a circle node
        PPath pathNode = new PPath( new Ellipse2D.Double( 0, 0, 100, 100 ) );
        pathNode.setPaint( Color.RED );
        pathNode.setStroke( new BasicStroke( STROKE_WIDTH ) );

        // Convert it to an image
        PImage imageNode = new PImage( pathNode.toImage() );
        imageNode.setOffset( 10, 10 );

        // Canvas
        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( imageNode );

        // Frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 150, 150 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        frame.setLocation( (int) ( screenSize.getWidth() / 2 - frame.getWidth() / 2 ),
                           (int) ( screenSize.getHeight() / 2 - frame.getHeight() / 2 ) );
        frame.setVisible( true );

        //Save to a file for inspection
        final File file = new File( System.getProperty( "user.home" ), "TestPPathToImage.png" );
        ImageIO.write( (RenderedImage) pathNode.toImage(), "PNG", new FileOutputStream( file ) );

        //Indicate where it was saved, since we used the working directory, which may be unknown
        System.out.println( "Saved file to " + file.getAbsolutePath() );
    }
}
