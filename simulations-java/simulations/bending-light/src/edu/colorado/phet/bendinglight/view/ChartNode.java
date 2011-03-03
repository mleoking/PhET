// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ChartNode extends PNode {
    public ModelViewTransform transform;

    public ChartNode( Rectangle chartArea, ArrayList<Series> series ) {
        transform = ModelViewTransform.createRectangleMapping( new Rectangle2D.Double( 0, -1, 2, 2 ), chartArea );
        addChild( new PhetPPath( transform.modelToView( new Line2D.Double( 0, 0, 2, 0 ) ), new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] { 10, 5 }, 0 ), Color.lightGray ) );
        addVerticalLine( 0.5 );
        addVerticalLine( 1 );
        addVerticalLine( 1.5 );
        for ( Series s : series ) {
            addChild( new SeriesNode( s ) );
        }
    }

    private void addVerticalLine( double x ) {
        addChild( new PhetPPath( transform.modelToView( new Line2D.Double( x, -1, x, 1 ) ), new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] { 10, 5 }, 0 ), Color.lightGray ) );
    }

    public static class Series {
        public final Property<ArrayList<Option<ImmutableVector2D>>> path;
        private final Color color;

        public Series( Property<ArrayList<Option<ImmutableVector2D>>> path, Color color ) {
            this.path = path;
            this.color = color;
        }

        public Paint getColor() {
            return color;
        }

        public void addPoint( final double x, final double y ) {
            path.setValue( new ArrayList<Option<ImmutableVector2D>>( path.getValue() ) {{
                add( new Option.Some<ImmutableVector2D>( new ImmutableVector2D( x, y ) ) );
            }} );
        }

        public Shape toShape() {
            DoubleGeneralPath generalPath = new DoubleGeneralPath();
            boolean moved = false;
            for ( Option<ImmutableVector2D> value : path.getValue() ) {
                if ( value.isSome() ) {
                    if ( !moved ) {
                        generalPath.moveTo( value.get() );
                        moved = true;
                    }
                    else {
                        generalPath.lineTo( value.get() );
                    }
                }
            }
            return generalPath.getGeneralPath();
        }
    }

    private class SeriesNode extends PNode {
        public SeriesNode( final Series s ) {
            addChild( new PhetPPath( new BasicStroke( 2 ), s.getColor() ) {{
                s.path.addObserver( new SimpleObserver() {
                    public void update() {
                        setPathTo( transform.modelToView( s.toShape() ) );
                    }
                } );
            }} );
        }
    }
}
