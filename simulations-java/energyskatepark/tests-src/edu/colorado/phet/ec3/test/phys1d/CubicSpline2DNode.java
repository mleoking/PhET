package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:16:04 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */
class CubicSpline2DNode extends PNode {
    private CubicSpline2D cubicSpline2D;
    private PhetPPath phetPPath;
    private PNode controlPointLayer = new PNode();

    public CubicSpline2DNode( CubicSpline2D splineSurface ) {
        this.cubicSpline2D = splineSurface;
        phetPPath = new PhetPPath( new BasicStroke( 1 ), Color.blue );
        addChild( phetPPath );
        update();
        splineSurface.addListener( new CubicSpline2D.Listener() {
            public void trackChanged() {
                update();
            }
        } );
        addChild( controlPointLayer );
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
    }

    private void removeControlPointNode() {
        controlPointLayer.removeChild( controlPointLayer.getChildrenCount() - 1 );
    }

    class ControlPointNode extends PNode {
        private int index;
        private PhetPPath controlPoint;

        public ControlPointNode( int index_ ) {
            this.index = index_;

            controlPoint = new PhetPPath( new Color( 255, 50, 50, 128 ), new BasicStroke( 1 ), Color.black );
            double w = 10;
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
