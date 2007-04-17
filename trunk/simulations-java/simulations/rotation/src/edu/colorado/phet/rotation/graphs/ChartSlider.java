package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * This component contains both a JFreeChartNode (supplied by the caller) and a vertical Slider node, which spans the data range of the JFreeChart.
 * This component can be used to display and control an xy dataset.
 *
 * @author Sam Reid
 */
public class ChartSlider extends PNode {
    private PhetPPath trackPPath;
    private PNode sliderThumb;
    private double value = 0.0;
    private ArrayList listeners = new ArrayList();
    private JFreeChartNode jFreeChartNode;

    public ChartSlider( JFreeChartNode jFreeChartNode, final PNode sliderThumb ) {
        this.sliderThumb = sliderThumb;
        this.sliderThumb.addInputEventListener( new CursorHandler() );
        trackPPath = new PhetPPath( new BasicStroke( 1 ), Color.black );
        addChild( trackPPath );
        addChild( sliderThumb );
        sliderThumb.addInputEventListener( new PBasicInputEventHandler() {
            Point2D initDragPoint = null;
            double origY;

            public void mousePressed( PInputEvent event ) {
                initDragPoint = event.getPositionRelativeTo( sliderThumb.getParent() );
                origY = value;
            }

            public void mouseReleased( PInputEvent event ) {
                initDragPoint = null;
            }

            public void mouseDragged( PInputEvent event ) {
                if( initDragPoint == null ) {
                    mousePressed( event );
                }
                double yCurrent = event.getPositionRelativeTo( sliderThumb.getParent() ).getY();
                double nodeDY = yCurrent - initDragPoint.getY();
                Point2D plot1 = nodeToPlot( new Point2D.Double( 0, 0 ) );
                Point2D plot2 = nodeToPlot( new Point2D.Double( 0, nodeDY ) );
                double plotDY = plot2.getY() - plot1.getY();
                setValue( clamp( origY + plotDY ) );
            }
        } );

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
        return jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound();
    }

    protected double getMinRangeValue() {
        return jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getLowerBound();
    }

    protected Rectangle2D getDataArea() {
        jFreeChartNode.updateChartRenderingInfo();
        return jFreeChartNode.getDataArea();
    }

    protected Point2D plotToNode( Point2D.Double aDouble ) {
        return jFreeChartNode.plotToNode( aDouble );
    }

    protected Point2D nodeToPlot( Point2D.Double pt ) {
        return jFreeChartNode.nodeToPlot( pt );
    }

    private double clamp( double v ) {
        if( v > getMaxRangeValue() ) {
            v = getMaxRangeValue();
        }
        if( v < getMinRangeValue() ) {
            v = getMinRangeValue();
        }
        return v;
    }

    /**
     * Sets the value of the slider for this chart.
     *
     * @param value the value to set for this controller.
     */
    public void setValue( double value ) {
        if( this.value != value ) {
            this.value = value;
            updateThumbLocation();
            notifyValueChanged();
        }
    }

    /**
     * Gets the value of the control slider.
     *
     * @return the value of the control slider.
     */
    public double getValue() {
        return value;
    }

    protected void updateLayout() {
        Rectangle2D dataArea = getDataArea();
        trackPPath.setPathTo( new Rectangle2D.Double( 0, dataArea.getY(), 5, dataArea.getHeight() ) );
        updateThumbLocation();
    }

    private void updateThumbLocation() {
        Point2D nodeLocation = plotToNode( new Point2D.Double( 0, clamp( value ) ) );
        sliderThumb.setOffset( trackPPath.getFullBounds().getCenterX() - sliderThumb.getFullBounds().getWidth() / 2.0,
                               nodeLocation.getY() - sliderThumb.getFullBounds().getHeight() / 2.0 );
    }

    /**
     * Clients can listen for value change events, whether generated by the slider or another call.
     */
    public static interface Listener {
        void valueChanged();
    }

    /**
     * Adds a listener for value change events.
     *
     * @param listener the value change listener.
     */
    public void addListener( ChartSlider.Listener listener ) {
        listeners.add( listener );
    }

    private void notifyValueChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ChartSlider.Listener listener = (ChartSlider.Listener)listeners.get( i );
            listener.valueChanged();
        }
    }
}
