/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Chart for depicting the membrane potential.  This is a PNode, and as such
 * is intended for use primarily in the play area.
 * 
 * Originally, this chart was designed to scroll once there was enough data
 * the fill the chart half way, but this turned out to be too CPU intensive,
 * so it was changed to draw one line of data across the screen, then clear
 * and draw the next line.

 * Author: John Blanco
 */

public class MembranePotentialChart extends PNode {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	private static final double TIME_SPAN = 25; // In milliseconds.
	
	// This value sets the frequency of chart updates, which helps to reduce
	// the processor consumption.
	private static final double UPDATE_PERIOD = 1 * NeuronDefaults.CLOCK_DT; // In seconds
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    private JFreeChart chart;
    private JFreeChartNode jFreeChartNode;
    private AxonModel axonModel;
	private XYSeries dataSeries = new XYSeries("0");

	private static NumberAxis xAxis;
	private static NumberAxis yAxis;
	
	private double updateCountdownTimer = 0;  // Init to zero to an update occurs right away.
	
	private double endTimeOfChart = TIME_SPAN;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
    public MembranePotentialChart( Dimension2D size, String title, AxonModel axonModel, String distanceUnits ) {
    	
        this.axonModel = axonModel;
        
        if (axonModel != null){
        	
        	// Register for clock ticks so that we can update.
        	axonModel.getClock().addClockListener(new ClockAdapter(){
        	    public void clockTicked( ClockEvent clockEvent ) {
        	    	updateChart(clockEvent);
        	    }
        	    public void simulationTimeReset( ClockEvent clockEvent ) {
        	    	clearAndSetStartTime(0);
        	    }
        	});
        }
        
        XYDataset dataset = new XYSeriesCollection( dataSeries );
        // TODO: i18n.
        chart = createXYLineChart( title, "Time (ms)", "Membrane Potential (mv)", dataset, PlotOrientation.VERTICAL);
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible( true );
        chart.getXYPlot().getRangeAxis().setRange( -100, 100 );
        jFreeChartNode = new JFreeChartNode( chart, false );
        jFreeChartNode.setBounds( 0, 0, size.getWidth(), size.getHeight() );

        // TODO: i18n
        chart.getXYPlot().getDomainAxis().setLabel( "Time (ms)" );
        chart.getXYPlot().getDomainAxis().setRange( 0, TIME_SPAN );

        jFreeChartNode.updateChartRenderingInfo();

        addChild( jFreeChartNode );
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /**
     * Add a data point to the graph.
     * 
     * @param time - Time in milliseconds.
     * @param voltage - Voltage in volts.
     */
    private void addDataPoint(double time, double voltage, boolean update){

    	if (time > endTimeOfChart){
    		clearAndSetStartTime(time);
    	}
    	// Add the date point to the data series.  Note that internally we
    	// work in millivolts, not volts.
    	dataSeries.add(time, voltage * 1000, update);
    }
    
    /**
     * Create the JFreeChart chart that will show the data and that will be
     * contained by this node.
     * 
     * @param title
     * @param xAxisLabel
     * @param yAxisLabel
     * @param dataset
     * @param orientation
     * @return
     */
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

        // Set the stroke for the data line to be larger than the default.
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setStroke(new BasicStroke(3f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL));

    	return chart;
    }
    
    /**
     * Update the chart based on the current time and the model that is being
     * monitored.
     * 
     * @param clockEvent
     */
    private void updateChart(ClockEvent clockEvent){
    	updateCountdownTimer -= clockEvent.getSimulationTimeChange();
    	
    	double timeInMilliseconds = clockEvent.getSimulationTime() * 1000; 
    	
    	if (updateCountdownTimer <= 0){
    		addDataPoint(timeInMilliseconds, axonModel.getMembranePotential(), true);
    		updateCountdownTimer = UPDATE_PERIOD;
    	}
    	else{
    		addDataPoint(timeInMilliseconds, axonModel.getMembranePotential(), false);
    	}
    }

    /**
     * Clear all data from the chart and redraw the background starting from
     * the provided initial time value.
     *  
     * @param initialTime
     */
    private void clearAndSetStartTime(double initialTime){
    	dataSeries.clear();
    	initialTime = roundToResolution(initialTime, 0);
    	endTimeOfChart = initialTime + TIME_SPAN;
    	chart.getXYPlot().getDomainAxis().setRange( initialTime, endTimeOfChart );
    	updateCountdownTimer = 0;
    }
    
    /**
     * Utility to round to the specified number of decimal places.  Negative
     * values can be used to round to the 10's, 100's, etc, places (e.g. -1
     * means round to the nearest 10s digit, such as rounding 17 to 20).
     */
	private double roundToResolution(double val, int places) {
		
		double factor = Math.pow(10,places);

		// Shift the decimal the correct number of places.
		val = val * factor;

		// Round to the nearest integer.
		long tmp = Math.round(val);

		// Shift the decimal the correct number of places
		// back to the left.
		return (double)tmp / factor;
	}

	/**
	 * Test framework, do whatever is needed here to test the behavior of
	 * this node.
	 * 
	 * @param args
	 */
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
				membranePotentialChart.addDataPoint(200, 70, true);
				membranePotentialChart.addDataPoint(300, -20, true);
			}
		});
        PSwing buttonPSwing = new PSwing(button);
        phetPCanvas.addScreenChild(buttonPSwing);
        
        // Associate the canvas and the frame and display it.
        frame.setContentPane(phetPCanvas);
        frame.setVisible(true);
	}
}
