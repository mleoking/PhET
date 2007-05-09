/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.CloseButtonNode;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.view.node.ModelViewTransform;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;


public class PositionHistogramChartNode extends PhetPNode {

    public static final double DEFAULT_HEIGHT = 100;
    
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 255, 200 ); // translucent white
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Stroke BORDER_STROKE = new BasicStroke( 6f );
    private static final Font TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    private static final Color TITLE_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 14 );
    private static final Color LABEL_COLOR = Color.BLACK;

    private PositionHistogramPlot _plot;
    private Bead _bead;
    private IClock _clock;
    private ClockListener _clockListener;
    private ModelViewTransform _modelViewTransform;

    private boolean _isRunning;
    private int _numberOfMeasurements;

    private PPath _borderNode;
    private PPath _controlsBackgroundNode;
    private JFreeChartNode _chartWrapper;
    private PText _measurementsLabel;
    private PText _titleLabel;
    private PSwing _startStopButtonWrapper;
    private PSwing _clearButtonWrapper;
    private JButton _startStopButton;
    private String _startString;
    private String _stopString;
    private String _measurementsString;
    private CloseButtonNode _closeButtonNode;

    public PositionHistogramChartNode( Bead bead, IClock clock, ModelViewTransform modelViewTransform ) {
        super();

        _plot = new PositionHistogramPlot();
        _bead = bead;
        _clock = clock;
        _modelViewTransform = modelViewTransform;
        
        _isRunning = false;
        _numberOfMeasurements = 0;
        
        _clockListener = new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if ( _isRunning ) {
                    handleClockEvent( event );
                }
            }
        };
        _clock.addClockListener( _clockListener );

        _borderNode = new PPath();
        _borderNode.setPaint( null );
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );

        _controlsBackgroundNode = new PPath();
        _controlsBackgroundNode.setPaint( BACKGROUND_COLOR );
        _controlsBackgroundNode.setStroke( null );

        _measurementsString = OTResources.getString( "label.measurements" );
        _measurementsLabel = new PText();
        _measurementsLabel.setFont( LABEL_FONT );
        _measurementsLabel.setTextPaint( LABEL_COLOR );

        _titleLabel = new PText( OTResources.getString( "title.positionHistogram" ) );
        _titleLabel.setFont( TITLE_FONT );
        _titleLabel.setTextPaint( TITLE_COLOR );

        _startString = OTResources.getString( "button.start" );
        _stopString = OTResources.getString( "button.stop" );
        _startStopButton = new JButton( _isRunning ? _stopString : _startString );
        _startStopButton.setOpaque( false );
        _startStopButton.setFont( LABEL_FONT );
        _startStopButton.setForeground( LABEL_COLOR );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                setRunning( !_isRunning );
            }
        } );
        _startStopButtonWrapper = new PSwing( _startStopButton );

        JButton clearButton = new JButton( OTResources.getString( "button.clear" ) );
        clearButton.setOpaque( false );
        clearButton.setFont( LABEL_FONT );
        clearButton.setForeground( LABEL_COLOR );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                clearMeasurements();
            }
        } );
        _clearButtonWrapper = new PSwing( clearButton );

        _closeButtonNode = new CloseButtonNode();
        _closeButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
            }
        });

        JFreeChart chart = new JFreeChart( null /* title */, null /* titleFont */, _plot, false /* createLegend */ );
        chart.setAntiAlias( true );
        chart.setBorderVisible( false );
        chart.setBackgroundPaint( BACKGROUND_COLOR );
        chart.setPadding( new RectangleInsets( 0, 5, 5, 5 ) ); // top,left,bottom,right
        _chartWrapper = new JFreeChartNode( chart );

        // Layering order
        addChild( _controlsBackgroundNode );
        addChild( _measurementsLabel );
        addChild( _titleLabel );
        addChild( _startStopButtonWrapper );
        addChild( _clearButtonWrapper );
        addChild( _closeButtonNode );
        addChild( _chartWrapper );
        addChild( _borderNode );

        // Only buttons should be pickable
        setPickable( false );
        _controlsBackgroundNode.setPickable( false );
        _measurementsLabel.setPickable( false );
        _titleLabel.setPickable( false );
        _chartWrapper.setPickable( false );
        _borderNode.setPickable( false );

        setNumberOfMeasurements( 0 );
        updateLayout();
    }

    public void cleanup() {
        _clock.removeClockListener( _clockListener );
    }

    public void setChartSize( double w, double h ) {
        // resize the chart
        _chartWrapper.setBounds( 0, 0, w, h );
        _chartWrapper.updateChartRenderingInfo();
        // update the layout of this node
        updateLayout();
        // update the range of the x-axis
        Rectangle2D dataArea = getPlotBounds();
        double minPosition = _modelViewTransform.viewToModel( dataArea.getMinX() );
        double maxPosition = _modelViewTransform.viewToModel( dataArea.getMaxX() );
        _plot.setPositionRange( minPosition, maxPosition );
    }

    /**
     * When the chart is made invisible, clear it and stop making measurements.
     * 
     * @param visible true or false
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( !visible ) {
            clearMeasurements();
            setRunning( false );
        }
    }

    /**
     * Gets the bounds of the associated chart's plot.
     * The bounds are in the node's local coordinates.
     * 
     * @return Rectangle2D
     */
    public Rectangle2D getPlotBounds() {
        ChartRenderingInfo chartInfo = _chartWrapper.getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = plotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }

    /*
     * Sets the number of measurements displayed.
     */
    private void setNumberOfMeasurements( int numberOfMeasurements ) {
        _numberOfMeasurements = numberOfMeasurements;
        _measurementsLabel.setText( _measurementsString + String.valueOf( _numberOfMeasurements ) );
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
        _plot.clear();
        setNumberOfMeasurements( 0 );
    }

    /*
     * Makes a position measurement and updates the plot.
     */
    private void handleClockEvent( ClockEvent event ) {
        setNumberOfMeasurements( _numberOfMeasurements + 1 );
        double x = _bead.getPositionRef().getX();
        _plot.addPosition( x );
    }

    /*
     * Updates the layout when the bounds change.
     */
    private void updateLayout() {

        final double horizMargin = 15;
        final double vertMargin = 8;
        final double buttonSpacing = 5;

        final double maxWidth = _chartWrapper.getFullBounds().getWidth();
        double maxHeight = Math.max( _measurementsLabel.getFullBounds().getHeight(), _titleLabel.getFullBounds().getHeight() );
        maxHeight = Math.max( maxHeight, _startStopButtonWrapper.getFullBounds().getHeight() );
        maxHeight = Math.max( maxHeight, _clearButtonWrapper.getFullBounds().getHeight() );
        maxHeight = Math.max( maxHeight, _closeButtonNode.getFullBounds().getHeight() );
        maxHeight += vertMargin;

        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;

        // measurements display: left edge, vertically centered
        x = horizMargin;
        y = ( maxHeight - _measurementsLabel.getFullBounds().getHeight() ) / 2;
        _measurementsLabel.setOffset( x, y );

        // title: vertically and horizontally centered
        x = ( maxWidth - _titleLabel.getFullBounds().getWidth() ) / 2;
        y = ( maxHeight - _titleLabel.getFullBounds().getHeight() ) / 2;
        _titleLabel.setOffset( x, y );

        // close button: right edge, vertically centered
        x = maxWidth - horizMargin - _closeButtonNode.getFullBounds().getWidth();
        y = ( maxHeight - _closeButtonNode.getFullBounds().getHeight() ) / 2;
        _closeButtonNode.setOffset( x, y );

        // clear button: right edge, vertically centered
        x = _closeButtonNode.getOffset().getX() - buttonSpacing - _clearButtonWrapper.getFullBounds().getWidth();
        y = ( maxHeight - _clearButtonWrapper.getFullBounds().getHeight() ) / 2;
        _clearButtonWrapper.setOffset( x, y );

        // start/stop button: to left of Clear button, vertically centered
        x = _clearButtonWrapper.getOffset().getX() - buttonSpacing - _startStopButtonWrapper.getFullBounds().getWidth();
        y = ( maxHeight - _startStopButtonWrapper.getFullBounds().getHeight() ) / 2;
        _startStopButtonWrapper.setOffset( x, y );

        // chart: below all of the controls
        x = 0;
        y = maxHeight;
        _chartWrapper.setOffset( x, y );

        // size the background to fit the controls
        x = 0;
        y = 0;
        w = maxWidth;
        h = maxHeight;
        _controlsBackgroundNode.setPathTo( new Rectangle2D.Double( x, y, w, h ) );

        // size the border to fit around everything
        x = 0;
        y = 0;
        w = maxWidth;
        h = maxHeight + _chartWrapper.getFullBounds().getHeight();
        _borderNode.setPathTo( new Rectangle2D.Double( x, y, w, h ) );
    }
}
