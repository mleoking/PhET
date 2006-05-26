/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 12:54:19 PM
 * Copyright (c) May 26, 2006 by Sam Reid
 */

public class JetPackGraphic extends PhetPNode {
//    private PImage flameGraphic;
//    private final BufferedImage[] flames = new BufferedImage[3];
//    private int flameFrame = 0;
    private BodyGraphic skater;
    private PImage jetPackGraphic;

    public JetPackGraphic( BodyGraphic bodyGraphic ) {
        this.skater = bodyGraphic;
//        try {
//            flames[0] = ImageLoader.loadBufferedImage( "images/myflames/flames1.gif" );
//            flames[1] = ImageLoader.loadBufferedImage( "images/myflames/flames2.gif" );
//            flames[2] = ImageLoader.loadBufferedImage( "images/myflames/flames3.gif" );
//        }
//        catch( IOException e ) {
//            e.printStackTrace();
//        }
//        flameGraphic = new PImage( flames[0] );
        jetPackGraphic = PImageFactory.create( "images/jet_pack-nat-geo.gif" );
        addChild( jetPackGraphic );
    }

    public void update() {
        System.out.println( "getFullBounds() = " + getFullBounds() );
//        flameGraphic.setTransform( );
//        jetPackGraphic.setOffset( skater.getSkater().getFullBounds().getCenter2D());
//        jetPackGraphic.setTransform( new AffineTransform( ) );
//        jetPackGraphic.setTransform( skater.getTransform() );
        jetPackGraphic.setTransform( skater.getSkater().getTransform() );
//
//        if( Math.random() < 0.4 ) {
//            flameFrame = ( flameFrame + 1 ) % 3;
//            flameGraphic.setImage( flames[flameFrame] );
//        }
//
//        flameGraphic.rotateInPlace( Math.PI );
//        flameGraphic.translate( 0, -skater.getSkater().getHeight() + 3 );
    }

}
