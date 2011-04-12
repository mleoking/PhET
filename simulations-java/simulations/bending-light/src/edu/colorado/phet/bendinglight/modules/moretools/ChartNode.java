// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
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
    private final Property<ModelViewTransform> transform;
    private PNode gridLines = new PNode();
    public static int DASH_ON = 10;
    public static int DASH_OFF = 5;

    public ChartNode( final Clock clock, final Rectangle chartArea, final ArrayList<Series> series ) {
        setPathTo( chartArea );
        setStroke( null );
        final double timeWidth = 100 * MAX_DT / TIME_SPEEDUP_SCALE;
        final int maxSampleCount = (int) Math.ceil( timeWidth / BendingLightModel.MIN_DT );
        transform = new Property<ModelViewTransform>( createRectangleMapping( new Rectangle2D.Double( 0, -1, timeWidth, 2 ), chartArea ) );
        addChild( gridLines );
        for ( Series s : series ) {
            addChild( new SeriesNode( s ) );
        }

        //Move over the view port as time passes
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                final double minTime = clock.getSimulationTime() - timeWidth;
                transform.setValue( createRectangleMapping( new Rectangle2D.Double( minTime, -1, timeWidth, 2 ), chartArea ) );

                //Update grid lines
                gridLines.removeAllChildren();

                double verticalGridLineSpacing = timeWidth / 4;//distance between vertical grid lines
                double verticalGridLineSpacingDelta = getDelta( verticalGridLineSpacing, clock );
                for ( double x = minTime - verticalGridLineSpacingDelta; x <= minTime + timeWidth; x += timeWidth / 4 ) {
                    addVerticalLine( x );
                }

                double horizontalGridLineDelta = getDelta( DASH_ON + DASH_OFF, clock );
                gridLines.addChild( new GridLine( minTime - horizontalGridLineDelta, 0, minTime + timeWidth, 0, 0 ) );//horizontal axis

                //Remove any points that have gone outside of the time window, otherwise it is a memory leak
                //You can confirm this is working by passing in maxSampleCount/2 instead of maxSampleCount and turning the DT down to MIN_DT
                for ( Series s : series ) {
                    s.keepLastSamples( maxSampleCount );
                }
            }
        } );
    }

    //Compute the phase offset so that grid lines appear to be moving at the right speed
    private double getDelta( double verticalGridLineSpacing, Clock clock ) {
        final double totalNumPeriods = clock.getSimulationTime() / verticalGridLineSpacing;
        int integralNumberOfPeriods = (int) totalNumPeriods;//for computing the phase so we make the right number of grid lines
        return ( totalNumPeriods - integralNumberOfPeriods ) * verticalGridLineSpacing;
    }

    private void addVerticalLine( double x ) {
        gridLines.addChild( new GridLine( x, -1, x, 1, 0 ) );
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

        public void keepLastSamples( final int maxSampleCount ) {
            path.setValue( new ArrayList<Option<DataPoint>>( path.getValue().subList( Math.max( 0, path.getValue().size() - maxSampleCount ), path.getValue().size() ) ) );
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
        public GridLine( final double x1, final double y1, final double x2, final double y2, double phase ) {
            final float strokeWidth = 2;
            addChild( new PhetPPath( new BasicStroke( strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] { DASH_ON, DASH_OFF }, (float) phase ), Color.lightGray ) {
                {
                    //Grid lines are dynamically generated and therefore do not need to observe the transform
                    setPathTo( transform.getValue().modelToView( new Line2D.Double( x1, y1, x2, y2 ) ) );
                }

                //Provide a faster implementation because regenerating grid lines with original getPathBoundsWithStroke is too expensive
                @Override public Rectangle2D getPathBoundsWithStroke() {
                    return RectangleUtils.expand( getPathReference().getBounds2D(), strokeWidth, strokeWidth );
                }
            } );
        }
    }
}
