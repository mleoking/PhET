// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class LightRayNode extends PNode {
    public final PhetPPath ppath;
    public Shape shape;
    private Shape ss;
    public Line2D.Double myline;
    public Point2D mystart;
    public Point2D myend;
    private final LightRay lightRay;

    public LightRayNode( final ModelViewTransform transform, final LightRay lightRay ) {
        this.lightRay = lightRay;
        float powerFraction = (float) lightRay.getPowerFraction();
        Color color = lightRay.getColor();
        ppath = new PhetPPath( new BasicStroke( 4 ), new Color( color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float) Math.sqrt( powerFraction ) ) ) {{
            lightRay.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D.Double start = lightRay.tip.getValue().toPoint2D();
                    final Point2D.Double finish = lightRay.tail.getValue().toPoint2D();
                    mystart = transform.modelToView( start );
                    myend = transform.modelToView( finish );
                    myline = new Line2D.Double( start, finish );
                    shape = transform.modelToView( myline );
                    ss = getStroke().createStrokedShape( shape );
                    setPathTo( shape );
                }
            } );
        }};
        addChild( ppath );
        setPickable( false );
        setChildrenPickable( false );
    }

    public boolean shapeContains( int x, int y ) {
        return ss.contains( x, y );
    }

    public Line2D.Double getLine() {
        return new Line2D.Double( mystart, myend );
    }

    public Color getColor() {
        return lightRay.getColor();
    }
}
