/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.util.Random;
import java.util.Vector;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:26:37 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class SparkNode extends PNode {
    private Point source;
    private Point sink;
    private double maxDTheta;
    private Random random;
    private double threshold;
    private double segLength;

    public SparkNode( Point source, Point sink, double maxDTheta, double threshold, double segLength ) {
        this.segLength = segLength;
        this.source = source;
        this.sink = sink;
        this.maxDTheta = maxDTheta;
        this.threshold = threshold;
        this.random = new Random();
        update();
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } );
        timer.start();
    }

    private void update() {
        removeAllChildren();
        Point[]path = newPath( 100 );
        GeneralPath generalPath = new GeneralPath();
        for( int i = 0; i < path.length; i++ ) {
            if( i == 0 ) {
                generalPath.moveTo( path[i].x, path[i].y );
            }
            else {
                generalPath.lineTo( path[i].x, path[i].y );
            }
        }
        PPath p1 = new PPath( generalPath );
        p1.setStroke( new BasicStroke( 4 ) );
        p1.setStrokePaint( Color.white );
        addChild( p1 );

        PPath p2 = new PPath( generalPath );
        p2.setStroke( new BasicStroke( 1 ) );
        p2.setStrokePaint( Color.blue );
        addChild( p2 );
    }

    public void setSink( Point p ) {
        this.sink = p;
    }

    public Point[] newPath( int maxPts ) {
        /**Aim vaguely the direction of the sink, with some leeway.*/
        Vector pts = new Vector();
        Point prev = source;
        pts.add( source );
        for( int i = 0; i < maxPts; i++ ) {
            if( prev.distance( sink ) < threshold ) {
                //edu.colorado.phet.common.util.Debug.traceln("Distance="+prev.distance(sink)+", less than thershold: "+thresh);
                pts.add( sink );
                break;
            }
            Point p = nextPoint( prev );
            if( p == null ) {
                break;
            }
            pts.add( p );
            prev = p;
        }
        //System.out.println("Generated path: "+pts);
        return (Point[])pts.toArray( new Point[0] );
    }

    public double getAngle( double x, double y ) {
        return Math.atan2( y, x );
    }

    public Point nextPoint( Point prev ) {
        Point diff = new Point( prev.x - sink.x, prev.y - sink.y );
        double theta = getAngle( diff.x, diff.y );
        double dTheta = ( random.nextDouble() - .5 ) * 2 * maxDTheta;
        double thetaNew = theta + dTheta;
        //edu.colorado.phet.common.util.Debug.traceln("Theta="+theta+", NewTheta="+thetaNew);

        Point newPt = new Point( (int)( prev.x - segLength * Math.cos( thetaNew ) ), (int)( prev.y - segLength * Math.sin( thetaNew ) ) );
        //	edu.colorado.phet.common.util.Debug.traceln("New point="+newPt);
        return newPt;
        //angle up or down.
    }


}
