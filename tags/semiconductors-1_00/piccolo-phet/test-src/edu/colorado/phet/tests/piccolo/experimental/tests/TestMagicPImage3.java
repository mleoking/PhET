/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental.tests;

import edu.colorado.phet.piccolo.OscillateActivity;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.tests.piccolo.experimental.MagicPImage3;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Aug 7, 2005
 * Time: 7:33:14 PM
 * Copyright (c) Aug 7, 2005 by Sam Reid
 */

public class TestMagicPImage3 extends PhetPCanvas {
    Random random = new Random();

    public TestMagicPImage3() {
        for( int i = 0; i < 100; i++ ) {
            PNode node = toPImage();
            OscillateActivity oscillate = new OscillateActivity( node, random.nextDouble() * 600, random.nextDouble() * 600 );
            getLayer().addChild( node );
            getRoot().addActivity( oscillate );
        }

    }

    private PNode toPImage() {
//        PImage pImage = new PImage( getClass().getClassLoader().getResource( "csdept3.gif" ) );
        int width = 50;
//        BufferedImage newImage = toImage( width );
        BufferedImage newImage= null;
        try {
//            newImage = ImageLoader.loadBufferedImage( "csdept3.gif");
            newImage = ImageLoader.loadBufferedImage( "images/cabinet.gif");
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        PImage pImage = new MagicPImage3( newImage );
//        PImage pImage = new PImage( newImage );
//        pImage.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        return pImage;
    }


    private BufferedImage toImage( int width ) {
        BufferedImage newImage = new BufferedImage( width, width, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = newImage.createGraphics();
        g2.setColor( new Color( random.nextFloat(), random.nextFloat(), random.nextFloat() ) );
        g2.fillRect( -1, -1, width + 2, width + 2 );
        return newImage;
    }

    public static void main( String[] args ) {
//        SynchronizedRepaintManager synchronizedRepaintManager = new SynchronizedRepaintManager();
//        RepaintManager.setCurrentManager( synchronizedRepaintManager );
        TestMagicPImage3 testMagicPImage2 = new TestMagicPImage3();
        testMagicPImage2.setPanEventHandler( testMagicPImage2.getPanEventHandler() );
        testMagicPImage2.setZoomEventHandler( testMagicPImage2.getZoomEventHandler() );
        testMagicPImage2.start();

    }

    private void start() {
        JFrame frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setContentPane( this );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
