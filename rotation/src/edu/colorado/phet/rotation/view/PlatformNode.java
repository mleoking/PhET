package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:44:08 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class PlatformNode extends PNode {
    RingNode[] ringNode = {};
    private double ringRadius;
    private PNode contentNode;
    private double scale = 1.0 / 100;
    private double angle = 0.0;

    public PlatformNode() {
        ringRadius = 2.0;
        contentNode = new PNode();
        addRingNode( 2.0, Color.green );
        addRingNode( 1.5, Color.yellow );
        addRingNode( 1.0, Color.magenta );
        addRingNode( 0.5, Color.white );
        addRingNode( 0.01, Color.white );

        PhetPPath verticalCrossHair = new PhetPPath( new Line2D.Double( ringRadius, 0, ringRadius, ringRadius * 2 ), new BasicStroke( (float)( 2 * scale ) ), Color.black );
        contentNode.addChild( verticalCrossHair );

        PhetPPath horizontalCrossHair = new PhetPPath( new Line2D.Double( 0, ringRadius, ringRadius * 2, ringRadius ), new BasicStroke( (float)( 2 * scale ) ), Color.black );
        contentNode.addChild( horizontalCrossHair );

        double handleWidth = 10 * scale;
        double handleHeight = 10 * scale;
        PhetPPath handleNode = new PhetPPath( new Rectangle2D.Double( ringRadius * 2, ringRadius - handleHeight / 2, handleWidth, handleHeight ), Color.blue, new BasicStroke( (float)( 1 * scale ) ), Color.black );
        contentNode.addChild( handleNode );

        addChild( contentNode );

        contentNode.scale( 100 );
        addInputEventListener( new PBasicInputEventHandler() {
            double initAngle;
            public Point2D initLoc;

            public void mousePressed( PInputEvent event ) {
                initAngle = angle;
                initLoc = event.getPositionRelativeTo( PlatformNode.this );
            }

            public void mouseReleased( PInputEvent event ) {
            }

            public void mouseDragged( PInputEvent event ) {
                Point2D loc = event.getPositionRelativeTo( PlatformNode.this );
                Point2D.Double center = new Point2D.Double( ringRadius / scale, ringRadius / scale );
                Vector2D.Double a = new Vector2D.Double( center, initLoc );
                Vector2D.Double b = new Vector2D.Double( center, loc );
                double angleDiff = b.getAngle() - a.getAngle();
//                System.out.println( "a=" + a + ", b=" + b + ", center=" + center + ", angleDiff = " + angleDiff );
                System.out.println( "angleDiff=" + angleDiff );

                setAngle( initAngle + angleDiff );
                notifyListeners();
            }
        } );
        addInputEventListener( new CursorHandler() );
    }

    private void addRingNode( double radius, Color color ) {
        RingNode ringNode = new RingNode( ringRadius, ringRadius, radius, color );
        contentNode.addChild( ringNode );
    }

    public void setAngle( double angle ) {
        if( this.angle != angle ) {
            this.angle = angle;
            contentNode.setRotation( 0 );
            contentNode.setOffset( 0, 0 );
            contentNode.rotateAboutPoint( angle, ringRadius, ringRadius );
        }
    }

    public double getAngle() {
        return angle;
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void angleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.angleChanged();
        }
    }

    class RingNode extends PNode {
        public RingNode( double x, double y, double radius, Color color ) {
            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( (float)( 1 * scale ) ), Color.black );
//            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( 2.0f / 100.0f ,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,1.0f,new float[]{10.0f/100.0f,10.0f/100.0f},0.0f), Color.black );
//            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( 2.0f / 100.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{30.0f / 100.0f, 30.0f / 100.0f}, 0.0f ), Color.black );
            addChild( path );
        }
    }
}
