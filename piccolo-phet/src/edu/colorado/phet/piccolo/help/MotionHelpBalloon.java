package edu.colorado.phet.piccolo.help;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PPaintContext;

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

    public MotionHelpBalloon( PCanvas helpPane, String s ) {
        super( helpPane, s, HelpBalloon.BOTTOM_CENTER, 100, 0 );
        setBalloonVisible( false );
        setArrowLength( 0 );
        setEnabled( true );
    }

    public void animateTo( double x, double y ) {
        setActivity( animateToPositionScaleRotation( x, y, 1, 0, 5000 ) );
        testStartActivity();
    }

    private void setActivity( PTransformActivity pTransformActivity ) {
        if( activity != null ) {
            activity.terminate();
        }
        this.activity = pTransformActivity;
        testStartActivity();
    }

    public void animateTo( PNode node, PhetPCanvas canvas ) {
        Point2D loc = super.mapLocation( node, canvas );
        activity = animateToPositionScaleRotation( loc.getX(), loc.getY(), 1, 0, 5000 );
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
