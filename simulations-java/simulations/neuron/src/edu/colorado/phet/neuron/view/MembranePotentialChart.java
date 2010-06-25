/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;

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
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.NeuronModel;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Chart for depicting the membrane potential.  This is a PNode, and as such
 * is intended for use primarily in the play area.
 * 
 * Originally, this chart was designed to scroll once there was enough data
 * the fill the chart half way, but this turned out to be too CPU intensive,
 * so it was changed to draw one line of data across the screen and then stop.
 * The user can clear the chart and trigger another action potential to start
 * recording data again.
 * 
 * This chart also controls the record-and-playback state of the model.  This
 * is done so that the window of recorded data in the model matches that shown
 * in the chart, allowing the user to set the model state at any time shown in
 * the chart.

 * Author: John Blanco
 */

public class MembranePotentialChart extends PNode implements SimpleObserver {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	private static final double TIME_SPAN = 25; // In seconds.
	
	// This value sets the frequency of chart updates, which helps to reduce
	// the processor consumption.
	private static final double UPDATE_PERIOD = 1 * NeuronDefaults.DEFAULT_ACTION_POTENTIAL_CLOCK_DT; // In seconds
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    private JFreeChart chart;
    private JFreeChartNode jFreeChartNode;
    private NeuronModel neuronModel;
	private XYSeries dataSeries = new XYSeries("0");
	private ChartCursor chartCursor;
	private static NumberAxis xAxis;
	private static NumberAxis yAxis;
	private boolean chartIsFull = false;
	private double updateCountdownTimer = 0;  // Init to zero to an update occurs right away.
	private double timeIndexOfFirstDataPt = 0;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
    public MembranePotentialChart( Dimension2D size, String title, final NeuronModel neuronModel ) {
    	
    	assert neuronModel != null;
        this.neuronModel = neuronModel;
        
    	// Register for clock ticks so that we can update.
    	neuronModel.getClock().addClockListener(new ClockAdapter(){
    	    public void clockTicked( ClockEvent clockEvent ) {
    	    	updateChart(clockEvent);
    	    }
    	    public void simulationTimeReset( ClockEvent clockEvent ) {
                neuronModel.setModeLive();
    	    	clearChart();
    	    	updateChartCursorVisibility();
    	    }
    	});
    	
    	// Register for model events that are important to us.
    	neuronModel.addListener(new NeuronModel.Adapter(){
    		
    		public void stimulusPulseInitiated() {
    			if (!MembranePotentialChart.this.neuronModel.isPotentialChartVisible()){
    				// If the chart is not visible, we clear any previous
    				// recording.
    				clearChart();
    			}
    			// Start recording, if it isn't already happening.
    			neuronModel.startRecording();
    		}
    	});
    	
    	// Register as an observer of model events related to record and
    	// playback.
    	neuronModel.addObserver(this);
        
        // Create the chart itself, i.e. the place where date will be shown.
        XYDataset dataset = new XYSeriesCollection( dataSeries );
        chart = createXYLineChart( title, NeuronStrings.MEMBRANE_POTENTIAL_X_AXIS_LABEL,
        		NeuronStrings.MEMBRANE_POTENTIAL_Y_AXIS_LABEL, dataset, PlotOrientation.VERTICAL);
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible( true );
        chart.getXYPlot().getRangeAxis().setRange( -100, 100 );
        jFreeChartNode = new JFreeChartNode( chart, false );
        jFreeChartNode.setBounds( 0, 0, size.getWidth(), size.getHeight() );
        chart.getXYPlot().getDomainAxis().setRange( 0, TIME_SPAN );
        jFreeChartNode.updateChartRenderingInfo();

        // Add the chart to this node.
        addChild( jFreeChartNode );
        
        // Add the chart cursor, which will allow the user to move back and
        // forth through time.
        chartCursor = new ChartCursor(jFreeChartNode);
        addChild(chartCursor);
        
		// Add a handler to the chart cursor that will track when it is moved
        // by the user and will set the model time accordingly.
        
        chartCursor.addInputEventListener( new PBasicInputEventHandler() {
        	
            Point2D pressPoint;
            double pressTime;

            public void mousePressed( PInputEvent event ) {
                pressPoint = event.getPositionRelativeTo( MembranePotentialChart.this );
                pressTime = jFreeChartNode.nodeToPlot(chartCursor.getOffset()).getX();
                neuronModel.setPlayback(1); // Set into playback mode.
            }

            public Point2D localToPlotDifferential( double dx, double dy ) {
                Point2D pt1 = new Point2D.Double( 0, 0 );
                Point2D pt2 = new Point2D.Double( dx, dy );
                localToGlobal( pt1 );
                localToGlobal( pt2 );
                jFreeChartNode.globalToLocal( pt1 );
                jFreeChartNode.globalToLocal( pt2 );
                pt1 = jFreeChartNode.nodeToPlot( pt1 );
                pt2 = jFreeChartNode.nodeToPlot( pt2 );
                return new Point2D.Double( pt2.getX() - pt1.getX(), pt2.getY() - pt1.getY() );
            }

            public void mouseDragged( PInputEvent event ) {
                Point2D d = event.getPositionRelativeTo( MembranePotentialChart.this );
                Point2D dx = new Point2D.Double( d.getX() - pressPoint.getX(), d.getY() - pressPoint.getY() );
                Point2D diff = localToPlotDifferential( dx.getX(), dx.getY() );
                double recordingTimeIndex = pressTime + diff.getX();
                recordingTimeIndex = MathUtil.clamp(0, recordingTimeIndex, getLastTimeValue());
                double compensatedRecordingTimeIndex = recordingTimeIndex / 1000 + neuronModel.getMinRecordedTime();
                neuronModel.setTime(compensatedRecordingTimeIndex);
            }
        } );
        
		// Add the button that will allow the user to close the chart.  This
        // will look like a red 'x' in the corner of the chart, much like the
        // one seen on standard MS Windows apps.
		ImageIcon imageIcon = new ImageIcon( 
				PhetCommonResources.getInstance().getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON) );
		JButton closeButton = new JButton( imageIcon );
		closeButton.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				neuronModel.setPotentialChartVisible(false);
			}
		} );
		
		PSwing closePSwing = new PSwing( closeButton );
		closePSwing.setOffset(size.getWidth() - closeButton.getBounds().width - 2, 2);
		closePSwing.addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
		addChild(closePSwing);
		
        // Add the button for clearing the chart.
        JButton clearButton = new JButton(NeuronStrings.MEMBRANE_POTENTIAL_CLEAR_CHART);
        clearButton.setFont(new PhetFont(14));
        clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If an action potential is in progress, start or continue
				// recording.
				if (neuronModel.isActionPotentialInProgress()){
					neuronModel.startRecording();
				}
				else if (neuronModel.isRecord()){
					// Stop recording if one is in progress.
					neuronModel.setModeLive();
				}
				// Clear the chart.
				clearChart();
			}
		});
        PSwing clearButtonPSwing = new PSwing(clearButton);
        clearButtonPSwing.setOffset(
        		closePSwing.getFullBoundsReference().getMinX() - clearButtonPSwing.getFullBoundsReference().width - 10,
        		0);
        addChild(clearButtonPSwing);
        
        // Final initialization steps.
        updateChartCursorVisibility();
        updateChartCursorPos();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /**
     * Add a data point to the graph.
     * 
     * @param time - Time in milliseconds.
     * @param voltage - Voltage in volts.
     * @param update - Controls if graph should be refreshed on the screen.
     */
    private void addDataPoint(double time, double voltage, boolean update){

    	if (dataSeries.getItemCount() == 0){
    		// This is the first data point added since the last time the
    		// chart was cleared or since it was created.  Record the time
    		// index for future reference.
    		timeIndexOfFirstDataPt = time;
    	}
    	
    	// If the chart isn't full, add the data point to the data series.
    	// Note that internally we work in millivolts, not volts.
    	assert (time - timeIndexOfFirstDataPt >= 0);
    	if (time - timeIndexOfFirstDataPt <= TIME_SPAN){
    		dataSeries.add(time - timeIndexOfFirstDataPt, voltage * 1000, update);
    		chartIsFull = false;
    	}
    	else if (!chartIsFull){
    	    // This is the first data point to be received that is outside of
    	    // the chart's range.  Add it anyway so that there is no gap
    	    // in the data shown at the end of the chart.
            dataSeries.add(time - timeIndexOfFirstDataPt, voltage * 1000, true);
    	    chartIsFull = true;
    	}
    	else{
    	    System.out.println(getClass().getName() + " Warning: Attempt to add data to full chart, ignoring.");
    	}
    }
    
    /**
     * Get the last time value in the data series.  This is assumed to be the
     * highest time value, since data points are expected to be added in order
     * of increasing time.  If no data is present, 0 is returned.
     */
    private double getLastTimeValue(){
    	double timeOfLastDataPoint = 0;
    	if (dataSeries.getItemCount() > 0){
    		timeOfLastDataPoint = dataSeries.getX(dataSeries.getItemCount() - 1).doubleValue(); 
    	}
    	return timeOfLastDataPoint;
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
    	
    	if (neuronModel.isRecord()){
    		if (!chartIsFull && clockEvent.getSimulationTimeChange() > 0){
    			updateCountdownTimer -= clockEvent.getSimulationTimeChange();
    			
    			double timeInMilliseconds = neuronModel.getTime() * 1000;
    			
    			if (updateCountdownTimer <= 0){
    				addDataPoint(timeInMilliseconds, neuronModel.getMembranePotential(), true);
    				updateCountdownTimer = UPDATE_PERIOD;
    			}
    			else{
    				addDataPoint(timeInMilliseconds, neuronModel.getMembranePotential(), false);
    			}
    		}
    		
    		if (chartIsFull && neuronModel.isRecord()){
    			// The chart is full, so it is time to stop recording.
    			neuronModel.setModeLive();
    		}
    	}
    }
    
    /**
     * Clear all data from the chart.
     */
    private void clearChart(){
    	dataSeries.clear();
    	chartIsFull = false;
    	neuronModel.clearHistory();
    	updateChartCursorVisibility();
    }
    
    public JFreeChartNode getJFreeChartNode() {
        return jFreeChartNode;
    }
    
    private void updateChartCursorVisibility(){
    	// Deciding whether or not the chart cursor should be visible is a
    	// little tricky, so I've tried to make the logic very explicit for
    	// easier maintenance.  Basically, any time we are in playback mode
    	// and we are somewhere on the chart, or when stepping and recording,
    	// the cursor should be seen.
    	
    	double timeOnChart = (neuronModel.getTime() - neuronModel.getMinRecordedTime()) * 1000;
    	boolean isCurrentTimeOnChart = ( timeOnChart >= 0 ) && ( timeOnChart <= TIME_SPAN );
    	boolean dataExists = dataSeries.getItemCount() > 0;
    	
    	boolean chartCursorVisible = isCurrentTimeOnChart && dataExists && 
    	    (neuronModel.isPlayback() || (neuronModel.getClock().isPaused() && neuronModel.isRecord()));
    	
    	chartCursor.setVisible(chartCursorVisible);
    }
    
    private void moveChartCursorToTime(double time){
        Point2D cursorPos = jFreeChartNode.plotToNode( new Point2D.Double( time, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound() ) );
        chartCursor.setOffset(cursorPos);
    }
    
	//----------------------------------------------------------------------------
	// Inner Classes and Interfaces
	//----------------------------------------------------------------------------
    
    /**
     * This class represents the cursor that the user can grab and move around
     * in order to move the sim back and forth in time.
     */
    private static class ChartCursor extends PPath {

    	private static final double WIDTH_PROPORTION = 0.013;
    	private static final Color FILL_COLOR = new Color( 50, 50, 200, 80 );
    	private static final Color STROKE_COLOR = Color.DARK_GRAY;
    	private static final Stroke STROKE = new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10.0f, 5.0f}, 0 );
    	
		public ChartCursor(JFreeChartNode jFreeChartNode) {
			
			// Set up the general appearance.
			setStroke(STROKE);
			setStrokePaint(STROKE_COLOR);
			setPaint(FILL_COLOR);
			
	        Point2D topOfPlotArea = jFreeChartNode.plotToNode( new Point2D.Double( 0, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound() ) );
	        Point2D bottomOfPlotArea = jFreeChartNode.plotToNode( new Point2D.Double( 0, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getLowerBound() ) );
	        
			// Set the shape.  The shape is created so that it is centered
			// around an offset of 0 in the x direction and the top edge is
			// at 0 in the y direction.
			double width = jFreeChartNode.getFullBoundsReference().width * WIDTH_PROPORTION;
			setPathTo(new Rectangle2D.Double(-width / 2, 0, width, bottomOfPlotArea.getY() - topOfPlotArea.getY()));
			
			// Set a cursor handler for this node.
			addInputEventListener(new CursorHandler(Cursor.E_RESIZE_CURSOR));
		}
    }
    
    private void updateChartCursorPos(){
		double recordingStartTime = neuronModel.getMinRecordedTime();
		double recordingCurrentTime = neuronModel.getTime();
		moveChartCursorToTime( ( recordingCurrentTime - recordingStartTime ) * 1000 );
    }

    /**
     * Handle change notifications from the record-and-playback portion of the
     * model.
     */
	public void update() {
		updateChartCursorVisibility();
		if (chartCursor.getVisible()){
			updateChartCursorPos();
		}
	}
}
