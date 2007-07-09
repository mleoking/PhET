/* Copyright 2007, University of Colorado */

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
import edu.umd.cs.piccolox.nodes.PComposite;


public class PositionHistogramPanel extends JPanel implements Observer {
    
    private static class ZoomSpec {
        public final double positionRange;
        public final double binWidth;
        public final int rulerMax;
        public final int rulerMajorTickSpacing;
        public final double rulerMinorTickSpacing;
        public ZoomSpec( double positionRange, double binWidth, int rulerMax, int rulerMajorTickSpacing, double rulerMinorTickSpacing ) {
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
    
    private static final boolean DEFAULT_IS_RUNNING = true;
    private static final Dimension CHART_SIZE = new Dimension( 700, 150 );
    private static final Color CHART_BACKGROUND_COLOR = Color.WHITE;
    
    private static final ZoomSpec[] ZOOM_SPECS = {
        // positionRange, binWidth, rulerMax, rulerMajorTickSpacing, rulerMinorTickSpacing
        new ZoomSpec( 50, 0.25, 75, 5, 1 ),
        new ZoomSpec( 100, 0.5, 150, 10, 5 ),
        new ZoomSpec( 150, 0.75, 250, 50, 5),
        new ZoomSpec( 200, 1.0, 300, 50, 10 ),
        new ZoomSpec( 250, 1.5, 350, 50, 10 ),
        new ZoomSpec( 300, 2.0, 400, 50, 10 )
    };
    private static final int DEFAULT_ZOOM_INDEX = 2;
    
    private static final DecimalFormat BIN_WIDTH_FORMAT = new DecimalFormat( "0.0#" );
    
    public static final Color ORIGIN_MARKER_COLOR = Color.BLACK;
    public static final Stroke ORIGIN_MARKER_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,6}, 0 ); // dashed
 
    private static final int SNAPSOT_DIALOG_OFFEST = 10; // pixels
    
    private static final double RULER_HEIGHT = 30;
    private static final int RULER_FONT_SIZE = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock;
    private ClockListener _clockListener;
    private Bead _bead;
    private Laser _laser;

    private JButton _startStopButton;
    private JButton _clearButton;
    private JButton _zoomInButton;
    private JButton _zoomOutButton;
    private JButton _snapshotButton;
    private JButton _rulerButton;

    private PositionHistogramPlot _plot;
    private PText _measurementsNode;
    private PText _binWidthNode;
    private PNode _snapshotNode;
    private JFreeChartNode _chartNode;

    private String _measurementsString;
    private String _startString, _stopString;
    private String _binWidthString;
    private String _unitsString;
    private String _positionHistogramSnapshotTitle;

    private boolean _isRunning;
    private int _numberOfMeasurements;
    private int _zoomIndex;

    private Dialog _snapshotDialogOwner;
    private ArrayList _snapshotDialogs;
    private int _numberOfSnapshots;
    
