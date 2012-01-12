// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.PositionHistogramSnapshotDialog;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * PositionHistogramPanel is a panel that displays a histogram of the bead's position.
 * The top panel contains control buttons.
 * The bottom panel contains a histogram, ruler, and various display values.
 * While running, the histogram makes observations of the bead's position,
 * relative to the laser's position.
 * When the laser is moved, the histogram data is cleared.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositionHistogramPanel extends JPanel implements Observer {
    
    /*
     * ZoomLevel is a data structure that describes a zoom level.
     * The zoom level affects the histogram's x-axis, the bin width, and the layout of the ruler.
     */
    private static class ZoomLevel {
        
        public final double positionRange;
        public final double binWidth;
        public final int rulerMax;
        public final int rulerMajorTickSpacing;
        public final double rulerMinorTickSpacing;
        
        public ZoomLevel( double positionRange, double binWidth, int rulerMax, int rulerMajorTickSpacing, double rulerMinorTickSpacing ) {
            if ( ! ( positionRange > 0 ) ) {
                throw new IllegalArgumentException( "positionRange must be > 0" );
            }
            if ( ! ( binWidth > 0 ) ) {
                throw new IllegalArgumentException( "binWidth must be > 0" );
            }
            if ( rulerMax % rulerMajorTickSpacing != 0 ) {
                throw new IllegalArgumentException( "rulerMax must be an integer multiple of rulerMajorTickSpacing" );
            }
            if ( ! (rulerMajorTickSpacing > rulerMinorTickSpacing ) ) {
                throw new IllegalArgumentException( "rulerMajorTickSpacing must be > rulerMinorTickSpacing" );
            }
            if ( rulerMajorTickSpacing % rulerMinorTickSpacing != 0 ) {
                throw new IllegalArgumentException( "rulerMajorTickSpacing must an integer multiple of rulerMinorTickSpacing" );
            }
            this.positionRange = positionRange;
            this.binWidth = binWidth;
            this.rulerMax = rulerMax;
            this.rulerMajorTickSpacing = rulerMajorTickSpacing;
            this.rulerMinorTickSpacing = rulerMinorTickSpacing;
        }
    }
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // determines whether the chart is making observations when it's created
    private static final boolean DEFAULT_IS_RUNNING = true;
    // size of the chart
    private static final Dimension CHART_SIZE = new Dimension( 700, 150 );
    // normal background color for the chart and plot
    private static final Color CHART_BACKGROUND_COLOR = Color.WHITE;
    // background color used for snapshots
    private static final Color SNAPSHOT_BACKGROUND_COLOR = new Color( 225, 225, 225 );  // light gray
    
    // The zoom levels available using the zoom buttons
    private static final ZoomLevel[] ZOOM_LEVELS = {
        // positionRange, binWidth, rulerMax, rulerMajorTickSpacing, rulerMinorTickSpacing
        new ZoomLevel( 52, 1, 75, 5, 1 ),
        new ZoomLevel( 105, 2, 150, 10, 2 ),
        new ZoomLevel( 160, 3, 225, 25, 5 ),
        new ZoomLevel( 210, 5, 300, 25, 5 ),
        new ZoomLevel( 260, 5, 375, 25, 5 ),
        new ZoomLevel( 320, 7.5, 450, 50, 5 )
    };
    private static final int DEFAULT_ZOOM_INDEX = 2;
    
    // Format of the bin width display
    private static final DecimalFormat BIN_WIDTH_FORMAT = new DecimalFormat( "0.0#" );
    
    // How far to offset each snapshot dialog when setting the dialog's position
    private static final int SNAPSOT_DIALOG_OFFEST = 10; // pixels
    
    // Ruler properties
    private static final double RULER_HEIGHT = 30;
    private static final int RULER_FONT_SIZE = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock; // an observation is made each time this clock ticks
    private ClockListener _clockListener; // listens for clock ticks
    private Bead _bead; // bead whose positiom we're observing
    private Laser _laser; // histogram is reset whenever the laser moves

    // control buttons that appear in the tool (upper) panel
    private JButton _startStopButton;
    private JButton _clearButton;
    private JButton _zoomInButton;
    private JButton _zoomOutButton;
    private JButton _snapshotButton;
    private JButton _rulerButton;

    // stuff that appears in the chart (lower) panel
    private PositionHistogramPlot _plot;
    private PText _measurementsNode;  // displays the number of measurements
    private PText _binWidthNode; // displays the bin width
    private PClip _snapshotClipNode; // all children of this node are included in snapshots
    private JFreeChart _chart;
    private JFreeChartNode _chartNode;

    // localized strings
    private String _measurementsString;
    private String _startString, _stopString;
    private String _binWidthString;
    private String _unitsString;
    private String _positionHistogramSnapshotTitle;

    private boolean _isRunning; // is the histogram making observations?
    private int _numberOfMeasurements;
    private int _zoomIndex; // the current index into ZOOM_LEVELS

    private Dialog _snapshotDialogOwner; // owner of the snapshot dialogs
    private ArrayList _snapshotDialogs; // list of PositionHistogramSnapshotDialog
    private int _numberOfSnapshots; // number of snapshots, used to number the snapshots
    
    private RulerNode _rulerNode; // the ruler
    private PNode _rulerParentNode; // the ruler's parent
    private PNode _rulerDragBoundsNode; // defines the ruler's drag bounds
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param font font to used for all controls
     * @param bead
     * @param laser
     */
    public PositionHistogramPanel( Dialog snapshotDialogOwner, Font font, IClock clock, Bead bead, Laser laser ) {
        super();
        
        _snapshotDialogOwner = snapshotDialogOwner;

        _clock = clock;
        _clockListener = new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if ( _isRunning ) {
                    handleClockEvent( event );
                }
            }
            public void simulationTimeReset( ClockEvent clockEvent ) {
                // clear the histogram if the clock is reset
                clearMeasurements();
            }
        };
        _clock.addClockListener( _clockListener );

        _bead = bead;

        _laser = laser;
        _laser.addObserver( this );

        _isRunning = DEFAULT_IS_RUNNING;
        _numberOfMeasurements = 0;
        _snapshotDialogs =  new ArrayList();
        _numberOfSnapshots = 0;
        
        initStrings();

        JPanel toolPanel = createToolPanel( font );

        JPanel chartPanel = createChartPanel( font, bead, laser );

        setLayout( new BorderLayout() );
        add( toolPanel, BorderLayout.NORTH );
        add( chartPanel, BorderLayout.CENTER );
        
        setZoomIndex( DEFAULT_ZOOM_INDEX );
    }
    
    /**
     * Call this method before releasing all references to an object of this type.
     */
    public void cleanup() {
        
        _clock.removeClockListener( _clockListener );
        _laser.deleteObserver( this );
        
        // Close all snapshot dialogs
        Iterator i = _snapshotDialogs.iterator();
        while ( i.hasNext() ) {
            ((JDialog)i.next()).dispose();
        }
        _snapshotDialogs.clear();
    }

    //----------------------------------------------------------------------------
    // User interface creation
    //----------------------------------------------------------------------------
    
    /*
     * Initialize localized strings.
     */
    private void initStrings() {
        _measurementsString = OTResources.getString( "label.measurements" );
        _startString = OTResources.getString( "button.start" );
        _stopString = OTResources.getString( "button.stop" );
        _binWidthString = OTResources.getString( "label.binWidth" );
        _unitsString = OTResources.getString( "units.position" );
        _positionHistogramSnapshotTitle = OTResources.getString( "title.positionHistogramSnapshot" );
    }
    
    /*
     * Creates the tool panel that appears at the top of the dialog.
     */
    private JPanel createToolPanel( Font font ) {

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
        _zoomInButton.setEnabled( _zoomIndex != 0 );
        
        Icon zoomOutIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_ZOOM_OUT ) );
        _zoomOutButton = new JButton( zoomOutIcon );
        _zoomOutButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomOutButton();
            }
        } );
        _zoomOutButton.setEnabled( _zoomIndex != ZOOM_LEVELS.length - 1 );

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
     * Creates the Piccolo panel that contains the histogram, ruler, etc.
     */
    private JPanel createChartPanel( Font font, Bead bead, Laser laser ) {

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( CHART_SIZE );
        
        _snapshotClipNode = new PClip();
        _snapshotClipNode.setPathTo( new Rectangle2D.Double( 0, 0, CHART_SIZE.width, CHART_SIZE.height ) );
        _snapshotClipNode.setPaint( null );
        _snapshotClipNode.setStroke( null );
        _snapshotClipNode.setOffset( 0, 0 );
        
        _plot = new PositionHistogramPlot();
        _plot.setBackgroundPaint( CHART_BACKGROUND_COLOR );
        
        // add a vertical marker at position=0
        Marker originMarker = new ValueMarker( 0 );
        originMarker.setLabel("");
        originMarker.setPaint( OTConstants.ORIGIN_MARKER_COLOR );
        originMarker.setStroke( OTConstants.ORIGIN_MARKER_STROKE );
        _plot.addDomainMarker(originMarker);
        
        _chart = new JFreeChart( null /* title */, null /* titleFont */, _plot, false /* createLegend */);
        _chart.setAntiAlias( true );
        _chart.setBorderVisible( true );
        _chart.setBackgroundPaint( CHART_BACKGROUND_COLOR );
        
        _chartNode = new JFreeChartNode( _chart );
        _chartNode.setPickable( false );
        _chartNode.setChildrenPickable( false );
        _chartNode.setOffset( 0, 0 );
        _chartNode.setBounds( 0, 0, CHART_SIZE.width, CHART_SIZE.height );
        _chartNode.updateChartRenderingInfo();
        
        _measurementsNode = new PText( "?" );
        _measurementsNode.setOffset( 10, 10 );
        _measurementsNode.setPickable( false );
        setNumberOfMeasurements( _numberOfMeasurements );
        
        _binWidthNode = new PText();
        _binWidthNode.setOffset( _measurementsNode.getXOffset(), _measurementsNode.getFullBounds().getMaxY() + 3 );
        _binWidthNode.setPickable( false );
        
        _rulerParentNode = new PNode();
        _rulerParentNode.setOffset( 0, 0 );

        // Layering
        _snapshotClipNode.addChild( _chartNode );
        _snapshotClipNode.addChild( _measurementsNode );
        _snapshotClipNode.addChild( _binWidthNode );
        _snapshotClipNode.addChild( _rulerParentNode );
        canvas.getLayer().addChild( _snapshotClipNode );
        
        JPanel panel = new JPanel();
        panel.add( canvas );

        return panel;
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    /**
     * Converts the histogram chart to an image.
     * This is used to create snapshots of the histogram.
     * 
     * @return Image
     */
    public Image getSnapshotImage() {
        return _snapshotClipNode.toImage();
    }
    
    /**
     * Sets the zoom index.
     * 
     * @param zoomIndex
     */
    public void setZoomIndex( int zoomIndex ) {
        if ( zoomIndex < 0 || zoomIndex > ZOOM_LEVELS.length - 1 ) {
            throw new IndexOutOfBoundsException( "zoomIndex out of bounds: " + zoomIndex );
        }
        _zoomIndex = zoomIndex;
        ZoomLevel zoomLevel = ZOOM_LEVELS[_zoomIndex];
        // Update the histogram's x-axis range
        _plot.setPositionRange( -zoomLevel.positionRange, zoomLevel.positionRange, zoomLevel.binWidth );
        // Update the bin width display
        _binWidthNode.setText( _binWidthString + " " + BIN_WIDTH_FORMAT.format( zoomLevel.binWidth ) + " " + _unitsString );
        // Enable/disable zoom buttons
        _zoomInButton.setEnabled( _zoomIndex != 0 );
        _zoomOutButton.setEnabled( _zoomIndex != ZOOM_LEVELS.length - 1 );
        // Update the ruler
        updateRuler();
    }
    
    /**
     * Gets the zoom index.
     * 
     * @return int
     */
    public int getZoomIndex() {
        return _zoomIndex;
    }
    
    /**
     * Sets the visibility of the ruler.
     * 
     * @param visible
     */
    public void setRulerVisible( boolean visible ) {
        updateRuler();
        _rulerNode.setVisible( visible );
    }
    
    /**
     * Is the ruler visible?
     * 
     * @return true or false
     */
    public boolean isRulerVisible() {
        return ( _rulerNode != null && _rulerNode.isVisible() );
    }
    
    //----------------------------------------------------------------------------
    // private setters
    //----------------------------------------------------------------------------

    /*
     * Sets the number of measurements displayed.
     */
    private void setNumberOfMeasurements( int numberOfMeasurements ) {
        _numberOfMeasurements = numberOfMeasurements;
        _measurementsNode.setText( _measurementsString + " " + String.valueOf( _numberOfMeasurements ) );
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
        int newZoomIndex = _zoomIndex - 1;
        setZoomIndex( newZoomIndex );
    }
    
    private void handleZoomOutButton() {
        int newZoomIndex = _zoomIndex + 1;
        setZoomIndex( newZoomIndex );
    }
    
    private void handleSnapshotButton() {
        // Change the background color for the snapshot
        _plot.setBackgroundPaint( SNAPSHOT_BACKGROUND_COLOR );
        _chart.setBackgroundPaint( SNAPSHOT_BACKGROUND_COLOR );
        // Create a snapshot dialog and add it to the list
        String title = _positionHistogramSnapshotTitle + " " + (++_numberOfSnapshots);
        JDialog snapshotDialog = new PositionHistogramSnapshotDialog( _snapshotDialogOwner, title, this );
        _snapshotDialogs.add( snapshotDialog );
        // Position the dialog below the main histogram dialog
        final int offset = ( _numberOfSnapshots * SNAPSOT_DIALOG_OFFEST ) % 200;
        int x = (int)_snapshotDialogOwner.getLocation().getX() + offset;
        int y = (int)_snapshotDialogOwner.getLocation().getY() + (int)_snapshotDialogOwner.getHeight() + offset;
        snapshotDialog.setLocation( x, y );
        // Show the dialog
        snapshotDialog.show();
        // Restore the background color
        _plot.setBackgroundPaint( CHART_BACKGROUND_COLOR );
        _chart.setBackgroundPaint( CHART_BACKGROUND_COLOR );
    }

    private void handleRulerButton() {
        _rulerNode.setVisible( !_rulerNode.isVisible() );
    }
    
    /*
     * Updates the ruler to match the current zoom level.
     */
    private void updateRuler() {
        
        // delete current ruler
        boolean rulerVisible = false;
        if ( _rulerNode != null ) {
            rulerVisible = _rulerNode.isVisible();
            _rulerParentNode.removeAllChildren();
            _rulerNode = null;
            _rulerDragBoundsNode = null;
        }
        
        // ruler length
        ZoomLevel zoomLevel = ZOOM_LEVELS[_zoomIndex];
        double distanceBetweenFirstAndLastTick = getRulerNodeLength( zoomLevel.rulerMax ); 
        
        // major tick labels
        int numberOfMajorTickLabels = ( zoomLevel.rulerMax / zoomLevel.rulerMajorTickSpacing ) + 1;
        int majorTickDelta = zoomLevel.rulerMax / ( numberOfMajorTickLabels - 1 );
        String[] majorTickLabels = new String[ numberOfMajorTickLabels ];
        for ( int i = 0; i < numberOfMajorTickLabels; i++ ) {
            majorTickLabels[i] = String.valueOf( i * majorTickDelta );
        }
        
        // minor ticks
        int numMinorTicksBetweenMajors = (int)( zoomLevel.rulerMajorTickSpacing / zoomLevel.rulerMinorTickSpacing ) - 1;
        
        // create the ruler
        _rulerNode = new RulerNode( distanceBetweenFirstAndLastTick, RULER_HEIGHT, majorTickLabels, _unitsString, numMinorTicksBetweenMajors, RULER_FONT_SIZE );
        _rulerParentNode.addChild( _rulerNode );
        _rulerNode.addInputEventListener( new CursorHandler() );
        _rulerNode.setBackgroundPaint( OTConstants.RULER_COLOR );
        
        // constraint the ruler's drag bounds
        final int minPixelsVisible = 20;
        PBounds snapshotBounds = _snapshotClipNode.getFullBoundsReference();
        PBounds rulerBounds = _rulerNode.getFullBoundsReference();
        double x = snapshotBounds.getX() - rulerBounds.getWidth() + minPixelsVisible;
        double y = snapshotBounds.getY();
        double w = snapshotBounds.getWidth() + ( 2 * rulerBounds.getWidth() ) - ( 2 * minPixelsVisible );
        double h = snapshotBounds.getHeight();
        _rulerDragBoundsNode = new PPath( new Rectangle2D.Double( x, y, w,h ) );
        _rulerParentNode.addChild( _rulerDragBoundsNode );
        _rulerNode.addInputEventListener( new BoundedDragHandler( _rulerNode, _rulerDragBoundsNode ) );
        
        // Center the ruler
        PBounds chartBounds = _chartNode.getFullBoundsReference();
        double xOffset = ( chartBounds.getWidth() - rulerBounds.getWidth() ) / 2;
        double yOffset = ( chartBounds.getHeight() - rulerBounds.getHeight() ) / 2;
        _rulerNode.setOffset( xOffset, yOffset );
        
        _rulerNode.setVisible( rulerVisible );
    }
    
    /*
     * Given a ruler length in model coordinates,
     * gets the ruler length in view (Piccolo) coordinates.
     */
    private double getRulerNodeLength( double rulerModelLength ) {
        // chart coordinates
        Point2D cp0 = _chartNode.plotToNode( new Point2D.Double( 0, 0 ) );
        Point2D cpMax = _chartNode.plotToNode( new Point2D.Double( rulerModelLength, 0 ) );
        // global coordinates
        Point2D gp0 = _chartNode.localToGlobal( cp0 );
        Point2D gpMax = _chartNode.localToGlobal( cpMax );
        // ruler coordinates
        Point2D rp0 = _rulerParentNode.globalToLocal( gp0 );
        Point2D rpMax = _rulerParentNode.globalToLocal( gpMax );
        return rpMax.getX() - rp0.getX();
    }

    /*
     * Makes a position measurement and updates the plot.
     */
    private void handleClockEvent( ClockEvent event ) {
        setNumberOfMeasurements( _numberOfMeasurements + 1 );
        double xOffset = _bead.getPositionReference().getX() - _laser.getPositionReference().getX();
        _plot.addPosition( xOffset );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Clears the histogram when the laser is moved.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _laser && arg == Laser.PROPERTY_POSITION ) {
            clearMeasurements();
        }
    }
}
