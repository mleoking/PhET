package edu.colorado.phet.piccolo.help;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PAffineTransform;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 2, 2006
 * Time: 1:55:11 PM
 * Copyright (c) Jun 2, 2006 by Sam Reid
 */

public class MotionHelpBalloon extends HelpBalloon {
    private boolean started = false;
    private ActivitySource activitySource;
    private static final int DEFAULT_DURATION = 3000;
    private PCanvas canvas;

    public MotionHelpBalloon( PCanvas canvas, String s ) {
        super( canvas, s, HelpBalloon.BOTTOM_CENTER, 100, 0 );
        setBalloonVisible( false );
        setArrowVisible( false );
        setEnabled( true );
        this.canvas = canvas;
    }

    public void animateTo( double x, double y ) {
        activitySource = new AnimateToXY( this, x, y, DEFAULT_DURATION );
    }

    public static interface ActivitySource {
        PActivity createActivity();
    }

    public static class AnimateToXY implements ActivitySource {
        private PNode node;
        private double x;
        private double y;
        private long duration;

        public AnimateToXY( PNode node, double x, double y, long duration ) {
            this.node = node;
            this.x = x;
            this.y = y;
            this.duration = duration;
        }

        public PActivity createActivity() {
            PActivity activity = node.animateToPositionScaleRotation( x, y, 1, 0, duration );
            unschedule( node, activity );
            return activity;
        }
    }

    private static void unschedule( PNode source, PActivity activity ) {
        PRoot r = source.getRoot();
        if( r != null ) {
            PActivityScheduler sched = r.getActivityScheduler();
            if( sched != null ) {
                sched.removeActivity( activity );
            }
        }
    }

    public static class AnimateToNode implements ActivitySource {
        private MotionHelpBalloon motionHelpBalloon;
        private PNode dst;

        public AnimateToNode( MotionHelpBalloon motionHelpBalloon, PNode dst ) {
            this.motionHelpBalloon = motionHelpBalloon;
            this.dst = dst;
        }

        public PActivity createActivity() {
            Point2D loc = motionHelpBalloon.mapLocation( dst, motionHelpBalloon.canvas );
            final PTransformActivity activity = motionHelpBalloon.animateToPositionScaleRotation( loc.getX(), loc.getY(), 1, 0, DEFAULT_DURATION );
            dst.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    PAffineTransform t = motionHelpBalloon.getTransform();
                    Point2D loc = motionHelpBalloon.mapLocation( dst, motionHelpBalloon.canvas );
                    t.setOffset( loc.getX(), loc.getY() );
                    t.setScale( 1.0 );
                    t.setRotation( 0.0 );
                    double[]m = new double[6];
                    t.getMatrix( m );
                    activity.setDestinationTransform( m );
                }
            } );
            return activity;
        }
    }

    public void animateTo( PNode node ) {
        this.activitySource = new AnimateToNode( this, node );
    }

    Timer timer;

    /**
     * Starts the animation.
     */
    public void start() {
        if( !started && activitySource != null ) {
            if( getRoot() != null ) {
                getRoot().addActivity( activitySource.createActivity() );
            }
            else if( timer == null ) {
                timer = new Timer( 30, new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        if( getRoot() != null ) {
                            getRoot().addActivity( activitySource.createActivity() );
                            timer.stop();
                        }
                        else {
                            System.out.println( "Waiting for proot." );
                        }
                    }
                } );
                timer.start();
            }
        }
    }

    private void testStartActivity() {
        if( getRoot() != null && activitySource != null && !started ) {
            PActivity activity = activitySource.createActivity();
            getRoot().addActivity( activity );
            started = true;
            System.out.println( "Started activity." );
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        setPickable( visible );
        setChildrenPickable( visible );
    }
}
