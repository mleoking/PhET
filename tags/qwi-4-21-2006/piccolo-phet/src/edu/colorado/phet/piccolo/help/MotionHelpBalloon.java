package edu.colorado.phet.piccolo.help;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 2, 2006
 * Time: 1:55:11 PM
 * Copyright (c) Jun 2, 2006 by Sam Reid
 */

public class MotionHelpBalloon extends HelpBalloon {
    private boolean started = false;
    private PActivity activity;
    private final int DEFAULT_DURATION = 3000;

    public MotionHelpBalloon( PCanvas helpPane, String s ) {
        super( helpPane, s, HelpBalloon.BOTTOM_CENTER, 100, 0 );
        setBalloonVisible( false );
        setArrowVisible( false );
        setEnabled( true );
    }

    public void animateTo( double x, double y ) {
        setActivity( animateToPositionScaleRotation( x, y, 1, 0, DEFAULT_DURATION ) );
        testStartActivity();
    }

    private void setActivity( PActivity activity ) {
        if( this.activity != null ) {
            this.activity.terminate();
        }
        this.activity = activity;
        testStartActivity();
    }

    public void animateTo( final PNode node, final PhetPCanvas canvas ) {
        Point2D loc = super.mapLocation( node, canvas );
        final Runnable updateActivity = new Runnable() {
            public void run() {
                Point2D loc = mapLocation( node, canvas );
                PActivity a2 = animateToPositionScaleRotation( loc.getX(), loc.getY(), 1, 0, DEFAULT_DURATION );
                setActivity( a2 );
                started = false;
                testStartActivity();
            }
        };
        activity = animateToPositionScaleRotation( loc.getX(), loc.getY(), 1, 0, DEFAULT_DURATION );
//        final PropertyChangeListener plc = new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                updateActivity.run();
//            }
//        };
//        node.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, plc );
//        node.addPropertyChangeListener( PROPERTY_BOUNDS, plc );
        canvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateActivity.run();
            }
        } );
        testStartActivity();
    }

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        testStartActivity();
    }

    private void testStartActivity() {
        if( getRoot() != null && activity != null && !started ) {
            getRoot().addActivity( activity );
            started = true;
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if( !visible && activity != null ) {
            activity.terminate();
        }
    }
}
