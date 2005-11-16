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
 * QTChart
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTChart extends PNode {

    private JFreeChart _chart;
    private ChartPanel _chartPanel;
    private PSwing _pSwing;
    
    public QTChart( PSwingCanvas canvas, String title, String xAxisLabel, String yAxisLabel ) {
        
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
        _chartPanel = new ChartPanel( _chart );
        _chartPanel.setOpaque( false );
        _pSwing = new PSwing( canvas, _chartPanel );
        
        addChild( _pSwing );
    }
    
    public void setAxisLabelFont( Font font ) {
        XYPlot xyPlot = _chart.getXYPlot();
        xyPlot.getDomainAxis().setLabelFont( font ); // x axis
        xyPlot.getRangeAxis().setLabelFont( font ); // y axis
    }
    
    public void setXRange( Range range ) {
        XYPlot xyPlot = _chart.getXYPlot();
        xyPlot.getDomainAxis().setRange( range );
    }
    
    public void setYRange( Range range ) {
        XYPlot xyPlot = _chart.getXYPlot();
        xyPlot.getRangeAxis().setRange( range );
    }
    
    public void setSize( int width, int height ) {
        setSize( new Dimension( width, height ) );
    }
    
    public void setSize( Dimension size ) {
        if ( size.getWidth() > 0 && size.getHeight() > 0 ) {
            _chartPanel.setPreferredSize( size );
            _pSwing.computeBounds();
            _pSwing.repaint();
            repaint();
        }
    }
}
