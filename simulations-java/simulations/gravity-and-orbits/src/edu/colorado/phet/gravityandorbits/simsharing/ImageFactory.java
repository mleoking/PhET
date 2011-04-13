// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

/**
 * Encapsulates thumbnail creation, reusing BufferedImages as necessary for performance reasons.
 *
 * @author Sam Reid
 */
public class ImageFactory {
    private BufferedImage image;
    private int count;
    private int numTimes = 10;//sim runs at 30fps //REVIEW what is the mapping from 30fps to 10?

    public BufferedImage getThumbnail( PhetFrame frame, int width ) {
        return BufferedImageUtils.multiScaleToWidth( toImage( frame ), width );
    }

    public BufferedImage toImage( JFrame frame ) {
        count++;
        if ( image == null || image.getWidth() != frame.getWidth() || image.getHeight() != frame.getHeight() ) {
            image = new BufferedImage( frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB );
            System.out.println( "new image created" );
        }
        //TODO: could be done in a different thread?
        if ( count % numTimes == 0 ) {
            Graphics2D g2 = image.createGraphics();
            frame.getContentPane().paint( g2 );
            g2.dispose();
//            image = sendFromRobot( frame );
        }
        return image;
    }

    //REVIEW unused, delete or doc
    private BufferedImage sendFromRobot( JFrame frame ) {
        try {
            Robot robot = new Robot();
            BufferedImage capture = robot.createScreenCapture( new Rectangle( frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight() ) );
            return capture;
        }
        catch ( AWTException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
