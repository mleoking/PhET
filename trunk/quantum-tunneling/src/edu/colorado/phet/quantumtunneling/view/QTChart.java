/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Dimension;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;

import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.umd.cs.piccolo.PNode;


/**
 * QTChart is a PNode wrapper around a JFreeChart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTChart extends PNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JFreeChart _chart;
    private ChartPanel _chartPanel;
    private PSwing _pSwing;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param canvas
     * @param title
     * @param xAxisLabel
     * @param yAxisLabel
     */
    public QTChart( PSwingCanvas canvas, String title, String xAxisLabel, String yAxisLabel ) {
        
        // Create a JFreeChart
        _chart = ChartFactory.createXYLineChart( 
                title,            // title
                xAxisLabel,       // x-axis label
                yAxisLabel,       // y-axis label
                null,             // data
                PlotOrientation.VERTICAL,
                false,            // create legend?
                false,            // generate tooltips?
                false             // generate URLs?
        );
        _chart.setBackgroundPaint( QTConstants.CANVAS_COLOR );
        
        // Put the JFreeChart inside a ChartPanel (a JPanel)
        _chartPanel = new ChartPanel( _chart );
        _chartPanel.setOpaque( false );
        
        // Wrap the ChartPanel with PSwing
        _pSwing = new PSwing( canvas, _chartPanel );
        
        // Wrap the PSwing with this PNode
        addChild( _pSwing );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the font used to draw the axes labels.
     * 
     * @param font
     */
    public void setAxisLabelFont( Font font ) {
        XYPlot xyPlot = _chart.getXYPlot();
        xyPlot.getDomainAxis().setLabelFont( font ); // x axis
        xyPlot.getRangeAxis().setLabelFont( font ); // y axis
    }
    
    /**
     * Sets the range of the X axis.
     * 
     * @param range
     */
    public void setXRange( Range range ) {
        XYPlot xyPlot = _chart.getXYPlot();
        xyPlot.getDomainAxis().setRange( range );
    }
    
    /**
     * Sets the range of the Y axis.
     * 
     * @param range
     */
    public void setYRange( Range range ) {
        XYPlot xyPlot = _chart.getXYPlot();
        xyPlot.getRangeAxis().setRange( range );
    }
    
    /**
     * Sets the dimensions of the chart.
     * 
     * @param width
     * @param height
     */
    public void setSize( int width, int height ) {
        setSize( new Dimension( width, height ) );
    }
    
    /**
     * Sets the dimensions of the chart.
     * 
     * @param size
     */
    public void setSize( Dimension size ) {
        if ( size.getWidth() > 0 && size.getHeight() > 0 ) {
            _chartPanel.setPreferredSize( size );
            _pSwing.computeBounds();
            _pSwing.repaint();
            repaint();
        }
    }
}
