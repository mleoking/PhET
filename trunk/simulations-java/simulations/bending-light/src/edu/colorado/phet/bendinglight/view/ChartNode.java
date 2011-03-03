// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleMapping;

/**
 * @author Sam Reid
 */
public class ChartNode extends PClip {
    public Property<ModelViewTransform> transform;

    public class GridLine extends PNode {
        public GridLine( final double x1, final double y1, final double x2, final double y2 ) {
            addChild( new PhetPPath( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] { 10, 5 }, 0 ), Color.lightGray ) {{
                transform.addObserver( new SimpleObserver() {
                    public void update() {
                        setPathTo( transform.getValue().modelToView( new Line2D.Double( x1, y1, x2, y2 ) ) );
                    }
                } );
            }} );
        }
    }

    public ChartNode( final Clock clock, final Rectangle chartArea, ArrayList<Series> series ) {
        setPathTo( chartArea );
        setStroke( null );
        transform = new Property<ModelViewTransform>( createRectangleMapping( new Rectangle2D.Double( 0, -1, 2, 2 ), chartArea ) );
        addChild( new GridLine( 0, 0, 200, 0 ) );//main axis        //TODO: we'll need to extend these lines
        for ( double x = 0.5; x <= 200; x += 0.5 ) {//TODO: extend past 200
            addVerticalLine( x );
        }
        for ( Series s : series ) {
            addChild( new SeriesNode( s ) );
        }
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                double timeWidth = 2;
                transform.setValue( createRectangleMapping( new Rectangle2D.Double( clock.getSimulationTime() - timeWidth, -1, timeWidth, 2 ), chartArea ) );
            }
        } );
    }

    private void addVerticalLine( double x ) {
        addChild( new GridLine( x, -1, x, 1 ) );
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
                        setPathTo( transform.getValue().modelToView( s.toShape() ) );
                    }
                } );
            }} );
        }
    }
}
