// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.MAX_DT;
import static edu.colorado.phet.bendinglight.model.BendingLightModel.TIME_SPEEDUP_SCALE;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleMapping;

/**
 * Node that shows the chart in the "more tools" tab's intensity sensor.
 *
 * @author Sam Reid
 */
public class ChartNode extends PClip {
    public Property<ModelViewTransform> transform;

    public ChartNode( final Clock clock, final Rectangle chartArea, ArrayList<Series> series ) {
        setPathTo( chartArea );
        setStroke( null );
        final double timeWidth = 100 * MAX_DT / TIME_SPEEDUP_SCALE;
        transform = new Property<ModelViewTransform>( createRectangleMapping( new Rectangle2D.Double( 0, -1, timeWidth, 2 ), chartArea ) );
        addChild( new GridLine( -timeWidth, 0, timeWidth * 50, 0 ) );//main axis        //TODO: we'll need to extend these lines, will need to do modulo or equivalent
        for ( double x = 0.5; x <= 200; x += 0.5 ) {//TODO: extend past 200
            addVerticalLine( x );
        }
        for ( Series s : series ) {
            addChild( new SeriesNode( s ) );
        }
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                transform.setValue( createRectangleMapping( new Rectangle2D.Double( clock.getSimulationTime() - timeWidth, -1, timeWidth, 2 ), chartArea ) );
            }
        } );
    }

    private void addVerticalLine( double x ) {
        addChild( new GridLine( x, -1, x, 1 ) );
    }

    public static class Series {
        public final Property<ArrayList<Option<DataPoint>>> path;
        private final Color color;

        public Series( Property<ArrayList<Option<DataPoint>>> path, Color color ) {
            this.path = path;
            this.color = color;
        }

        public Paint getColor() {
            return color;
        }

        public void addPoint( final double time, final double value ) {
            path.setValue( new ArrayList<Option<DataPoint>>( path.getValue() ) {{
                add( new Option.Some<DataPoint>( new DataPoint( time, value ) ) );
            }} );
        }

        public Shape toShape() {
            DoubleGeneralPath generalPath = new DoubleGeneralPath();
            boolean moved = false;
            for ( Option<DataPoint> value : path.getValue() ) {
                if ( value.isSome() ) {
                    final DataPoint dataPoint = value.get();
                    if ( !moved ) {
                        generalPath.moveTo( dataPoint.time, dataPoint.value );
                        moved = true;
                    }
                    else {
                        generalPath.lineTo( dataPoint.time, dataPoint.value );
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
}
