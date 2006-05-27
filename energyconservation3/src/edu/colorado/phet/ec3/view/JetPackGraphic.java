/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 12:54:19 PM
 * Copyright (c) May 26, 2006 by Sam Reid
 */

public class JetPackGraphic extends PhetPNode {
    private BodyGraphic skater;
    private PImage jetPackGraphic;

    public JetPackGraphic( BodyGraphic bodyGraphic ) {
        this.skater = bodyGraphic;
        jetPackGraphic = PImageFactory.create( "images/jet_pack-nat-geo.gif" );
        addChild( jetPackGraphic );
    }

    public void update() {
        double scale = 0.5;
        AffineTransform tx = skater.createTransform( skater.getBodyModelWidth() * scale, skater.getBodyModelHeight() * scale,
                                                     jetPackGraphic.getImage().getWidth( null ), jetPackGraphic.getImage().getHeight( null ) );
        jetPackGraphic.setTransform( tx );
//        System.out.println( "jetPackGraphic.getFullBounds() = " + jetPackGraphic.getFullBounds() );
////        if (skater.i)
        if( skater.isFacingRight() ) {
            jetPackGraphic.translate( -jetPackGraphic.getImage().getWidth( null ), 0 );
        }
        else {
            jetPackGraphic.translate( jetPackGraphic.getImage().getWidth( null ), 0 );
        }
    }

}
