/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Chart for depicting the membrane potential in the play area.
 * 
 * Author: John Blanco, Sam Reid
 */

public class MembranePotentialChart extends PNode {
	
	private static final Color STROKE_COLOR = Color.red;
	private static final double TIME_SPAN = 100; // In milliseconds.
	
    private JFreeChart chart;
    private JFreeChartNode jFreeChartNode;
    private int crossSectionY;
    private AxonModel axonModel;
	private XYSeries dataSeries = new XYSeries("0");

	private static NumberAxis xAxis;
	private static NumberAxis yAxis;

    public MembranePotentialChart( Dimension2D size, String title, AxonModel axonModel, String distanceUnits ) {
    	
        this.axonModel = axonModel;
        
        if (axonModel != null){
        	
        	// Register for clock ticks so that we can update.
        	axonModel.getClock().addClockListener(new ClockAdapter(){
        	    public void clockTicked( ClockEvent clockEvent ) {
        	    	updateChart(clockEvent);
        	    }
        	    public void simulationTimeReset( ClockEvent clockEvent ) {
        	    	clearChart();
        	    }
        	});
        }
        
        XYDataset dataset = new XYSeriesCollection( dataSeries );
        // TODO: Internationalize.
        chart = createXYLineChart( title, "Time (ms)", "Membrane Potential (mv)", dataset, PlotOrientation.VERTICAL);
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible( true );
        chart.getXYPlot().getRangeAxis().setRange( -0.1, 0.1 );
        jFreeChartNode = new JFreeChartNode( chart, false );
        jFreeChartNode.setBounds( 0, 0, size.getWidth(), size.getHeight() );

        setHorizontalLabel( MessageFormat.format( "Time (ms)", new Object[]{distanceUnits} ) );
        chart.getXYPlot().getDomainAxis().setRange( 0, TIME_SPAN );

        jFreeChartNode.updateChartRenderingInfo();

        addChild( jFreeChartNode );

//        updateLocation();

    }
    
    /**
     * Add a data point to the graph.
     * 
     * @param time - Time in milliseconds.
     * @param voltage - Voltage in volts.
     */
    public void addDataPoint(double time, double voltage){
    	dataSeries.add(time, voltage);
    	if (time > TIME_SPAN / 2){
    		// Slide the chart to the left to keep the data to keep the data
    		// in the middle.  This essentially creates a strip chart.
            chart.getXYPlot().getDomainAxis().setRange( time - TIME_SPAN / 2, time + TIME_SPAN / 2);
    	}
    }
    
    public void clearChart(){
    	dataSeries.clear();
    	chart.getXYPlot().getDomainAxis().setRange( 0, TIME_SPAN );
    }

    public void setHorizontalLabel( String horizontalUnits ) {
        chart.getXYPlot().getDomainAxis().setLabel( horizontalUnits );
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
    
    private static JFreeChart createXYLineChart(String title, String xAxisLabel, String yAxisLabel,
    		XYDataset dataset, PlotOrientation orientation) {

    	if (orientation == null) {
    		throw new IllegalArgumentException("Null 'orientation' argument.");
    	}

    	xAxis = new NumberAxis(xAxisLabel);
    	xAxis.setLabelFont(new PhetFont(18));
    	yAxis = new NumberAxis(yAxisLabel);
    	yAxis.setLabelFont(new PhetFont(18));

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

        // Set the stroke to be larger than the default.
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setStroke(new BasicStroke(3f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL));
        System.out.println(chart.getAntiAlias());

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
    
    private void updateChart(ClockEvent clockEvent){
    	double timeInMilliseconds = clockEvent.getSimulationTime() * 1000; 
   		addDataPoint(timeInMilliseconds, axonModel.getMembranePotential());
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

    public static void main(String[] args) {
    	
    	// Set up the main frame for the application.
    	Dimension2D size = new PDimension(800, 600);
		JFrame frame = new JFrame();
        frame.setSize( (int)size.getWidth(), (int)size.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        // Create the chart.
        final MembranePotentialChart membranePotentialChart = 
        	new MembranePotentialChart(size, "Test Chart", null, "mV");
        
        // Create the canvas and add the chart to it.
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( membranePotentialChart );
        
        // Create and add a button that will add a new data point each time
        // when it is pressed.
        JButton button = new JButton("Add Data Point");
        button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				membranePotentialChart.addDataPoint(200, 70);
				membranePotentialChart.addDataPoint(300, -20);
			}
		});
        PSwing buttonPSwing = new PSwing(button);
        phetPCanvas.addScreenChild(buttonPSwing);
        
        // Create and add a button that will clear the chart.
        JButton clearButton = new JButton("Clear Chart");
        clearButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				membranePotentialChart.clearChart();
			}
		});
        PSwing clearButtonPSwing = new PSwing(clearButton);
        clearButtonPSwing.setOffset(100, 0);
        phetPCanvas.addScreenChild(clearButtonPSwing);
        
        // Associate the canvas and the frame and display it.
        frame.setContentPane(phetPCanvas);
        frame.setVisible(true);
	}
}
