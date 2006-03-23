/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 12:25:21 AM
 * Copyright (c) Mar 23, 2006 by Sam Reid
 */

public class PressureWaveGraphic extends PNode {
    private Lattice2D lattice;
    private BufferedImage blueImage;
    private int spacingBetweenCells = 10;
    private ArrayList particles = new ArrayList();
    private BufferedImage pinkImage;

    public PressureWaveGraphic( Lattice2D lattice ) {
        this.lattice = lattice;
        try {
            blueImage = ImageLoader.loadBufferedImage( "images/particle-pink.gif" );
            pinkImage = ImageLoader.loadBufferedImage( "images/particle-blue.gif" );
            blueImage = BufferedImageUtils.rescaleYMaintainAspectRatio( blueImage, 20 );
            pinkImage = BufferedImageUtils.rescaleYMaintainAspectRatio( pinkImage, 20 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        Random random = new Random();
        int MOD = 2;
        for( int i = 0; i < lattice.getWidth(); i++ ) {
            for( int j = 0; j < lattice.getHeight(); j++ ) {
                if( i % MOD == 0 && j % MOD == 0 ) {

//                    Particle particle = new Particle( blueImage, i, j );
                    Particle particle = new Particle( random.nextBoolean() ? pinkImage : blueImage, i, j );
                    addParticle( particle );
                }
            }
        }
    }

    private void addParticle( Particle particle ) {
        particles.add( particle );
        addChild( particle );
    }

    public void setSpaceBetweenCells( int spacingBetweenCells ) {
        this.spacingBetweenCells = spacingBetweenCells;
        update();
    }

    public double getDistBetweenCells() {
        return spacingBetweenCells;
    }

    class Particle extends PImage {
        private int homeX;
        private int homeY;
        private double a;
        private double b;
        private double speed = 4.0;//pixels per time step

        public Particle( Image newImage, int i, int j ) {
            super( newImage );
            this.homeX = i;
            this.homeY = j;

            this.a = i * spacingBetweenCells;
            this.b = j * spacingBetweenCells;
            update();
        }

        public void update() {
            //look near to x,y (but don't stray from homeX and homeY)
            double bestValue = Double.NEGATIVE_INFINITY;
            int windowSize = 8;
            Point bestPoint = null;
            for( int i = -windowSize / 2; i <= windowSize / 2; i++ ) {
                for( int j = -windowSize / 2; j <= windowSize / 2; j++ ) {
                    if( inBounds( homeX + i, homeY + j ) ) {
//                        if( bestPoint == null || (( lattice.getValue( homeX + i, homeY + j ) > bestValue ) && new Point( homeX + i, homeY + j ).distance( homeX, homeY ) < 5 ))
                        if( bestPoint == null || ( lattice.getValue( homeX + i, homeY + j ) > bestValue && new Point( homeX + i, homeY + j ).distance( homeX, homeY ) <= windowSize / 2 ) )
                        {
                            bestPoint = new Point( homeX + i, homeY + j );
                            bestValue = lattice.getValue( homeX + i, homeY + j );
                        }
                    }
                }
            }
//            this.x = bestPoint.x;
//            this.y = bestPoint.y;
            //step towards the peak
            double prefX = bestPoint.x * spacingBetweenCells;
            double prefY = bestPoint.y * spacingBetweenCells;
            Vector2D.Double vec = new Vector2D.Double( new Point2D.Double( a, b ), new Point2D.Double( prefX, prefY ) );

            if( vec.getMagnitude() >= 1.2 ) {
                vec.normalize();
                vec.scale( speed );
//                System.out.println( "vec = " + vec );
                Point2D dest = vec.getDestination( new Point2D.Double( a, b ) );
                a = dest.getX();
                b = dest.getY();
                setOffset( a, b );

            }
//            setOffset( 300,200);
//            offset();
        }

        private boolean inBounds( int i, int j ) {
            return i > 0 && j > 0 && i < lattice.getWidth() && j < lattice.getHeight();
        }
    }

    public void removeAllChildren() {
        super.removeAllChildren();
        particles.clear();
    }

    public void update() {
        //each particle moves toward high wave values near it's neighborhood.
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            particle.update();
        }
    }

    public void updateORIG() {
        removeAllChildren();
        for( int i = 0; i < lattice.getWidth(); i++ ) {
            for( int j = 0; j < lattice.getHeight(); j++ ) {
                Particle particle = new Particle( blueImage, i, j );
                if( lattice.getValue( i, j ) > 0.25 ) {
                    addParticle( particle );
                }
            }
        }
    }

}
