// Copyright 2002-2011, University of Colorado

/**
 * Class: StarGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Nov 26, 2003
 */
package edu.colorado.phet.greenhouse.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import edu.colorado.phet.greenhouse.model.Disk;
import edu.colorado.phet.greenhouse.model.Star;

public class StarGraphic extends DiskGraphic {
    private Star star;
    private Rectangle2D.Double modelBounds;
    private static Paint paint = Color.YELLOW;
    private java.util.List sunbeams = Collections.synchronizedList( new LinkedList() );

    public StarGraphic( Star star, Rectangle2D.Double modelBounds ) {
        super( new Disk( star.getLocation(), star.getRadius() ), paint );
        this.star = star;
        this.modelBounds = modelBounds;
//        sunbeamGenerator.start();
//        sunbeamAnimator.start();
        Arrays.fill( dR, 1.3 );
    }

    public void stopAnimation() {
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

}
