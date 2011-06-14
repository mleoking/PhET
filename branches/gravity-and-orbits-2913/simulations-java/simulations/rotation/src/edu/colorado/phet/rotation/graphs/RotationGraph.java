// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.graphs;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.ui.RectangleEdge;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.motion.graphs.ReadoutTitleNode;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

/**
 * Author: Sam Reid
 * Jul 12, 2007, 9:43:17 AM
 */
public class RotationGraph extends MotionControlGraph {
    private ArrayList secondarySeries = new ArrayList();//keep track of series for the 2nd bug so we can show/hide them together
    private ArrayList seriesPairs = new ArrayList();
    private RotationGraphNumberAxis verticalAxis;
    private String title;

    public RotationGraph( PhetPCanvas pSwingCanvas, ControlGraphSeries series,
                          String label, String title, String units, double min, double max,
                          boolean editable, TimeSeriesModel timeSeriesModel,
                          UpdateStrategy updateStrategy, double maxDomainValue, RotationPlatform iPositionDriven ) {
        super( pSwingCanvas, series,
               label, title, min, max,
               editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
        this.title = title;
        super.getDynamicJFreeChartNode().setAutoUpdateAll( false );

        verticalAxis = new RotationGraphNumberAxis( title + " (" + units + ")" );
        verticalAxis.setRange( getJFreeChartNode().getChart().getXYPlot().getRangeAxis().getRange() );
        getJFreeChartNode().getChart().getXYPlot().setRangeAxis( verticalAxis );

        NumberAxis domainAxis = new NumberAxis( RotationStrings.getString( "variable.time.s" ) );
        domainAxis.setLabelFont( new PhetFont( 12 ) );
        domainAxis.setRange( getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getRange() );
        domainAxis.setTickUnit( new NumberTickUnit( 2.5 ) );
        getJFreeChartNode().getChart().getXYPlot().setDomainAxis( domainAxis );

        getDynamicJFreeChartNode().setBufferedSeries();

        addListener( new Adapter() {
            public void zoomChanged() {
                getDynamicJFreeChartNode().forceUpdateAll();
            }
        } );
    }

    public RotationGraphNumberAxis getVerticalAxis() {
        return verticalAxis;
    }

    public String getTitle() {
        return title;
    }

    protected TitleLayer createTitleLayer() {
        return new RotationTitleLayer();
    }

    public class RotationTitleLayer extends TitleLayer {

        public RotationTitleLayer() {
        }

        public void addReadoutNode( ReadoutTitleNode titleNode ) {


            if ( isSecondarySeries( titleNode.getSeries() ) ) {
                ControlGraphSeries primarySeries = getPrimarySeries( titleNode.getSeries() );
                ReadoutTitleNode primaryNode = super.getReadoutNode( primarySeries );
                titleNode.setOffset( primaryNode.getOffset().getX(), primaryNode.getOffset().getY() + primaryNode.getFullBounds().getHeight() + 3 );
            }
            else {
                titleNode.setOffset( getReadoutNodeX(), 0 );
//                            titleNode.setOffset( getFullBounds().getWidth(), 0 );
//            addChild( titleNode );
            }
            addChild( titleNode );
        }

        private double getReadoutNodeX() {
            double maxX = 0.0;
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                if ( getChild( i ) instanceof ReadoutTitleNode ) {
                    ReadoutTitleNode readoutTitleNode = (ReadoutTitleNode) getChild( i );
//                    double x = readoutTitleNode.getX() + readoutTitleNode.getPreferredWidth();
                    double x = readoutTitleNode.getOffset().getX() + readoutTitleNode.getPreferredWidth();
                    maxX = Math.max( maxX, x );
                }
            }
            return maxX;
        }

    }

    private ControlGraphSeries getPrimarySeries( ControlGraphSeries series ) {
        for ( int i = 0; i < seriesPairs.size(); i++ ) {
            SeriesPair seriesPair = (SeriesPair) seriesPairs.get( i );
            if ( seriesPair.getB() == series ) {
                return seriesPair.getA();
            }
        }
        return null;
    }

    private boolean isSecondarySeries( ControlGraphSeries series ) {
        if ( secondarySeries == null ) {
            return false;//todo: this assumes (1) secondarySeries will be null for the first ControlGraphSeries, and that (2) that series should be a primary series
        }
        return secondarySeries.contains( series );
    }

    public void addSecondarySeries( ControlGraphSeries graphSeries ) {
        secondarySeries.add( graphSeries );
        addSeries( graphSeries );
    }

    public void setSecondarySeriesVisible( boolean visible ) {
        for ( int i = 0; i < secondarySeries.size(); i++ ) {
            ( (ControlGraphSeries) secondarySeries.get( i ) ).setVisible( visible );
        }
    }

    public SeriesPair addSeriesPair( String name, ControlGraphSeries a, ControlGraphSeries b, RotationBody bodyA, RotationBody bodyB ) {
        SeriesPair seriesPair = new SeriesPair( name, a, b, bodyA, bodyB );
        seriesPairs.add( seriesPair );
        addSeries( a );
        addSecondarySeries( b );
        return seriesPair;
    }

    public int getSeriesPairCount() {
        return seriesPairs.size();
    }

    public SeriesPair getSeriesPair( int i ) {
        return (SeriesPair) seriesPairs.get( i );
    }

    public static class SeriesPair {
        private String name;
        private ControlGraphSeries a;
        private ControlGraphSeries b;
        private RotationBody body0;
        private RotationBody body1;
        private boolean visible;

        public SeriesPair( String name, ControlGraphSeries a, ControlGraphSeries b, RotationBody body0, RotationBody body1 ) {
            this.name = name;
            this.a = a;
            this.b = b;
            this.body0 = body0;
            this.body1 = body1;
            this.visible = a.isVisible();
            body0.addListener( new RotationBody.Adapter() {

                public void platformStateChanged() {
                    updateVisibility();
                }

                public void displayGraphChanged() {
                    updateVisibility();
                }
            } );

            body1.addListener( new RotationBody.Adapter() {
                public void platformStateChanged() {
                    updateVisibility();
                }

                public void displayGraphChanged() {
                    updateVisibility();
                }
            } );
            b.addListener( new ControlGraphSeries.Adapter() {
                public void visibilityChanged() {
                    updateVisibility();//todo this is a workaround to overwrite values set by other calls to setVisible(true)
                }
            } );
        }

        private void updateVisibility() {
            a.setVisible( visible && body0.getDisplayGraph() );
//            b.setVisible( visible && body1.isOnPlatform() && body1.getDisplayGraph() );
            b.setVisible( visible && body1.getDisplayGraph() );
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
            if ( visible != selected ) {
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
