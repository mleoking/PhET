/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.charts.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.charts.SinePlot;
import edu.colorado.phet.common.charts.StringLabelTable;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.phetgraphics.test.DeprecatedPhetApplicationLauncher;

/**
 * Tests the performance of charts by drawing lots of SinePlots (sine waves).
 * Set the colors for the sine waves so that they are evenly distributed
 * across the visible spectrum.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision:14669 $
 */
public class TestSinePlotPerformance {

    // Wave parameters
    private static final double L = 1.0;  // length of fundamental cycle
    private static final int MAX_WAVES = 200;
    private static final double PIXELS_PER_POINT = 2;

    // Chart parameters
    private static final Range2D CHART_RANGE = new Range2D( -0.5, -1, 0.5, 1 ); // xMin, yMin, xMax, yMax
    private static final Dimension CHART_SIZE = new Dimension( 650, 160 );
    private static final Point CHART_LOCATION = new Point( 50, 200 );

    // Axis parameter
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 2f );
    private static final Font AXIS_TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;

    // Tick Mark parameter
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final double X_MINOR_TICK_SPACING = ( L / 8 );
    private static final double Y_MAJOR_TICK_SPACING = 0.5;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font MAJOR_TICK_FONT = new PhetFont( Font.BOLD, 12 );
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = MAJOR_TICK_STROKE;
    private static final Font MINOR_TICK_FONT = MAJOR_TICK_FONT;
    private static final Color MINOR_TICK_COLOR = MAJOR_TICK_COLOR;

    // Gridline parameters
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    private static final Color MINOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );

    // Zoom parameters
    private static final double ZOOM_FACTOR = Math.sqrt( 2 );
    private static final int MIN_ZOOM_LEVEL = -2;
    private static final int MAX_ZOOM_LEVEL = +3;

    /**
     * main
     *
     * @param args
     * @throws IOException
     */
    public static void main( String args[] ) throws IOException {
        TestSinePlotPerformance test = new TestSinePlotPerformance( args );
    }

    public TestSinePlotPerformance( String[] args ) throws IOException {

        String title = "Test SinePlot Performance";
        IClock clock = new SwingClock( 1, 40 );
        boolean useClockControlPanel = false;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

        DeprecatedPhetApplicationLauncher app = new DeprecatedPhetApplicationLauncher( args,
                                                                       title, "", "", frameSetup );

        Module module = new TestModule( clock );
        app.setModules( new Module[]{module} );

        app.startApplication();
    }

    private class TestModule extends PhetGraphicsModule {

        private Chart _chart;
        private SinePlot[] _sinePlots;
        private JLabel _label;
        private JSlider _slider;
        private JButton _zoomInButton,
                _zoomOutButton;
        private int _zoomLevel;
        private Cursor _saveCursor;
        private JRadioButton _colorButton,
                _grayscaleButton;
        private boolean _colorEnabled;

        public TestModule( IClock clock ) {
            super( "Test Module", clock );

            _zoomLevel = 0;
            _colorEnabled = false;


            // Model
            BaseModel model = new BaseModel();
            setModel( model );

            // Apparatus Panel
            {
                ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
                apparatusPanel.setBackground( Color.WHITE );
                setApparatusPanel( apparatusPanel );

                // Chart
                _chart = new Chart( apparatusPanel, CHART_RANGE, CHART_SIZE );
                _chart.setLocation( CHART_LOCATION );
                apparatusPanel.addGraphic( _chart );

                // Enable antialiasing
                _chart.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

                // X axis
                {
                    _chart.getXAxis().setStroke( AXIS_STROKE );
                    _chart.getXAxis().setColor( AXIS_COLOR );

                    // Title
                    _chart.setXAxisTitle( new PhetTextGraphic( apparatusPanel, AXIS_TITLE_FONT, "x", AXIS_TITLE_COLOR ) );

                    // No ticks or labels on the axis
                    _chart.getXAxis().setMajorTicksVisible( false );
                    _chart.getXAxis().setMajorTickLabelsVisible( false );
                    _chart.getXAxis().setMinorTicksVisible( false );
                    _chart.getXAxis().setMinorTickLabelsVisible( false );

                    // Major ticks with labels below the chart
                    _chart.getHorizontalTicks().setMajorTicksVisible( true );
                    _chart.getHorizontalTicks().setMajorTickLabelsVisible( true );
                    _chart.getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                    _chart.getHorizontalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
                    _chart.getHorizontalTicks().setMajorTickFont( MAJOR_TICK_FONT );

                    // Symbolic labels at L/8 intervals
                    StringLabelTable labelTable = new StringLabelTable( apparatusPanel, MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
                    labelTable.put( -L, "-L" );
                    labelTable.put( -7 * L / 8, "-7L/8" );
                    labelTable.put( -3 * L / 4, "-3L/4" );
                    labelTable.put( -5 * L / 8, "-5L/8" );
                    labelTable.put( -L / 2, "-L/2" );
                    labelTable.put( -3 * L / 8, "-3L/8" );
                    labelTable.put( -L / 4, "-L/4" );
                    labelTable.put( -L / 8, "-L/8" );
                    labelTable.put( 0, "0" );
                    labelTable.put( L / 8, "L/8" );
                    labelTable.put( L / 4, "L/4" );
                    labelTable.put( 3 * L / 8, "3L/8" );
                    labelTable.put( L / 2, "L/2" );
                    labelTable.put( 5 * L / 8, "5L/8" );
                    labelTable.put( 3 * L / 4, "3L/4" );
                    labelTable.put( 7 * L / 8, "7L/8" );
                    labelTable.put( L, "L" );
                    _chart.getHorizontalTicks().setMajorLabels( labelTable );

                    // Vertical gridlines for major ticks.
                    _chart.getVerticalGridlines().setMajorGridlinesVisible( true );
                    _chart.getVerticalGridlines().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                    _chart.getVerticalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                    _chart.getVerticalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

                    // Vertical gridlines for minor ticks.
                    _chart.getVerticalGridlines().setMinorGridlinesVisible( true );
                    _chart.getVerticalGridlines().setMinorTickSpacing( X_MINOR_TICK_SPACING );
                    _chart.getVerticalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
                    _chart.getVerticalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
                }

                // Y axis
                {
                    _chart.getYAxis().setStroke( AXIS_STROKE );
                    _chart.getYAxis().setColor( AXIS_COLOR );

                    // No ticks or labels on the axis
                    _chart.getYAxis().setMajorTicksVisible( false );
                    _chart.getYAxis().setMajorTickLabelsVisible( false );
                    _chart.getYAxis().setMinorTicksVisible( false );
                    _chart.getYAxis().setMinorTickLabelsVisible( false );

                    // Major ticks with labels to the left of the chart
                    _chart.getVerticalTicks().setMajorTicksVisible( true );
                    _chart.getVerticalTicks().setMajorTickLabelsVisible( true );
                    _chart.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                    _chart.getVerticalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
                    _chart.getVerticalTicks().setMajorTickFont( MAJOR_TICK_FONT );

                    // Horizontal gridlines for major ticks
                    _chart.getHorizonalGridlines().setMajorGridlinesVisible( true );
                    _chart.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                    _chart.getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                    _chart.getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );
                }

                // Pre-populate the sine waves at harmonic intervals.
                _sinePlots = new SinePlot[MAX_WAVES];
                for ( int i = 0; i < _sinePlots.length; i++ ) {
                    SinePlot sinePlot = new SinePlot( apparatusPanel, _chart );
                    sinePlot.setAmplitude( 1 );
                    sinePlot.setPeriod( L / ( i + 1 ) );
                    sinePlot.setPixelsPerPoint( PIXELS_PER_POINT );
                    sinePlot.setBorderColor( new Color( 255 - i, 255 - i, 255 - i, 120 ) );
                    _sinePlots[i] = sinePlot;
                }
            }

            // Control Panel
            {
                ControlPanel controlPanel = new ControlPanel( this );
                setControlPanel( controlPanel );

                // Labeled slider
                {
                    JPanel panel = new JPanel();
                    controlPanel.addControl( panel );

                    panel.setBorder( new EtchedBorder() );
                    panel.setLayout( new BorderLayout() );

                    // Label that shows the current number of waves
                    _label = new JLabel();
                    panel.add( _label, BorderLayout.NORTH );

                    // Slider for changing the number of waves
                    _slider = new JSlider();
                    panel.add( _slider, BorderLayout.CENTER );
                    _slider.setMinimum( 0 );
                    _slider.setMaximum( MAX_WAVES );
                    _slider.setMajorTickSpacing( 50 );
                    _slider.setPaintTicks( true );
                    _slider.setPaintLabels( true );
                }

                // Color controls
                {
                    JPanel panel = new JPanel();
                    controlPanel.addControl( panel );
                    panel.setBorder( new EtchedBorder() );

                    _grayscaleButton = new JRadioButton( "Grayscale" );
                    panel.add( _grayscaleButton );
                    _colorButton = new JRadioButton( "Color" );
                    panel.add( _colorButton );

                    ButtonGroup buttonGroup = new ButtonGroup();
                    buttonGroup.add( _grayscaleButton );
                    buttonGroup.add( _colorButton );
                }

                // Zoom controls
                {
                    JPanel panel = new JPanel();
                    controlPanel.addControl( panel );

                    panel.setBorder( new TitledBorder( "Zoom" ) );

                    _zoomInButton = new JButton( "+" );
                    panel.add( _zoomInButton );
                    _zoomOutButton = new JButton( "-" );
                    panel.add( _zoomOutButton );
                }

                // Wire up event handling
                EventListener listener = new EventListener();
                _slider.addChangeListener( listener );
                _zoomInButton.addActionListener( listener );
                _zoomOutButton.addActionListener( listener );
                _colorButton.addActionListener( listener );
                _grayscaleButton.addActionListener( listener );
            }

            // Set the initial state
            _slider.setValue( 1 );
            _grayscaleButton.setSelected( true );
        }

        /**
         * Dispatches events to the appropriate handler method.
         */
        private class EventListener implements ActionListener, ChangeListener {

            public EventListener() {
            }

            public void actionPerformed( ActionEvent event ) {
                Object source = event.getSource();
                if ( source == _zoomInButton ) {
                    handleZoomIn();
                }
                else if ( source == _zoomOutButton ) {
                    handleZoomOut();
                }
                else if ( source == _colorButton ) {
                    handleColor();
                }
                else if ( source == _grayscaleButton ) {
                    handleGrayscale();
                }
            }

            public void stateChanged( ChangeEvent event ) {
                if ( event.getSource() == _slider ) {
                    int numberOfHarmonics = _slider.getValue();
                    // Update the label as the slider is dragged.
                    Object[] args = {new Integer( numberOfHarmonics )};
                    String s = MessageFormat.format( "Number of sine waves: {0}", args );
                    _label.setText( s );
                    // Update the chart when the slider is released.
                    if ( !_slider.getValueIsAdjusting() ) {
                        updateChart();
                    }
                }
            }
        }

        /**
         * Updates the chart to reflect the current state as set by the control panel.
         */
        private void updateChart() {
            // Clear the chart
            _chart.removeAllDataSetGraphics();

            int numberOfHarmonics = _slider.getValue();
            if ( numberOfHarmonics > 0 ) {
                // Populate the chart.
                double deltaWavelength = ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH ) / numberOfHarmonics;
                int deltaGrayscale = 225 / numberOfHarmonics;
                // Work backwards, so that the fundamental is in the foreground.
                for ( int i = numberOfHarmonics - 1; i >= 0; i-- ) {
                    // Set the wave's color
                    Color color = null;
                    if ( _colorEnabled ) {
                        double wavelength = VisibleColor.MAX_WAVELENGTH - ( i * deltaWavelength );
                        color = VisibleColor.wavelengthToColor( wavelength );
                    }
                    else {
                        int c = i * deltaGrayscale;
                        color = new Color( c, c, c );
                    }
                    _sinePlots[i].setBorderColor( color );
                    // Add the wave to the chart
                    _chart.addDataSetGraphic( _sinePlots[i] );
                }
            }
        }

        /**
         * Handles zoom in.
         */
        private void handleZoomIn() {
            setWaitCursorEnabled( true );

            // Adjust the chart range
            Range2D range = _chart.getRange();
            range.setMinX( range.getMinX() / ZOOM_FACTOR );
            range.setMaxX( range.getMaxX() / ZOOM_FACTOR );
            _chart.setRange( range );

            // Enable zoom buttons
            _zoomLevel++;
            _zoomInButton.setEnabled( _zoomLevel < MAX_ZOOM_LEVEL );
            _zoomOutButton.setEnabled( _zoomLevel > MIN_ZOOM_LEVEL );

            setWaitCursorEnabled( false );
        }

        /**
         * Handles zoom out.
         */
        private void handleZoomOut() {
            setWaitCursorEnabled( true );

            // Adjust the chart range
            Range2D range = _chart.getRange();
            range.setMinX( range.getMinX() * ZOOM_FACTOR );
            range.setMaxX( range.getMaxX() * ZOOM_FACTOR );
            _chart.setRange( range );

            // Enable zoom buttons
            _zoomLevel--;
            _zoomInButton.setEnabled( _zoomLevel < MAX_ZOOM_LEVEL );
            _zoomOutButton.setEnabled( _zoomLevel > MIN_ZOOM_LEVEL );

            setWaitCursorEnabled( false );
        }

        /**
         * Handles the color radio button.
         */
        private void handleColor() {
            setWaitCursorEnabled( true );
            _colorEnabled = true;
            updateChart();
            setWaitCursorEnabled( false );
        }

        /**
         * Handles the grayscale radio button.
         */
        private void handleGrayscale() {
            setWaitCursorEnabled( true );
            _colorEnabled = false;
            updateChart();
            setWaitCursorEnabled( false );
        }

        /**
         * Turns the wait cursor on and off.
         *
         * @param enabled
         */
        private void setWaitCursorEnabled( boolean enabled ) {
            if ( enabled ) {
                PhetApplication.instance().getPhetFrame().setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
            }
            else {
                PhetApplication.instance().getPhetFrame().setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
            }
        }
    }
}
