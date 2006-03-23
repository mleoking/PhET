/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.AbstractVector2D;
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
import java.util.Collections;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 12:25:21 AM
 * Copyright (c) Mar 23, 2006 by Sam Reid
 */

public class PressureWaveGraphic extends PNode {
    private Lattice2D lattice;
    private BufferedImage blueImageORIG;
    private int spacingBetweenCells = 10;
    private ArrayList particles = new ArrayList();
    private BufferedImage pinkImageORIG;
    private BufferedImage blueImage;
    private BufferedImage pinkImage;

    public PressureWaveGraphic( Lattice2D lattice ) {
        this.lattice = lattice;
        try {
            blueImageORIG = ImageLoader.loadBufferedImage( "images/particle-pink.gif" );
            pinkImageORIG = ImageLoader.loadBufferedImage( "images/particle-blue.gif" );
            blueImage = BufferedImageUtils.rescaleYMaintainAspectRatio( blueImageORIG, 20 );
            pinkImage = BufferedImageUtils.rescaleYMaintainAspectRatio( pinkImageORIG, 20 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        Random random = new Random();
        int MOD = 3;
        for( int i = 0; i < lattice.getWidth(); i++ ) {
            for( int j = 0; j < lattice.getHeight(); j++ ) {
                if( i % MOD == 0 && j % MOD == 0 ) {

//                    Particle particle = new Particle( blueImage, i, j );
                    Particle particle = new Particle( random.nextBoolean() ? pinkImage : blueImage, i, j );
                    addParticle( particle );
                }
            }
        }
        reorderChildren();
    }

    private void reorderChildren() {
        ArrayList all = new ArrayList( particles );
        super.removeAllChildren();
        Collections.shuffle( all );
        for( int i = 0; i < all.size(); i++ ) {
            Particle particle = (Particle)all.get( i );
            addChild( particle );
        }
    }

    public void setImageSize( int height ) {
        BufferedImage newBlue = BufferedImageUtils.rescaleYMaintainAspectRatio( blueImageORIG, height );
        BufferedImage newPink = BufferedImageUtils.rescaleYMaintainAspectRatio( pinkImageORIG, height );
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            if( particle.getImage() == blueImage ) {
                particle.setImage( newBlue );
            }
            else {
                particle.setImage( newPink );
            }
        }
        pinkImage = newPink;
        blueImage = newBlue;
    }

    public double getImageSize() {
        return pinkImage.getHeight();
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
        private Vector2D velocity = new Vector2D.Double();
        private BufferedImage newImage;

        public Particle( BufferedImage newImage, int i, int j ) {
            super( newImage );
            this.newImage = newImage;
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
            if( bestPoint != null ) {
                //step towards the peak
                double prefX = bestPoint.x * spacingBetweenCells;
                double prefY = bestPoint.y * spacingBetweenCells;
                Vector2D.Double vec = new Vector2D.Double( new Point2D.Double( a, b ), new Point2D.Double( prefX, prefY ) );

//            stepToTarget( vec );
                accelerateToTarget( vec );
//            accelerateFromNeighbors();//very expensive
            }

        }

//        private void accelerateFromNeighbors() {
//            double netForceX=0;
//            double netForceY=0;
////            Vector2D netForce=new Vector2D.Double( );
//            for( int i = 0; i < particles.size(); i++ ) {
//                Particle particle = (Particle)particles.get( i );
////                netForce.add( getForce(particle));
//                netForceX+=particle.getX();
//                netForceX-=particle.getX();
//            }
//            velocity=velocity.add(new Vector2D.Double( netForceX,netForceY ));
//        }

        private AbstractVector2D getForce( Particle particle ) {
            return new Vector2D.Double();
        }

        private void accelerateToTarget( Vector2D.Double vec ) {
            double acceleration = 0.4;
            if( vec.getMagnitude() >= 1.2 ) {
                vec.normalize();
                vec.scale( acceleration );
//                System.out.println( "vec = " + vec );

                velocity = velocity.add( vec );
                double MAX_V = 15;
                if( velocity.getX() > MAX_V ) {
                    velocity.setX( MAX_V );
                }
                if( velocity.getY() > MAX_V ) {
                    velocity.setY( MAX_V );
                }
                if( velocity.getX() < -MAX_V ) {
                    velocity.setX( -MAX_V );
                }
                if( velocity.getY() < -MAX_V ) {
                    velocity.setY( -MAX_V );
                }
                velocity.scale( 0.96 );//friction
                Point2D dest = velocity.getDestination( new Point2D.Double( a, b ) );
                a = dest.getX();
                b = dest.getY();
                setOffset( a, b );

            }
        }

        private void stepToTarget( Vector2D.Double vec ) {
            if( vec.getMagnitude() >= 1.2 ) {
                vec.normalize();
                vec.scale( speed );
//                System.out.println( "vec = " + vec );
                Point2D dest = vec.getDestination( new Point2D.Double( a, b ) );
                a = dest.getX();
                b = dest.getY();
                setOffset( a, b );

            }
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
                Particle particle = new Particle( blueImageORIG, i, j );
                if( lattice.getValue( i, j ) > 0.25 ) {
                    addParticle( particle );
                }
            }
        }
    }

}
