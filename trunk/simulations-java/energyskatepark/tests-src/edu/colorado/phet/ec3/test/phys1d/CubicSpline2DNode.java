package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
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
    private PNode topLayer = new PNode();
    private boolean normalsVisible = false;
    private CubicSpline2D.Listener listener = new CubicSpline2D.Listener() {
        public void trackChanged() {
            update();
        }
    };
    private boolean curvatureVisible = false;
    private PNode curvatureLayer = new PNode();
    private PNode topOffsetTrack = new PNode();
    private PNode bottomOffsetTrack=new PNode();
    private boolean topOffsetTrackVisible = true;
    private double splineOffset = HUMAN_CENTER_OF_MASS_HEIGHT;
    private boolean showBottomOffsetSpline=false;
    

    public CubicSpline2DNode( CubicSpline2D splineSurface ) {
        this.cubicSpline2D = splineSurface;
        phetPPath = new PhetPPath( new BasicStroke( 0.01f ), Color.blue );
        addChild( phetPPath );

        splineSurface.addListener( listener );
        addChild( controlPointLayer );
        addChild( topLayer );
        addChild( curvatureLayer );
        addChild( topOffsetTrack );
        addChild( bottomOffsetTrack);

        update();
    }

    public void setCubicSpline2D( CubicSpline2D cubicSpline2D ) {
        if( this.cubicSpline2D != null ) {
            this.cubicSpline2D.removeListener( listener );
        }
        this.cubicSpline2D = cubicSpline2D;
        cubicSpline2D.addListener( listener );
        update();
    }

    public boolean isNormalsVisible() {
        return normalsVisible;
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
        if( normalsVisible ) {
            for( double x = 0; x <= cubicSpline2D.getMetricDelta( 0, 1 ); x += 0.1 ) {
                double alpha = cubicSpline2D.getFractionalDistance( 0, x );
                Point2D pt = cubicSpline2D.evaluate( alpha );
                Arrow arrow = new Arrow( pt, cubicSpline2D.getUnitNormalVector( alpha ).getScaledInstance( 0.1 ).getDestination( pt ), 0.03f, 0.03f, 0.01f );
                PhetPPath phetPPath = new PhetPPath( arrow.getShape(), Color.black );
                topLayer.addChild( phetPPath );
            }
        }

        curvatureLayer.removeAllChildren();
        if( curvatureVisible ) {
            for( double x = 0; x <= cubicSpline2D.getMetricDelta( 0, 1 ); x += 0.1 ) {
                double alpha = cubicSpline2D.getFractionalDistance( 0, x );
                Point2D at = cubicSpline2D.evaluate( alpha );
                AbstractVector2D pt = cubicSpline2D.getCurvatureDirection( alpha );
                Arrow arrow = new Arrow( at, pt.getScaledInstance( 0.1 ).getDestination( at ), 0.03f, 0.03f, 0.01f );
                PhetPPath phetPPath = new PhetPPath( arrow.getShape(), Color.blue );
                curvatureLayer.addChild( phetPPath );
            }
        }
        topOffsetTrack.removeAllChildren();

        if( topOffsetTrackVisible ) {
            double alpha = 0;
            double dAlpha = 0.005;
            DoubleGeneralPath path = new DoubleGeneralPath( cubicSpline2D.getOffsetPoint( alpha, splineOffset, true ) );
            for( alpha = alpha + dAlpha; alpha <= 1.0; alpha += dAlpha ) {
                path.lineTo( cubicSpline2D.getOffsetPoint( alpha, splineOffset, true ) );
            }
            topOffsetTrack.addChild( new PhetPPath( path.getGeneralPath(), new BasicStroke( 0.01f ), Color.green ) );
        }
        bottomOffsetTrack.removeAllChildren();
        if (showBottomOffsetSpline){
            double alpha = 0;
            double dAlpha = 0.005;
            DoubleGeneralPath path = new DoubleGeneralPath( cubicSpline2D.getOffsetPoint( alpha, splineOffset, false ) );
            for( alpha = alpha + dAlpha; alpha <= 1.0; alpha += dAlpha ) {
                path.lineTo( cubicSpline2D.getOffsetPoint( alpha, splineOffset, false ) );
            }
            bottomOffsetTrack.addChild( new PhetPPath( path.getGeneralPath(), new BasicStroke( 0.01f ), Color.magenta ) );
        }
    }

    //typical height of a human =1.7 meters
    //	1.7 meters = 5.57742782 feet
    //    double humanHeight = 1.7;
    //    double offset = humanHeight * 0.56;
    //http://hypertextbook.com/facts/2006/centerofmass.shtml
    public static final double HUMAN_HEIGHT = 1.7;
    public static final double HUMAN_CENTER_OF_MASS_HEIGHT = HUMAN_HEIGHT * 0.56;

    public void setNormalsVisible( boolean normalsVisible ) {
        this.normalsVisible = normalsVisible;
        update();
    }

    private void removeControlPointNode() {
        controlPointLayer.removeChild( controlPointLayer.getChildrenCount() - 1 );
    }

    public boolean isCurvatureVisible() {
        return curvatureVisible;
    }

    public void setCurvatureVisible( boolean selected ) {
        this.curvatureVisible = selected;
        update();
    }

    public void setShowTopOffsetSpline( boolean showTopOffsetSpline ) {
        this.topOffsetTrackVisible = showTopOffsetSpline;
        update();
    }

    public void setOffsetSplineDistance( double offsetDistance ) {
        this.splineOffset = offsetDistance;
        update();
    }

    public void setShowBottomOffsetSpline( boolean showBottomOffsetSpline ) {
        this.showBottomOffsetSpline=showBottomOffsetSpline;
        update();
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
        update();
    }
}
