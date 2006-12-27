package edu.colorado.phet.balloon.util;

import phet.paint.FixedImagePainter;
import phet.paint.LayeredPainter;
import phet.paint.PainterPanel;
import phet.utils.ImageLoader;
import phet.utils.ResourceLoader3;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.net.URLClassLoader;

//import jai.*;

public class BalloonFilter extends LookupTable {
    public BalloonFilter() {
        super( 0, 4 );
    }

    //public int[]WHITE=new int[]{0,0,0,0};
    public int[] WHITE = new int[]{255, 255, 255, 255};//,150};
    int threshold = 240 * 3;

    public int[] lookupPixel( int[] src, int[] dst ) {
        int sum = 0;
        for( int i = 0; i < src.length; i++ ) {
            sum += src[i];
        }
        //System.err.println("Sum="+sum+", threshold="+threshold);
        if( sum < threshold ) {
            //System.err.println("src");
            return src;
        }
        else {
            //System.err.println("White");
            for( int i = 0; i < dst.length; i++ ) {
                dst[i] = WHITE[i];
            }
            return WHITE;
        }
    }

    public void doit() {
        Component observer = new JLabel();
        ImageLoader loader = new ResourceLoader3( (URLClassLoader)getClass().getClassLoader(), observer );

        BufferedImage balloon = loader.loadBufferedImage( "images/balloon5Invert.jpg" );
        BalloonFilter bf = new BalloonFilter();
        LookupOp lo = new LookupOp( bf, null );
        BufferedImage out = lo.filter( balloon, null );
        System.err.println( "Created output filter: " + out );

        JFrame f = new JFrame();
        LayeredPainter lp = new LayeredPainter();
        lp.addPainter( new FixedImagePainter( out ) );
        PainterPanel pp = new PainterPanel( lp );
        f.setContentPane( pp );
        f.setSize( 400, 400 );
        f.setVisible( true );
        //FileStore.save(out,"FilteredBalloon.tiff");
        throw new RuntimeException( "Requires JAI.  See documentation." );
    }

    public static void main( String[] args ) {
        new BalloonFilter().doit();
    }
}
