/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import org.jfree.chart.JFreeChart;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.umd.cs.piccolo.nodes.PText;


public class PositionHistogramPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension CHART_SIZE = new Dimension( 700, 150 );
    private static final Color CHART_BACKGROUND_COLOR = Color.WHITE;
    
    private static final DoubleRange POSITION_RANGE = new DoubleRange( -150, 150 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock;
    private ClockListener _clockListener;
    private Bead _bead;
    private Laser _laser;

    private JLabel _measurementsLabel;
    private JButton _startStopButton;
    private JButton _clearButton;
    private JButton _zoomInButton;
    private JButton _zoomOutButton;
    private JButton _snapshotButton;
    private JButton _rulerButton;

    private PositionHistogramPlot _plot;

    private String _measurementsString;
    private String _startString, _stopString;

    private boolean _isRunning;
    private int _numberOfMeasurements;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param font font to used for all controls
     * @param bead
     * @param laser
     * @param binWidth
     */
    public PositionHistogramPanel( Font font, IClock clock, Bead bead, Laser laser, double binWidth ) {
        super();

        _clock = clock;
        _clockListener = new ClockAdapter() {

            public void clockTicked( ClockEvent event ) {
                if ( _isRunning ) {
                    handleClockEvent( event );
                }
            }
        };
        _clock.addClockListener( _clockListener );

        _bead = bead;

        _laser = laser;
        _laser.addObserver( this );

        _isRunning = false;
        _numberOfMeasurements = 0;

        JPanel toolPanel = createToolPanel( font );

        JPanel chartPanel = createChartPanel( font, bead, laser, binWidth );

        setLayout( new BorderLayout() );
        add( toolPanel, BorderLayout.NORTH );
        add( chartPanel, BorderLayout.CENTER );
    }
    
    public void cleanup() {
        System.err.println( "implement PositionHistogramPanel.cleanup!" );//XXX
    }

    //----------------------------------------------------------------------------
    // User interface creation
    //----------------------------------------------------------------------------
    
    /*
     * Creates the tool panel that appears at the top of the dialog.
     */
    private JPanel createToolPanel( Font font ) {

        _measurementsString = OTResources.getString( "label.measurements" );
        _startString = OTResources.getString( "button.start" );
        _stopString = OTResources.getString( "button.stop" );

        _measurementsLabel = new JLabel( _measurementsString );
        _measurementsLabel.setFont( font );

        _startStopButton = new JButton( _isRunning ? _stopString : _startString );
        _startStopButton.setFont( font );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleStartStopButton();
            }
        } );
        
        _clearButton = new JButton( OTResources.getString( "button.clear" ) );
        _clearButton.setFont( font );
        _clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                clearMeasurements();
            }
        } );
        
        Icon zoomInIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_ZOOM_IN ) );
        _zoomInButton = new JButton( zoomInIcon );
        _zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomInButton();
            }
        } );
        
        Icon zoomOutIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_ZOOM_OUT ) );
        _zoomOutButton = new JButton( zoomOutIcon );
        _zoomOutButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomOutButton();
            }
        } );

        Icon cameraIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_CAMERA ) );
        _snapshotButton = new JButton( cameraIcon );
        _snapshotButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleSnapshotButton();
            }
        } );

        Icon rulerIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_RULER ) );
        _rulerButton = new JButton( rulerIcon );
        _rulerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRulerButton();
            }
        } );
        
        // Layout
        JPanel toolPanel = new JPanel();
        {
            EasyGridBagLayout toolLayout = new EasyGridBagLayout( toolPanel );
            toolPanel.setLayout( toolLayout );
            toolLayout.setFill( GridBagConstraints.HORIZONTAL );
            int row = 0;
            int column = 0;
            toolLayout.addComponent( _measurementsLabel, row, column++, 1, 1, GridBagConstraints.WEST );
            toolLayout.addComponent( Box.createHorizontalStrut( 75 ), row, column++, 1, 1, GridBagConstraints.WEST );
            toolLayout.addComponent( _startStopButton, row, column++, 1, 1, GridBagConstraints.EAST );
            toolLayout.addComponent( _clearButton, row, column++, 1, 1, GridBagConstraints.EAST );
            toolLayout.addComponent( _zoomInButton, row, column++, 1, 1, GridBagConstraints.EAST );
            toolLayout.addComponent( _zoomOutButton, row, column++, 1, 1, GridBagConstraints.EAST );
            toolLayout.addComponent( _snapshotButton, row, column++, 1, 1, GridBagConstraints.EAST );
            toolLayout.addComponent( _rulerButton, row, column++, 1, 1, GridBagConstraints.EAST );
        }

        return toolPanel;
    }

    /*
     * Creates the Piccolo panel that contains the chart, ruler, etc.
     */
    private JPanel createChartPanel( Font font, Bead bead, Laser laser, double binWidth ) {

        _plot = new PositionHistogramPlot( binWidth );
        _plot.setPositionRange( POSITION_RANGE.getMin(), POSITION_RANGE.getMax() );

        JFreeChart chart = new JFreeChart( null /* title */, null /* titleFont */, _plot, false /* createLegend */);
        chart.setAntiAlias( true );
        chart.setBorderVisible( true );
        chart.setBackgroundPaint( CHART_BACKGROUND_COLOR );
        
        JFreeChartNode chartNode = new JFreeChartNode( chart );
        chartNode.setPickable( false );
        chartNode.setChildrenPickable( false );
        chartNode.setBounds( 0, 0, CHART_SIZE.width, CHART_SIZE.height );
        chartNode.updateChartRenderingInfo();

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.getLayer().addChild( chartNode );
        canvas.setPreferredSize( CHART_SIZE );
        
        JPanel panel = new JPanel();
        panel.add( canvas );

        return panel;
    }

    //----------------------------------------------------------------------------
    // private setters
    //----------------------------------------------------------------------------

    /*
     * Sets the number of measurements displayed.
     */
    private void setNumberOfMeasurements( int numberOfMeasurements ) {
        _numberOfMeasurements = numberOfMeasurements;
        _measurementsLabel.setText( _measurementsString + " " + String.valueOf( _numberOfMeasurements ) );
    }

    /*
     * Sets the running state of the chart.
     */
    private void setRunning( boolean isRunning ) {
        _isRunning = isRunning;
        if ( _isRunning ) {
            _clock.addClockListener( _clockListener );
            _startStopButton.setText( _stopString );
        }
        else {
            _clock.removeClockListener( _clockListener );
            _startStopButton.setText( _startString );
        }
    }

    /*
     * Clears all measurements from the chart.
     */
    private void clearMeasurements() {
        if ( _numberOfMeasurements > 0 ) {
            _plot.clear();
            setNumberOfMeasurements( 0 );
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    private void handleStartStopButton() {
        setRunning( !_isRunning );
    }

    private void handleZoomInButton() {
        System.out.println( "PositionHistogramPanel.handleZoomInButton" );//XXX
    }
    
    private void handleZoomOutButton() {
        System.out.println( "PositionHistogramPanel.handleZoomOutButton" );//XXX
    }
    
    private void handleSnapshotButton() {
        System.out.println( "PositionHistogramPanel.handleSnapshotButton" );//XXX
    }

    private void handleRulerButton() {
        System.out.println( "PositionHistogramPanel.handleRulerButton" );//XXX
    }

    /*
     * Makes a position measurement and updates the plot.
     */
    private void handleClockEvent( ClockEvent event ) {
        setNumberOfMeasurements( _numberOfMeasurements + 1 );
        double xOffset = _bead.getPositionRef().getX() - _laser.getPosition().getX();
        _plot.addPosition( xOffset );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    public void update( Observable o, Object arg ) {
        if ( o == _laser && arg == Laser.PROPERTY_POSITION ) {
            clearMeasurements();
        }
    }
}
