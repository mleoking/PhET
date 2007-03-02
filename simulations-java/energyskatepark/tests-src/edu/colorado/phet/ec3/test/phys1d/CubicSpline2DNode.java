package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:16:04 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */
public class CubicSpline2DNode extends PNode {
    private CubicSpline2D cubicSpline2D;
    private PhetPPath phetPPath;
    private PNode controlPointLayer = new PNode();
    private PNode topLayer;

    public CubicSpline2DNode( CubicSpline2D splineSurface ) {
        this.cubicSpline2D = splineSurface;
        phetPPath = new PhetPPath( new BasicStroke( 0.01f ), Color.blue );
        addChild( phetPPath );

        splineSurface.addListener( new CubicSpline2D.Listener() {
            public void trackChanged() {
                update();
            }
        } );
        addChild( controlPointLayer );
        topLayer = new PNode();
        addChild( topLayer );

        update();
    }

    private void update() {
        DoubleGeneralPath doubleGeneralPath = new DoubleGeneralPath( cubicSpline2D.evaluate( 0 ) );
        double ds = 0.01;
        for( double s = ds; s < 1.0; s += ds ) {
            doubleGeneralPath.lineTo( cubicSpline2D.evaluate( s ) );
        }
        doubleGeneralPath.lineTo( cubicSpline2D.evaluate( 1.0 ) );
        phetPPath.setPathTo( doubleGeneralPath.getGeneralPath() );
        while( controlPointLayer.getChildrenCount() < cubicSpline2D.getNumControlPoints() ) {
            addControlPointNode();
        }
        while( controlPointLayer.getChildrenCount() > cubicSpline2D.getNumControlPoints() ) {
            removeControlPointNode();
        }
        for( int i = 0; i < controlPointLayer.getChildrenCount(); i++ ) {
            ControlPointNode controlPointNode = (ControlPointNode)controlPointLayer.getChild( i );
            controlPointNode.setIndex( i );
        }
        topLayer.removeAllChildren();
        for( double x = 0; x <= cubicSpline2D.getMetricDelta( 0, 1 ); x += 0.33 ) {
            double alpha = cubicSpline2D.getFractionalDistance( 0, x );
            Point2D pt = cubicSpline2D.evaluate( alpha );
            Arrow arrow = new Arrow( pt, cubicSpline2D.getUnitNormalVector( alpha ).getScaledInstance( 0.1).getDestination( pt ), 0.03f, 0.03f, 0.01f );
            PhetPPath phetPPath = new PhetPPath( arrow.getShape(), Color.black );
            topLayer.addChild( phetPPath );
        }
    }

    private void removeControlPointNode() {
        controlPointLayer.removeChild( controlPointLayer.getChildrenCount() - 1 );
    }

    class ControlPointNode extends PNode {
        private int index;
        private PhetPPath controlPoint;

        public ControlPointNode( int index_ ) {
            this.index = index_;

            controlPoint = new PhetPPath( new Color( 255, 50, 50, 128 ), new BasicStroke( 0.01f ), Color.black );
            double w = 0.10;
            controlPoint.setPathTo( new Ellipse2D.Double( -w / 2, -w / 2, w, w ) );

            addChild( controlPoint );

            controlPoint.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    cubicSpline2D.translateControlPoint( index, event.getDeltaRelativeTo( CubicSpline2DNode.this ).width,
                                                         event.getDeltaRelativeTo( CubicSpline2DNode.this ).height );
                }
            } );

            addChild( controlPoint );
            update();
        }

        public void update() {
            controlPoint.setOffset( cubicSpline2D.getControlPoints()[index] );
        }

        public void setIndex( int i ) {
            this.index = i;
            update();
        }
    }

    private void addControlPointNode() {
        controlPointLayer.addChild( new ControlPointNode( 0 ) );
    }
}
