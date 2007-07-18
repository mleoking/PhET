package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.ITimeSeries;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.umd.cs.piccolo.nodes.PImage;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jul 12, 2007, 9:43:17 AM
 */
public class RotationGraph extends MotionControlGraph {
    private ArrayList secondarySeries = new ArrayList();//keep track of series for the 2nd bug so we can show/hide them together
    private ArrayList seriesPairs = new ArrayList();

    public RotationGraph( PhetPCanvas pSwingCanvas, ISimulationVariable simulationVariable,
                          String label, String title, String units, double min, double max, PImage thumb,
                          RotationModel motionModel, boolean editable, TimeSeriesModel timeSeriesModel,
                          UpdateStrategy updateStrategy, double maxDomainValue, RotationPlatform iPositionDriven ) {
        super( pSwingCanvas, simulationVariable,
               label, title, min, max, thumb,
               motionModel, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
        super.getDynamicJFreeChartNode().setAutoUpdateAll( false );
        ValueAxis oldRangeAxis = getJFreeChartNode().getChart().getXYPlot().getRangeAxis();
        RotationGraphNumberAxis verticalAxis = new RotationGraphNumberAxis( title + " (" + units + ")" );
        verticalAxis.setRange( oldRangeAxis.getRange() );
        getJFreeChartNode().getChart().getXYPlot().setRangeAxis( verticalAxis );

        getDynamicJFreeChartNode().setBufferedSeries();
//        dynamicJFreeChartNode.setBufferedImmediateSeries();

        addListener( new Adapter() {
            public void zoomChanged() {
                getDynamicJFreeChartNode().forceUpdateAll();
            }
        } );
    }

    protected TitleLayer createTitleLayer() {
        return new RotationTitleLayer();
    }

    public class RotationTitleLayer extends TitleLayer {

        public RotationTitleLayer() {
        }

        public void addReadoutNode( ReadoutTitleNode titleNode ) {
            if( isSecondarySeries( titleNode.getSeries() ) ) {
                ControlGraphSeries primarySeries = getPrimarySeries( titleNode.getSeries() );
                ReadoutTitleNode primaryNode = super.getReadoutNode( primarySeries );
                titleNode.setOffset( primaryNode.getOffset().getX(), primaryNode.getOffset().getY()+primaryNode.getFullBounds().getHeight()+3);
            }
            else {
                titleNode.setOffset( getFullBounds().getWidth(), 0 );
            }
            addChild( titleNode );
        }

    }

    private ControlGraphSeries getPrimarySeries( ControlGraphSeries series ) {
        for( int i = 0; i < seriesPairs.size(); i++ ) {
            SeriesPair seriesPair = (SeriesPair)seriesPairs.get( i );
            if( seriesPair.getB() == series ) {
                return seriesPair.getA();
            }
        }
        return null;
    }

    private boolean isSecondarySeries( ControlGraphSeries series ) {
        return secondarySeries.contains( series );
    }

    public void addSecondarySeries( String title, Color color, String abbr, String units, ISimulationVariable simulationVariable, ITimeSeries timeSeries, Stroke stroke ) {
        addSecondarySeries( new ControlGraphSeries( title, color, abbr, units, simulationVariable, timeSeries, stroke ) );
    }

    public void addSecondarySeries( ControlGraphSeries graphSeries ) {
        secondarySeries.add( graphSeries );
        super.addSeries( graphSeries );
    }

    public void setSecondarySeriesVisible( boolean visible ) {
        for( int i = 0; i < secondarySeries.size(); i++ ) {
            ( (ControlGraphSeries)secondarySeries.get( i ) ).setVisible( visible );
        }
    }

    public void addSeriesPair( String name, ControlGraphSeries a, ControlGraphSeries b, RotationBody bodyB ) {
        seriesPairs.add( new SeriesPair( name, a, b, bodyB ) );
        addSeries( a );
        addSecondarySeries( b );
    }

    public int getSeriesPairCount() {
        return seriesPairs.size();
    }

    public SeriesPair getSeriesPair( int i ) {
        return (SeriesPair)seriesPairs.get( i );
    }

    public static class SeriesPair {
        private String name;
        private ControlGraphSeries a;
        private ControlGraphSeries b;
        private RotationBody body1;
        private boolean visible;

        public SeriesPair( String name, ControlGraphSeries a, ControlGraphSeries b, RotationBody body1 ) {
            this.name = name;
            this.a = a;
            this.b = b;
            this.body1 = body1;
            this.visible = a.isVisible();
            body1.addListener( new RotationBody.Adapter() {
                public void platformStateChanged() {
                    updateVisibility();
                }

                public void displayGraphChanged() {
                    updateVisibility();
                }
            } );
            b.addListener( new ControlGraphSeries.Listener() {
                public void visibilityChanged() {
                    updateVisibility();//todo this is a workaround to overwrite values set by other calls to setVisible(true)
                }
            } );
        }

        private void updateVisibility() {
            a.setVisible( visible );
            b.setVisible( visible && body1.isOnPlatform() && body1.getDisplayGraph() );
        }

        public String getName() {
            return name;
        }

        public ControlGraphSeries getA() {
            return a;
        }

        public ControlGraphSeries getB() {
            return b;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible( boolean selected ) {
            if( visible != selected ) {
                this.visible = selected;
                updateVisibility();
            }
        }
    }

    public static class RotationGraphNumberAxis extends NumberAxis {
        public RotationGraphNumberAxis( String title ) {
            super( title );
            setLabelFont( RotationLookAndFeel.getGraphVerticalAxisLabelFont() );
        }

        protected void selectVerticalAutoTickUnit( Graphics2D g2,
                                                   Rectangle2D dataArea,
                                                   RectangleEdge edge ) {
            //modify the value chosen by the superclass
            super.selectVerticalAutoTickUnit( g2, dataArea, edge );
            setTickUnit( new NumberTickUnit( getTickUnit().getSize() * 2 ), false, false );
        }
    }
}
