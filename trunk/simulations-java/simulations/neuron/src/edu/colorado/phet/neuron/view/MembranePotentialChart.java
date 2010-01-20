/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Chart for depicting the membrane potential in the play area.
 * 
 * Author: John Blanco, Sam Reid
 */

public class MembranePotentialChart extends PNode {
	
	private static final Color STROKE_COLOR = Color.red;
	
    private JFreeChart chart;
    private JFreeChartNode jFreeChartNode;
    private PPath path;
    private int crossSectionY;
    private AxonModel axonModel;
	private XYSeries dataSeries = new XYSeries("0");

    public MembranePotentialChart( Dimension2D size, String title, AxonModel axonModel, String distanceUnits, double minX, double maxX ) {
    	
        this.axonModel = axonModel;
        // TODO: Temp - create some bogus data in order to see something initially.
        dataSeries.add(0, 0);
        dataSeries.add(10, 0);
        dataSeries.add(20, 0);
        dataSeries.add(30, 0.5);
        dataSeries.add(40, 1);
        dataSeries.add(50, 0.5);
        dataSeries.add(60, 0);
        XYDataset dataset = new XYSeriesCollection( dataSeries );
        // TODO: Internationalize.
        chart = createXYLineChart2( title, "Time (ms)", "Membrane Potential (mv)", dataset, PlotOrientation.VERTICAL);
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible( true );
        chart.getXYPlot().getRangeAxis().setRange( -50.0, 100.0 );
        jFreeChartNode = new JFreeChartNode( chart, true );
        jFreeChartNode.setBounds( 0, 0, size.getWidth(), size.getHeight() );

        setHorizontalLabel( MessageFormat.format( "Time (ms)", new Object[]{distanceUnits} ) );
        setHorizontalRange( minX, maxX );

        jFreeChartNode.updateChartRenderingInfo();
        path = new PPath();
        path.setStroke( new BasicStroke( 3 ) );
        path.setStrokePaint( Color.blue );

        addChild( jFreeChartNode );
        addChild( path );
//        updateLocation();

    }
    
    public void addDataPoint(double time, double voltage){
    	dataSeries.add(time, voltage);
    }

    public void setHorizontalLabel( String horizontalUnits ) {
        chart.getXYPlot().getDomainAxis().setLabel( horizontalUnits );
    }

    public void setHorizontalRange( double min, double max ) {
        chart.getXYPlot().getDomainAxis().setRange( min, max );
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
    
    protected static JFreeChart createXYLineChart(String title, String xAxisLabel, String yAxisLabel,
    		XYDataset dataset, PlotOrientation orientation) {

    	if (orientation == null) {
    		throw new IllegalArgumentException("Null 'orientation' argument.");
    	}
    	NumberAxis xAxis = new NumberAxis(xAxisLabel);
    	xAxis.setLabelFont(new PhetFont(18));
    	NumberAxis yAxis = new NumberAxis(yAxisLabel);
    	yAxis.setLabelFont(new PhetFont(18));
    	XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
    	XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
    	plot.setOrientation(orientation);

    	JFreeChart chart = new JFreeChart(title, new PhetFont(30), plot, false);

    	return chart;
    }
    
    private static JFreeChart createXYLineChart2(String title, String xAxisLabel, String yAxisLabel,
    		XYDataset dataset, PlotOrientation orientation) {

    	if (orientation == null) {
    		throw new IllegalArgumentException("Null 'orientation' argument.");
    	}
    	
        JFreeChart chart = ChartFactory.createXYLineChart(
            title,
            xAxisLabel,
            yAxisLabel,
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        

    	return chart;
    }
    /**
     * Creates a chart.
     * 
     * @param dataset  a dataset.
     * 
     * @return A chart.
     */
    private static JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Legal & General Unit Trust Prices",  // title
            "Date",             // x-axis label
            "Price Per Unit",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
        }
        
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        
        return chart;

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
    
    public static void main(String[] args) {
    	
    	// Set up the main frame for the application.
    	Dimension2D size = new PDimension(800, 600);
		JFrame frame = new JFrame();
        frame.setSize( (int)size.getWidth(), (int)size.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        // Create the chart.
        MembranePotentialChart membranePotentialChart = 
        	new MembranePotentialChart(size, "Test Chart", null, "mV", 0, 1000);
        
        // Create the canvas and add the chart to it.
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( membranePotentialChart );
        
        // Create and add a button that will add a new data point each time
        // it is pressed.
        JButton button = new JButton("Add Data Point");
        PSwing buttonPSwing = new PSwing(button);
        phetPCanvas.addScreenChild(buttonPSwing);
        
        // Associate the canvas and the frame and display it.
        frame.setContentPane(phetPCanvas);
        frame.setVisible(true);
	}
}
