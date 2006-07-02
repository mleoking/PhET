/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Vector;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:26:37 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class SparkNode extends PNode {
    private Point2D source;
    private Point2D sink;
    private double maxDTheta;
    private Random random;
    private double threshold;
    private ArmNode armNode;
    private DoorknobNode doorknobNode;
    private double segLength;

    public SparkNode( ArmNode armNode, DoorknobNode doorknobNode, double maxDTheta, double threshold, double segLength ) {
        this.armNode = armNode;
        this.doorknobNode = doorknobNode;
        this.segLength = segLength;
        this.maxDTheta = maxDTheta;
        this.threshold = threshold;
        this.random = new Random();
        this.sink = doorknobNode.getGlobalKnobPoint();
        updateSource();
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } );
        timer.start();
        armNode.addListener( new LimbNode.Listener() {
            public void limbRotated() {
                updateSource();
            }
        } );
        updateSource();

    }

    private void updateSource() {
        setSource( armNode.getGlobalFingertipPoint() );
    }

    private void setSource( Point2D source ) {
        this.source = source;
        update();
    }

    public void setVisible( boolean isVisible ) {
        boolean origVisible = getVisible();
        super.setVisible( isVisible );
        if( origVisible != isVisible ) {
            update();
        }
    }

    private void update() {
        if( getVisible() ) {
            removeAllChildren();
            Point2D[]path = newPath( 100 );
            DoubleGeneralPath generalPath = new DoubleGeneralPath();
            for( int i = 0; i < path.length; i++ ) {
                if( i == 0 ) {
                    generalPath.moveTo( path[i].getX(), path[i].getY() );
                }
                else {
                    generalPath.lineTo( path[i].getX(), path[i].getY() );
                }
            }
            PPath p1 = new PPath( generalPath.getGeneralPath() );
            p1.setStroke( new BasicStroke( 4 ) );
            p1.setStrokePaint( Color.white );
            addChild( p1 );

            PPath p2 = new PPath( generalPath.getGeneralPath() );
            p2.setStroke( new BasicStroke( 1 ) );
            p2.setStrokePaint( Color.blue );
            addChild( p2 );
        }
    }

    public void setSink( Point p ) {
        this.sink = p;
    }

    public Point2D[] newPath( int maxPts ) {
        /**Aim vaguely the direction of the sink, with some leeway.*/
        Vector pts = new Vector();
        Point2D prev = source;
        pts.add( source );
        for( int i = 0; i < maxPts; i++ ) {
            if( prev.distance( sink ) < threshold ) {
                //edu.colorado.phet.common.util.Debug.traceln("Distance="+prev.distance(sink)+", less than thershold: "+thresh);
                pts.add( sink );
                break;
            }
            Point2D p = nextPoint( prev );
            if( p == null ) {
                break;
            }
            pts.add( p );
            prev = p;
        }
        //System.out.println("Generated path: "+pts);
        return (Point2D[])pts.toArray( new Point2D[0] );
    }

    public double getAngle( double x, double y ) {
        return Math.atan2( y, x );
    }

    public Point2D nextPoint( Point2D prev ) {
        Point2D diff = new Point2D.Double( prev.getX() - sink.getX(), prev.getY() - sink.getY() );
        double theta = getAngle( diff.getX(), diff.getY() );
        double dTheta = ( random.nextDouble() - .5 ) * 2 * maxDTheta;
        double thetaNew = theta + dTheta;
        //edu.colorado.phet.common.util.Debug.traceln("Theta="+theta+", NewTheta="+thetaNew);

        //	edu.colorado.phet.common.util.Debug.traceln("New point="+newPt);
        return (Point2D)new Point2D.Double( prev.getX() - segLength * Math.cos( thetaNew ), prev.getY() - segLength * Math.sin( thetaNew ) );
        //angle up or down.
    }


}
