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

//import jai.*; //requires JAI

public class BF2 extends LookupTable {
    public BF2() {
        super( 0, 4 );
    }

    //public int[]WHITE=new int[]{0,0,0,0};
    public int[] WHITE = new int[]{255, 255, 255, 255};//,150};
    int threshold = 207 * 3;///500;//25*3;

    public int[] lookupPixel( int[] src, int[] dst ) {
        int sum = 0;
        for( int i = 0; i < src.length; i++ ) {
            sum += src[i];
        }
        //System.err.println("Sum="+sum+", threshold="+threshold);
        if( sum > threshold ) {
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

    public void doit( String in, String outFilename ) {
        Component observer = new JLabel();
        ImageLoader loader = new ResourceLoader3( (URLClassLoader)getClass().getClassLoader(), observer );

        BufferedImage balloon = loader.loadBufferedImage( in );
        LookupOp lo = new LookupOp( this, null );
        BufferedImage out = lo.filter( balloon, null );
        System.err.println( "Created output filter: " + out );

        JFrame f = new JFrame();
        LayeredPainter lp = new LayeredPainter();
        lp.addPainter( new FixedImagePainter( out ) );
        PainterPanel pp = new PainterPanel( lp );
        f.setContentPane( pp );
        f.setSize( 400, 400 );
        f.setVisible( true );
        JFrame f2 = new JFrame( "original" );
        LayeredPainter lp2 = new LayeredPainter();
        lp2.addPainter( new FixedImagePainter( balloon ) );
        f2.setContentPane( new PainterPanel( lp2 ) );
        f2.setSize( 400, 400 );
        f2.setLocation( 400, 0 );
        f2.setVisible( true );
        //FileStore.save(out,outFilename); //requires JAI
        throw new RuntimeException( "Requires JAI.  See documentation." );

    }

    public static void main( String[] args ) {
        new BF2().doit( "images/balloon5.jpg", "balloon5_filter.tiff" );
    }
}
