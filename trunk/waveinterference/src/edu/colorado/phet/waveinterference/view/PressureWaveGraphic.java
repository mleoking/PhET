/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private SlitPotential slitPotential;
//    private WaveInterferenceModel waveInterferenceModel;
    private BufferedImage blueImageORIG;
    private int spacingBetweenCells = 10;
    private ArrayList particles = new ArrayList();
    private BufferedImage pinkImageORIG;
    private BufferedImage blueImage;
    private BufferedImage pinkImage;
    private double acceleration = 0.3;
    private double maxVelocity = 3;
    private double friction = 0.97;
    private boolean pinked = false;
    private PPath background;
    private static final int IMAGE_HEIGHT = 14;

    public PressureWaveGraphic( Lattice2D lattice, LatticeScreenCoordinates latticeScreenCoordinates, SlitPotential slitPotential ) {
        this.lattice = lattice;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.slitPotential = slitPotential;
//        this.waveInterferenceModel = waveInterferenceModel;
        background = new PPath();
        background.setPaint( Color.black );
        background.setStrokePaint( Color.gray );
        addChild( background );
        try {
            blueImageORIG = ImageLoader.loadBufferedImage( "images/particle-blue.gif" );
            pinkImageORIG = ImageLoader.loadBufferedImage( "images/particle-pink.gif" );
            blueImage = BufferedImageUtils.rescaleYMaintainAspectRatio( blueImageORIG, IMAGE_HEIGHT );
            pinkImage = BufferedImageUtils.rescaleYMaintainAspectRatio( pinkImageORIG, IMAGE_HEIGHT );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        int MOD = 3;
        for( int i = 0; i < lattice.getWidth(); i++ ) {
            for( int j = 0; j < lattice.getHeight(); j++ ) {
                if( i % MOD == 0 && j % MOD == 0 ) {
                    Particle particle = new Particle( isPink( i, j ) ? pinkImage : blueImage, i, j );
                    addParticle( particle );
                }
            }
        }
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateBounds();
            }
        } );
        updateBounds();
        reorderChildren();
        slitPotential.addListener( new SlitPotential.Listener() {
            public void slitsChanged() {
                doSlitsChanged();
            }
        } );
        doSlitsChanged();
    }

    //set particles whose origin is on top of the barrier to be invisible.
    private void doSlitsChanged() {
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            if( barrierCoversParticle( particle ) ) {
                particle.setVisible( false );
            }
            else {
                particle.setVisible( true );
            }
        }
    }

    private boolean barrierCoversParticle( Particle particle ) {
        Point home = particle.getHome();
        double pot = slitPotential.getPotential( home.x, home.y, 0 );
        return pot != 0;
    }

    private boolean isPink( int i, int j ) {
        return random.nextDouble() < 0.05;
//        return isPinkForCenter( i, j );
    }

    //this implementation of isPink sets just the center graphic to be pink.
    private boolean isPinkForCenter( int i, int j ) {
        if( !pinked && i >= lattice.getWidth() / 2 && j >= lattice.getHeight() / 2 ) {
            pinked = true;
            return true;
        }
        else {
            return false;
        }
    }

    private void updateBounds() {
        setOffset( latticeScreenCoordinates.toScreenCoordinates( 0, 0 ) );
        int spacingBetweenCells = (int)latticeScreenCoordinates.getCellWidth();
        setSpaceBetweenCells( spacingBetweenCells );
        background.setPathTo( new Rectangle2D.Double( 0, 0, latticeScreenCoordinates.getScreenRect().getWidth(), latticeScreenCoordinates.getScreenRect().getHeight() ) );
    }

    private void reorderChildren() {
        ArrayList all = new ArrayList( particles );
        super.removeAllChildren();
        addChild( background );
        Collections.shuffle( all );
        for( int i = 0; i < all.size(); i++ ) {
            Particle particle = (Particle)all.get( i );
            addChild( particle );
        }
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            if( particle.getImage() == pinkImage ) {
                removeChild( particle );
                addChild( particle );
            }
        }
    }

    public void setParticleImageSize( int height ) {
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

    public void setParticleAcceleration( double acceleration ) {
        this.acceleration = acceleration;
    }

    public double getParticleAcceleration() {
        return acceleration;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity( double value ) {
        this.maxVelocity = value;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction( double friction ) {
        this.friction = friction;
    }

    private Random random = new Random();

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

        public Point getHome() {
            return new Point( homeX, homeY );
        }

        class SearchResult {
            private double pressure;
            private Point location;

            public SearchResult( double pressure, Point location ) {
                this.pressure = pressure;
                this.location = location;
            }

            public double getPressure() {
                return pressure;
            }

            public Point getLocation() {
                return location;
            }
        }

        public void update() {
            //look near to x,y (but don't stray from homeX and homeY)
            SearchResult searchResult = searchForTarget();
            Point bestPoint = searchResult.getLocation();
            if( bestPoint != null ) {
                //step towards the peak
                double prefX = bestPoint.x * spacingBetweenCells;
                double prefY = bestPoint.y * spacingBetweenCells;
                Vector2D.Double vec = new Vector2D.Double( new Point2D.Double( a, b ), new Point2D.Double( prefX, prefY ) );
                double accelScale = 1.0;
                double frictionScale = 1.0;
                if( Math.abs( searchResult.getPressure() ) < 0.01 ) {
                    prefX = homeX * spacingBetweenCells;
                    prefY = homeY * spacingBetweenCells;
                    vec.rotate( random.nextDouble() * Math.PI * 2 );
                    accelScale = 0.5;
                    frictionScale = 1.0 / friction * 0.99;
                    vec = new Vector2D.Double( new Point2D.Double( a, b ), new Point2D.Double( prefX, prefY ) );
                }
//            stepToTarget( vec );
                accelerateToTarget( vec, accelScale, frictionScale );
//            accelerateFromNeighbors();//very expensive
            }
        }

        private SearchResult searchForTarget() {
            return searchForMin();
        }

        private SearchResult searchForMin() {
            double bestValue = Double.POSITIVE_INFINITY;
            int windowSize = 8;
            Point bestPoint = null;
            for( int i = -windowSize / 2; i <= windowSize / 2; i++ ) {
                for( int j = -windowSize / 2; j <= windowSize / 2; j++ ) {
                    if( inBounds( homeX + i, homeY + j ) ) {
                        //                        if( bestPoint == null || (( lattice.getValue( homeX + i, homeY + j ) > bestValue ) && new Point( homeX + i, homeY + j ).distance( homeX, homeY ) < 5 ))
                        if( bestPoint == null || ( lattice.getValue( homeX + i, homeY + j ) < bestValue && new Point( homeX + i, homeY + j ).distance( homeX, homeY ) <= windowSize / 2 ) )
                        {
                            bestPoint = new Point( homeX + i, homeY + j );
                            bestValue = lattice.getValue( homeX + i, homeY + j );
                        }
                    }
                }
            }
            return new SearchResult( bestValue, bestPoint );
        }

        private SearchResult searchForMax() {
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
            return new SearchResult( bestValue, bestPoint );
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

//        private AbstractVector2D getForce( Particle particle ) {
//            return new Vector2D.Double();
//        }

        private void accelerateToTarget( Vector2D.Double vec, double accelScale, double frictionScale ) {
            if( vec.getMagnitude() >= 1.2 ) {
                vec.normalize();
                double finalAcceleration = acceleration * accelScale;
                vec.scale( finalAcceleration );

                velocity = velocity.add( vec );
                if( velocity.getX() > maxVelocity ) {
                    velocity.setX( maxVelocity );
                }
                if( velocity.getY() > maxVelocity ) {
                    velocity.setY( maxVelocity );
                }
                if( velocity.getX() < -maxVelocity ) {
                    velocity.setX( -maxVelocity );
                }
                if( velocity.getY() < -maxVelocity ) {
                    velocity.setY( -maxVelocity );
                }
                velocity.scale( friction * frictionScale );//friction
                Point2D dest = velocity.getDestination( new Point2D.Double( a, b ) );
                a = dest.getX();
                b = dest.getY();
                setOffset( a, b );
                //observe the barriers
                // .

//                Point pt = latticeScreenCoordinates.toLatticeCoordinates( a-PressureWaveGraphic.this.getOffset().getX(), b-PressureWaveGraphic.this.getOffset().getY() );
//                System.out.println( "pt = " + pt );
//                double pot = slitPotential.getPotential( pt.x, pt.y, 0 );
//                if( pot == 0 ) {

//                }
//                else{
//                    System.out.println( "PressureWaveGraphic$Particle.accelerateToTarget" );
//                }
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
