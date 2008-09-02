/**
 * Class: StarGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Nov 26, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import edu.colorado.phet.greenhouse.coreadditions.Disk;

public class StarGraphic extends DiskGraphic {
    private Star star;
    private Rectangle2D.Double modelBounds;
    private static Paint paint = Color.YELLOW;
    private java.util.List sunbeams = Collections.synchronizedList( new LinkedList() );
    private long timeBetweenSunbeams = 30;
    private SunbeamGenerator sunbeamGenerator;
    private SunbeamAnimator sunbeamAnimator;

    public StarGraphic( Star star, Rectangle2D.Double modelBounds ) {
        super( new Disk( star.getLocation(), star.getRadius() ), paint );
        this.star = star;
        this.modelBounds = modelBounds;
        sunbeamGenerator = new SunbeamGenerator( sunbeams, timeBetweenSunbeams );
        sunbeamGenerator.start();
        sunbeamAnimator = new SunbeamAnimator( sunbeams );
        sunbeamAnimator.start();
        Arrays.fill( dR, 1.3 );
    }

    public void stopAnimation() {
        sunbeamGenerator.setRunning( false );
        sunbeamAnimator.setRunning( false );
    }

    public void removeDistantSunbeams() {
//        System.out.println( "Sunbeam count="+sunbeams.size() );
        for ( int i = 0; i < sunbeams.size(); i++ ) {
            Sunbeam sunbeam = (Sunbeam) sunbeams.get( i );
            if ( !modelBounds.contains( sunbeam.getX0(), sunbeam.getY0() ) ) {
                sunbeams.remove( sunbeam );
                i--;
            }
        }
    }

    public void paint( Graphics2D g2 ) {
        super.paint( g2 );

        removeDistantSunbeams();
        for ( int i = 0; i < sunbeams.size(); i++ ) {
            Sunbeam sunbeam = (Sunbeam) sunbeams.get( i );
            if ( modelBounds.contains( sunbeam.getX0(), sunbeam.getY0() ) ) {
                sunbeam.paint( g2 );
            }
        }

        // draw flares
        double angleSubtended = Math.PI * 2 / numFlares;
//        int flareIdx = 0;
        double alpha = 0;
        for ( int flareIdx = 0; flareIdx < numFlares; flareIdx++ ) {
            alpha += angleSubtended;
//        for( double alpha = 0; alpha <= Math.PI * 2; alpha += angleSubtended ) {
            Polygon flare = new Polygon();
            flare.addPoint( (int) ( star.getLocation().getX() + star.getRadius() * Math.cos( alpha + dAlpha ) ),
                            (int) ( star.getLocation().getY() + star.getRadius() * Math.sin( alpha + dAlpha ) ) );
            flare.addPoint( (int) ( star.getLocation().getX() + ( star.getRadius() * dR[flareIdx] ) * Math.cos( alpha + dAlpha + angleSubtended / 2 ) ),
                            (int) ( star.getLocation().getY() + ( star.getRadius() * dR[flareIdx] ) * Math.sin( alpha + dAlpha + angleSubtended / 2 ) ) );
            flare.addPoint( (int) ( star.getLocation().getX() + star.getRadius() * Math.cos( alpha + dAlpha + angleSubtended ) ),
                            (int) ( star.getLocation().getY() + star.getRadius() * Math.sin( alpha + dAlpha + angleSubtended ) ) );
//            dAlpha += Math.PI / 150;
            g2.setColor( Color.YELLOW );
            g2.draw( flare );
            g2.fill( flare );
        }
    }

    double dAlpha = 0;
    int numFlares = 25;
    double[] dR = new double[numFlares];

    class Sunbeam {
        double theta;
        double x0;
        double y0;
        double x1;
        double y1;
        double timeCreated = System.currentTimeMillis();

        public Sunbeam( double theta ) {
            this.theta = theta;
        }

        synchronized private void computeLine( double t ) {
            double advance = ( ( t - timeCreated ) / 100 ) * star.getRadius();
            if ( t - timeCreated > 0 ) {
                x0 = star.getLocation().getX() + ( advance ) * Math.cos( theta );
                y0 = star.getLocation().getY() + ( advance ) * Math.sin( theta );
                x1 = star.getLocation().getX() + ( star.getRadius() + advance ) * Math.cos( theta );
                y1 = star.getLocation().getY() + ( star.getRadius() + advance ) * Math.sin( theta );
            }
        }

        synchronized void paint( Graphics2D g2 ) {
            GeneralPath sunbeam = new GeneralPath();
            sunbeam.moveTo( (float) x0, (float) y0 );
            sunbeam.lineTo( (float) x1, (float) y1 );
            g2.draw( sunbeam );
        }

        public double getX0() {
            return x0;
        }

        public double getY0() {
            return y0;
        }
    }

    class SunbeamGenerator extends Thread {
        java.util.List sunbeams;
        private long timeBetweenSunbeams;
        double t = System.currentTimeMillis();
        private boolean isRunning;
        int cnt;

        SunbeamGenerator( java.util.List sunbeams, long timeBetweenSunbeams ) {
            this.sunbeams = sunbeams;
            this.timeBetweenSunbeams = timeBetweenSunbeams;
        }

        public void run() {
            isRunning = true;
            while ( isRunning ) {
                synchronized( this ) {
                    try {
                        Thread.sleep( timeBetweenSunbeams );
                        removeDistantSunbeams();
                        for ( int i = 0; i < 2; i++ ) {
                            double theta = Math.random() * Math.PI * 2;
                            sunbeams.add( new Sunbeam( theta ) );
                        }
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public synchronized void setRunning( boolean isRunning ) {
            this.isRunning = isRunning;
        }
    }


    class SunbeamAnimator extends Thread {
        java.util.List sunbeams;
        double t = System.currentTimeMillis();
        double dt = 30;
        boolean isRunning;
        Random randomGenerator = new Random();

        SunbeamAnimator( java.util.List sunbeams ) {
            this.sunbeams = sunbeams;
        }

        int cnt = 0;

        public void run() {
            isRunning = true;
            while ( isRunning ) {
                try {
                    Thread.sleep( (long) dt );
                    t += dt;
                    removeDistantSunbeams();
                    for ( int i = 0; i < sunbeams.size(); i++ ) {
                        Sunbeam sunbeam = (Sunbeam) sunbeams.get( i );
                        sunbeam.computeLine( t );
                    }
                    cnt++;
                    for ( int j = 0; cnt % 5 == 0 && j < numFlares; j++ ) {
//                        dR[j] = 1.3 * ( randomGenerator.nextGaussian() * 0.1 + 1.0 );
                        dR[j] = 1.3 * ( Math.random() * 0.2 + 1.0 );
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }

        public void setRunning( boolean isRunning ) {
            this.isRunning = isRunning;
        }
    }
}
