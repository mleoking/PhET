package edu.colorado.phet.rotation.graphs.combined;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class CombinedChartSlider extends AbstractChartSlider {
    private JFreeChartNode jFreeChartNode;
    private int index;

    /**
     * Constructs a VerticalChartControl to use the data area of the specified JFreeChartNode and the specified graphic for the thumb control.
     *
     * @param jFreeChartNode the chart to control.
     * @param sliderThumb    the PNode that displays the thumb of the slider.
     * @param index          the index of the subplot
     */
    public CombinedChartSlider( final JFreeChartNode jFreeChartNode, final PNode sliderThumb, int index ) {
        super( sliderThumb );
        this.jFreeChartNode = jFreeChartNode;
        this.index = index;
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

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateLayout();
            }
        } );
        timer.start();
    }

    protected double getMinRangeValue() {
        if( jFreeChartNode == null ) {
            return 0;
        }
        return getSubplot().getRangeAxis().getLowerBound();
    }

    private XYPlot getSubplot() {
        CombinedDomainXYPlot combinedDomainXYPlot = (CombinedDomainXYPlot)jFreeChartNode.getChart().getXYPlot();
        return (XYPlot)combinedDomainXYPlot.getSubplots().get( index );
    }

    protected double getMaxRangeValue() {
        if( jFreeChartNode == null ) {
            return 0;
        }
        return getSubplot().getRangeAxis().getUpperBound();
    }

    protected Rectangle2D getDataArea() {
        if( jFreeChartNode == null ) {
            return new Rectangle2D.Double( 0, 0, 0, 0 );
        }
        return jFreeChartNode.getDataArea( index );
    }

    protected Point2D plotToNode( Point2D.Double aDouble ) {
        if( jFreeChartNode == null ) {
            return new Point2D.Double();
        }
        return jFreeChartNode.subplotToNode( aDouble, index );
    }

    protected Point2D nodeToPlot( Point2D.Double pt ) {
        if( jFreeChartNode == null ) {
            return new Point2D.Double();
        }
        return jFreeChartNode.nodeToSubplot( pt, index );
    }

}