    private RulerNode _rulerNode;
    private PNode _rulerParentNode;
    private PNode _rulerDragBoundsNode;
    
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
        };
        _clock.addClockListener( _clockListener );

        _bead = bead;

        _laser = laser;
        _laser.addObserver( this );

        _isRunning = DEFAULT_IS_RUNNING;
        _numberOfMeasurements = 0;
        _snapshotDialogs =  new ArrayList();
        _numberOfSnapshots = 0;

        JPanel toolPanel = createToolPanel( font );

        JPanel chartPanel = createChartPanel( font, bead, laser );

        setLayout( new BorderLayout() );
        add( toolPanel, BorderLayout.NORTH );
        add( chartPanel, BorderLayout.CENTER );
        
        setZoomIndex( DEFAULT_ZOOM_INDEX );
    }
    
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
     * Creates the tool panel that appears at the top of the dialog.
     */
    private JPanel createToolPanel( Font font ) {

        _measurementsString = OTResources.getString( "label.measurements" );
        _startString = OTResources.getString( "button.start" );
        _stopString = OTResources.getString( "button.stop" );
        _binWidthString = OTResources.getString( "label.binWidth" );
        _unitsString = OTResources.getString( "units.position" );
        _positionHistogramSnapshotTitle = OTResources.getString( "title.positionHistogramSnapshot" );

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
        _zoomOutButton.setEnabled( _zoomIndex != ZOOM_SPECS.length - 1 );

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
     * Creates the Piccolo panel that contains the chart, ruler, etc.
     */
    private JPanel createChartPanel( Font font, Bead bead, Laser laser ) {

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( CHART_SIZE );
        
        _snapshotNode = new PComposite();
        _snapshotNode.setPickable( false );
        _snapshotNode.setChildrenPickable( false );
        _snapshotNode.setOffset( 0, 0 );
        canvas.getLayer().addChild( _snapshotNode );
        
        _plot = new PositionHistogramPlot();
        
        // add a vertical marker at position=0
        Marker originMarker = new ValueMarker( 0 );
        originMarker.setLabel("");
        originMarker.setPaint( ORIGIN_MARKER_COLOR );
        originMarker.setStroke( ORIGIN_MARKER_STROKE );
        _plot.addDomainMarker(originMarker);
        
        JFreeChart chart = new JFreeChart( null /* title */, null /* titleFont */, _plot, false /* createLegend */);
        chart.setAntiAlias( true );
        chart.setBorderVisible( true );
        chart.setBackgroundPaint( CHART_BACKGROUND_COLOR );
        
        _chartNode = new JFreeChartNode( chart );
        _chartNode.setPickable( false );
        _chartNode.setChildrenPickable( false );
        _chartNode.setOffset( 0, 0 );
        _chartNode.setBounds( 0, 0, CHART_SIZE.width, CHART_SIZE.height );
        _chartNode.updateChartRenderingInfo();
        _snapshotNode.addChild( _chartNode );
        
        _measurementsNode = new PText( "?" );
        _measurementsNode.setOffset( 10, 10 );
        setNumberOfMeasurements( _numberOfMeasurements );
        _snapshotNode.addChild( _measurementsNode );
        
        _binWidthNode = new PText();
        _binWidthNode.setOffset( _measurementsNode.getXOffset(), _measurementsNode.getFullBounds().getMaxY() + 3 );
        _snapshotNode.addChild( _binWidthNode );
        
        _rulerParentNode = new PNode();
        _rulerParentNode.setOffset( 0, 0 );
        canvas.getLayer().addChild( _rulerParentNode );
        
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
        return _snapshotNode.toImage();
    }
    
    /**
     * Sets the zoom index.
     * 
     * @param zoomIndex
     */
    public void setZoomIndex( int zoomIndex ) {
        if ( zoomIndex < 0 || zoomIndex > ZOOM_SPECS.length - 1 ) {
            throw new IndexOutOfBoundsException( "zoomIndex out of bounds: " + zoomIndex );
        }
        _zoomIndex = zoomIndex;
        ZoomSpec zoomSpec = ZOOM_SPECS[_zoomIndex];
        // Update the histogram's x-axis range
        _plot.setPositionRange( -zoomSpec.positionRange, zoomSpec.positionRange, zoomSpec.binWidth );
        // Update the bin width display
        _binWidthNode.setText( _binWidthString + " " + BIN_WIDTH_FORMAT.format( zoomSpec.binWidth ) + " " + _unitsString );
        // Enable/disable zoom buttons
        _zoomInButton.setEnabled( _zoomIndex != 0 );
        _zoomOutButton.setEnabled( _zoomIndex != ZOOM_SPECS.length - 1 );
        // Update the ruler
        updateRuler();
        // Set measurements to zero
        clearMeasurements();
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
    }

    private void handleRulerButton() {
        _rulerNode.setVisible( !_rulerNode.isVisible() );
    }
    
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
        ZoomSpec zoomSpec = ZOOM_SPECS[_zoomIndex];
        double distanceBetweenFirstAndLastTick = getRulerNodeLength( zoomSpec.rulerMax ); 
        
        // major tick labels
        String[] majorTickLabels = { "0", String.valueOf( zoomSpec.rulerMax ) };
        
        // minor ticks
        int numMinorTicksBetweenMajors = (int)( zoomSpec.rulerMajorTickSpacing / zoomSpec.rulerMinorTickSpacing ) - 1;
        
        // create the ruler
        _rulerNode = new RulerNode( distanceBetweenFirstAndLastTick, RULER_HEIGHT, majorTickLabels, _unitsString, numMinorTicksBetweenMajors, RULER_FONT_SIZE );
        _rulerParentNode.addChild( _rulerNode );
        _rulerNode.addInputEventListener( new CursorHandler() );
        
        // constraint the ruler's drag bounds
        final int minPixelsVisible = 20;
        double x = _snapshotNode.getFullBounds().getX() - _rulerNode.getFullBounds().getWidth() + minPixelsVisible;
        double y = _snapshotNode.getFullBounds().getY();
        double w = _snapshotNode.getFullBounds().getWidth() + ( 2 * _rulerNode.getFullBounds().getWidth() ) - ( 2 * minPixelsVisible );
        double h = _snapshotNode.getFullBounds().getHeight();
        _rulerDragBoundsNode = new PPath( new Rectangle2D.Double( x, y, w,h ) );
        _rulerParentNode.addChild( _rulerDragBoundsNode );
        _rulerNode.addInputEventListener( new BoundedDragHandler( _rulerNode, _rulerDragBoundsNode ) );
        
        // Center the ruler
        double xOffset = ( _chartNode.getFullBounds().getWidth() - _rulerNode.getFullBounds().getWidth() ) / 2;
        double yOffset = ( _chartNode.getFullBounds().getHeight() - _rulerNode.getFullBounds().getHeight() ) / 2;
        _rulerNode.setOffset( xOffset, yOffset );
        
        _rulerNode.setVisible( rulerVisible );
    }
    
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
