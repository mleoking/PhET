package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This component contains both a JFreeChartNode (supplied by the caller) and a vertical Slider node, which spans the data range of the JFreeChart.
 * This component can be used to display and control an xy dataset.
 *
 * @author Sam Reid
 */
public class BasicChartSlider extends AbstractChartSlider {
    private JFreeChartNode jFreeChartNode;

    /**
     * Constructs a VerticalChartControl to use the data area of the specified JFreeChartNode and the specified graphic for the thumb control.
     *
     * @param jFreeChartNode the chart to control.
     * @param sliderThumb    the PNode that displays the thumb of the slider.
     */
    public BasicChartSlider( final JFreeChartNode jFreeChartNode, final PNode sliderThumb ) {
        super( sliderThumb );
        this.jFreeChartNode = jFreeChartNode;
        jFreeChartNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLayout();
            }
        } );
        jFreeChartNode.getChart().addChangeListener( new ChartChangeListener() {
            public void chartChanged( ChartChangeEvent chartChangeEvent ) {
                updateLayout();
            }
        } );

        updateLayout();
    }

    protected double getMaxRangeValue() {
        if( jFreeChartNode == null ) {
            return 0;
        }
        return jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound();
    }

    protected double getMinRangeValue() {
        if( jFreeChartNode == null ) {
            return 0;
        }
        return jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getLowerBound();
    }

    protected Rectangle2D getDataArea() {
        if( jFreeChartNode == null ) {
            return new Rectangle2D.Double( 0, 0, 0, 0 );
        }
        return jFreeChartNode.getDataArea();
    }

    protected Point2D plotToNode( Point2D.Double aDouble ) {
        if( jFreeChartNode == null ) {
            return new Point2D.Double();
        }
        return jFreeChartNode.plotToNode( aDouble );
    }

    protected Point2D nodeToPlot( Point2D.Double pt ) {
        if( jFreeChartNode == null ) {
            return new Point2D.Double();
        }
        return jFreeChartNode.nodeToPlot( pt );
    }

}
