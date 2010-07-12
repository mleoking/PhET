package edu.colorado.phet.common.piccolophet.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Tests PPath.toImage.  
 * On Macintosh, the right and bottom edges of the PPath are clipped by a few pixels.
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
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );

        //Save to a file for inspection
        final File file = new File("TestPPathToImage.png");
        ImageIO.write((RenderedImage) pathNode.toImage(),"PNG",new FileOutputStream(file));
        
        //Indicate where it was saved, since we used the working directory, which may be unknown
        System.out.println("Saved file to " + file.getAbsolutePath());
    }
}
