/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Chart for depicting the membrane potential in the play area.
 * 
 * Author: John Blanco, Sam Reid
 */

public class MembranePotentialChart extends PNode {
	
	private static final Color STROKE_COLOR = Color.red;
	
    private JFreeChart jFreeChart;
    private JFreeChartNode jFreeChartNode;
    private PPath path;
    private boolean colorized = true;
    private int crossSectionY;
    private AxonModel axonModel;

    public MembranePotentialChart( String title,
    		AxonModel axonModel, String distanceUnits, double minX, double maxX ) {
        this.axonModel = axonModel;
        XYSeries series = new XYSeries( "0" );
        XYDataset dataset = new XYSeriesCollection( series );
        // TODO: Internationalize.
        jFreeChart = ChartFactory.createXYLineChart( title, "Blah Blah", title, dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.getXYPlot().getRangeAxis().setTickLabelsVisible( false );
        jFreeChart.getXYPlot().getRangeAxis().setRange( -1.0, 1.0 );
        jFreeChartNode = new JFreeChartNode( jFreeChart, true );
        jFreeChartNode.setBounds( 0, 0, 500, 185 );

        setHorizontalLabel( MessageFormat.format( "Yadda yadda", new Object[]{distanceUnits} ) );
        setHorizontalRange( minX, maxX );

        jFreeChartNode.updateChartRenderingInfo();
        path = new PPath();
        path.setStroke( new BasicStroke( 3 ) );
        path.setStrokePaint( Color.blue );

        addChild( jFreeChartNode );
        addChild( path );
//        updateLocation();

    }

    public void setHorizontalLabel( String horizontalUnits ) {
        jFreeChart.getXYPlot().getDomainAxis().setLabel( horizontalUnits );
    }

    public void setHorizontalRange( double min, double max ) {
        jFreeChart.getXYPlot().getDomainAxis().setRange( min, max );
    }

    public Rectangle2D getChartBounds() {
        Rectangle2D bounds = jFreeChartNode.getFullBounds();
        localToParent( bounds );
        return bounds;
    }

    /*
    protected void updateLocation() {
        synchronizeWidth();
        synchronizeWidth();//in case the first one changed the insets of the graph

        Rectangle2D r2 = jFreeChartNode.getBounds();
        Rectangle2D data = jFreeChartNode.getDataArea();
//        double dataX = latticeScreenCoordinates.toScreenCoordinates( 0, 0 ).getX();
//        double dataY = latticeScreenCoordinates.toScreenCoordinates( 0, waveModel.getHeight() ).getY() + getChartOffset();
        double dataInsetX = data.getX() - r2.getX();
        jFreeChartNode.setBounds( 0, 0, r2.getWidth(), r2.getHeight() );
        // TODO: Sam was setting this to correspond with the location of the
        // model.  I'm not sure that I need to do this.  Set to 0,0 for now,
        // and get rid of the whole thing later if possible.
//        jFreeChartNode.setOffset( dataX - dataInsetX, dataY );
        jFreeChartNode.setOffset( 0, 0 );
    }
    */

    protected double getChartOffset() {
        return 0;
    }

    /*
    private void synchronizeWidth() {
        double diff = getDesiredDataWidth() - getActualDataWidth();
        changeDataWidth( (int) diff );
    }
    */

    private void changeDataWidth( int increase ) {
        Rectangle2D r2 = jFreeChartNode.getBounds();
        jFreeChartNode.setBounds( r2.getX(), r2.getY(), r2.getWidth() + increase, r2.getHeight() );
        jFreeChartNode.updateChartRenderingInfo();
    }

    private double getActualDataWidth() {
        return jFreeChartNode.getDataArea().getWidth();
    }

    public JFreeChartNode getjFreeChartNode() {
        return jFreeChartNode;
    }

    /*
    private double getDesiredDataWidth() {
        Point2D min = latticeScreenCoordinates.toScreenCoordinates( 0, 0 );
        Point2D max = latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth() - 1, waveModel.getHeight() - 1 );
        return max.getX() - min.getX();
    }
    */

    public void updateChart() {
        GeneralPath generalPath = new GeneralPath();
        Point2D[] pts = readValues();//todo this just assumes the chart transform matches perfectly
        if ( pts.length > 0 ) {
            generalPath.moveTo( (float) pts[0].getX(), (float) pts[0].getY() );//todo crop to fit inside data area.
        }
        for ( int i = 1; i < pts.length; i++ ) {
            generalPath.lineTo( (float) pts[i].getX(), (float) pts[i].getY() );
        }
        path.setPathTo( generalPath );
        path.setOffset( getPathLocation() );
    }

    protected Point2D getPathLocation() {
        Point2D nodeLoc = jFreeChartNode.plotToNode( new Point2D.Double( 0, 0 ) );
        jFreeChartNode.localToParent( nodeLoc );
        return nodeLoc;
    }

    protected Point2D[] readValues() {
//        return new WaveSampler( waveModel, -60, latticeScreenCoordinates.getCellWidth() ).readValues(crossSectionY);
    	// TODO: Make up bogus data for now.
    	Point2D[] retval = new Point2D[2];
    	retval[0] = new Point2D.Double(0, 0);
    	retval[1] = new Point2D.Double(10, 0);
    	return retval;
    }

    public double getChartHeight() {
        return jFreeChartNode.getFullBounds().getHeight();
    }

    public void setCurveVisible( boolean visible ) {
        path.setVisible( visible );
    }

    public boolean isCurveVisible() {
        return path.getVisible();
    }

    public void setCrossSectionYValue( int crossSectionY ) {
        this.crossSectionY = crossSectionY;
        updateChart();
    }
}
