/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import net.jmge.gif.TestJMGE;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 11:24:13 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class MovieRecorder implements ClockTickListener {
    JFrame frame;
    int index = 0;
    String name;
    int count = 0;
    ArrayList images = new ArrayList();

    public MovieRecorder( JFrame frame, String name ) {
        this.frame = frame;
        this.name = name;
    }

    public void exportAll() {
        BufferedImage[] im = (BufferedImage[])images.toArray( new BufferedImage[0] );
        try {
//            AnimGIFTest.saveImage( im );
            TestJMGE.writeAnimatedGIF( im, "annotation", true, 10, new FileOutputStream( "name_" + index + ".gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void clockTicked( AbstractClock c, double dt ) {
        if( count++ % 3 != 0 ) {
            return;
        }
//        BufferedImage im = new BufferedImage( frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB );
        BufferedImage im = new BufferedImage( frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_BYTE_INDEXED );
//        BufferedImage im = new BufferedImage( frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_USHORT_555_RGB);
        Graphics2D g2 = im.createGraphics();
        frame.paint( g2 );
        g2.dispose();

        try {

//            ImageIO.write( im, "png", new File( name + "_" + index + ".png" ) );
            images.add( im );
            index++;

            if( index > 20 && index % 20 == 1 ) {
                System.out.println( "exporting i=" + index );
                exportAll();
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
