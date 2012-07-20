// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.spline.ParametricFunction2D;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:16:04 AM
 */
public class ParametricFunction2DNode extends PNode {
    private final ParametricFunction2D parametricFunction2D;
    private final PhetPPath phetPPath;

    private final PNode topLayer = new PNode();
    private boolean normalsVisible = false;

    private boolean curvatureVisible = false;
    private final PNode curvatureLayer = new PNode();
    private final PNode topOffsetTrack = new PNode();
    private final PNode bottomOffsetTrack = new PNode();
    private boolean topOffsetTrackVisible = false;
    private double splineOffset = HUMAN_CENTER_OF_MASS_HEIGHT;
    private boolean showBottomOffsetSpline = false;


    public ParametricFunction2DNode( ParametricFunction2D splineSurface ) {
        this.parametricFunction2D = splineSurface;
        phetPPath = new PhetPPath( new BasicStroke( 0.01f ), Color.blue );
        addChild( phetPPath );


        addChild( topLayer );
        addChild( curvatureLayer );
        addChild( topOffsetTrack );
        addChild( bottomOffsetTrack );

        update();
    }


    public boolean isNormalsVisible() {
        return normalsVisible;
    }

    protected void update() {
        DoubleGeneralPath doubleGeneralPath = new DoubleGeneralPath( parametricFunction2D.evaluate( 0 ) );
        double ds = 0.01;
        for ( double s = ds; s < 1.0; s += ds ) {
            doubleGeneralPath.lineTo( parametricFunction2D.evaluate( s ) );
        }
        doubleGeneralPath.lineTo( parametricFunction2D.evaluate( 1.0 ) );
        phetPPath.setPathTo( doubleGeneralPath.getGeneralPath() );
        updateControlPoints();
        topLayer.removeAllChildren();
        if ( normalsVisible ) {
            for ( double x = 0; x <= parametricFunction2D.getMetricDelta( 0, 1 ); x += 0.1 ) {
                double alpha = parametricFunction2D.getFractionalDistance( 0, x );
                SerializablePoint2D pt = parametricFunction2D.evaluate( alpha );
                Arrow arrow = new Arrow( pt, parametricFunction2D.getUnitNormalVector( alpha ).getScaledInstance( 0.1 ).getDestination( pt ), 0.03f, 0.03f, 0.01f );
                PhetPPath phetPPath = new PhetPPath( arrow.getShape(), Color.black );
                topLayer.addChild( phetPPath );
            }
        }

        curvatureLayer.removeAllChildren();
        if ( curvatureVisible ) {
            for ( double x = 0; x <= parametricFunction2D.getMetricDelta( 0, 1 ); x += 0.1 ) {
                double alpha = parametricFunction2D.getFractionalDistance( 0, x );
                SerializablePoint2D at = parametricFunction2D.evaluate( alpha );
                Vector2D pt = parametricFunction2D.getCurvatureDirection( alpha );
                Arrow arrow = new Arrow( at, pt.getScaledInstance( 0.1 ).getDestination( at ), 0.03f, 0.03f, 0.01f );
                PhetPPath phetPPath = new PhetPPath( arrow.getShape(), Color.blue );
                curvatureLayer.addChild( phetPPath );
            }
        }
        topOffsetTrack.removeAllChildren();

        if ( topOffsetTrackVisible ) {
            double alpha = 0;
            double dAlpha = 0.005;
            DoubleGeneralPath path = new DoubleGeneralPath( parametricFunction2D.getOffsetPoint( alpha, splineOffset, true ) );
            for ( alpha = alpha + dAlpha; alpha <= 1.0; alpha += dAlpha ) {
                path.lineTo( parametricFunction2D.getOffsetPoint( alpha, splineOffset, true ) );
            }
            topOffsetTrack.addChild( new PhetPPath( path.getGeneralPath(), new BasicStroke( 0.01f ), Color.green ) );
        }
        bottomOffsetTrack.removeAllChildren();
        if ( showBottomOffsetSpline ) {
            double alpha = 0;
            double dAlpha = 0.005;
            DoubleGeneralPath path = new DoubleGeneralPath( parametricFunction2D.getOffsetPoint( alpha, splineOffset, false ) );
            for ( alpha = alpha + dAlpha; alpha <= 1.0; alpha += dAlpha ) {
                path.lineTo( parametricFunction2D.getOffsetPoint( alpha, splineOffset, false ) );
            }
            bottomOffsetTrack.addChild( new PhetPPath( path.getGeneralPath(), new BasicStroke( 0.01f ), Color.magenta ) );
        }
    }

    protected void updateControlPoints() {
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
        this.showBottomOffsetSpline = showBottomOffsetSpline;
        update();
    }


}
