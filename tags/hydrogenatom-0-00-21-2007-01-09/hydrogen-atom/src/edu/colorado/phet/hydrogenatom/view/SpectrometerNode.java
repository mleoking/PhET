/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.util.*;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.CloseButtonNode;
import edu.colorado.phet.hydrogenatom.event.PhotonEmittedEvent;
import edu.colorado.phet.hydrogenatom.event.PhotonEmittedListener;
import edu.colorado.phet.hydrogenatom.model.Photon;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * SpectrometerNode is the visual representation of a spectrometer.
 * The spectrometer's display is divided into a discrete grid, with
 * the horizontal axis being wavelength, and the vertical wavelength 
 * being number of photos emitted. Each time a photon is emitted,
 * a "cell" in the grid is lit.  The grid has a fixed number of cells,
 * so it is only capable of showing some maximum number of photons 
 * for each wavelength. Once this maximum is reached, additional 
 * emitted photons are not displayed for that wavelength.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SpectrometerNode extends PhetPNode implements PhotonEmittedListener {
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    // prints debugging output
    private static final boolean DEBUG_OUTPUT = false;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /* The display is the area where emitted photons are recorded. */
    private static final Color DISPLAY_BACKGROUND = Color.BLACK;
    private static final double DISPLAY_X_MARGIN = 10;
    private static final double DISPLAY_Y_MARGIN = 5;
    private static final double DISPLAY_CORNER_RADIUS = 10;
    private static final double DISPLAY_UV_PERCENT = 0.15;
    private static final double DISPLAY_IR_PERCENT = 0.15;
    private static final double DISPLAY_INNER_X_MARGIN = 10;

    /* Title appear in the upper left */
    private static final double TITLE_X_MARGIN = 15;
    private static final double TITLE_Y_MARGIN = 7;
    private static final Color TITLE_COLOR = Color.WHITE;

    /* Close butto appears in the upper right */
    private static final double CLOSE_BUTTON_X_MARGIN = 15;
    private static final double CLOSE_BUTTON_Y_MARGIN = 6;
    
    /* Button panel appear in bottom center */
    private static final double BUTTON_PANEL_Y_MARGIN = 5;
    
    /* Ticks and labels appear at the bottom of the display area */
    private static final double MAJOR_TICK_LENGTH = 4;
    private static final double MINOR_TICK_LENGTH = 2;
    private static final Color TICK_COLOR = Color.WHITE;
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Font TICK_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    private static final double TICK_LABEL_SPACING = 2;
    private static final Font UV_IR_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 18 );
    
    /* Color key is a think horizontal strip at the bottom of the display area, above tick marks */
    private static final double COLOR_KEY_HEIGHT = 2;
    private static  final double COLOR_KEY_Y_MARGIN = 4;
    
    /* Cells are the small dots that appear in the display area to represent photons emitted */
    private static final double CELL_WIDTH = 5;
    private static final double CELL_HEIGHT = 5;
    
    //----------------------------------------------------------------------------
    // Data structures
    //----------------------------------------------------------------------------
    
    /*
     * MeterRecord tells how many "cells" on the spectrometer 
     * should be lit for a specific wavelength.
     */
    private static class MeterRecord {
        public double wavelength;
        public int count;
        public MeterRecord( double wavelength ) {
            this.wavelength = wavelength;
            count = 0;
        }
    }
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PSwingCanvas _canvas;
    private Dimension _size;
    private Font _font;
    private double _minWavelength, _maxWavelength;

    private boolean _isRunning;
    private ArrayList _meterRecords; // array of MeterRecords
    
    private PImage _panelNode;
    private PNode _displayAreaNode;
    private PNode _cellsNode; // contains all of the "cells" in the display area
    private PText _titleNode;
    private CloseButtonNode _closeButton;
    private JButton _startStopButton;
    private JButton _resetButton;
    private JButton _snapshotButton;
    private PSwing _buttonPanelWrapper;
    
    private double _minUVPosition;
    private double _maxUVPosition;
    private double _minVisiblePosition;
    private double _maxVisiblePosition;
    private double _minIRPosition;
    private double _maxIRPosition;
    private int _maxCells; // number of vertical cells that the display area can show
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param canvas
     * @param size 
     * @param font
     * @param minWavelength
     * @param maxWavelength
     */
    public SpectrometerNode( PSwingCanvas canvas, Dimension size, String title, Font font, double minWavelength, double maxWavelength ) {

        if ( minWavelength >= VisibleColor.MIN_WAVELENGTH || maxWavelength <= VisibleColor.MAX_WAVELENGTH ) {
            throw new IllegalArgumentException( "requries a spectrum the contains UV, IR and visible wavelengths" );
        }
        
        _canvas = canvas;
        _size = new Dimension( size.width, size.height );
        _font = font;
        _minWavelength = minWavelength;
        _maxWavelength = maxWavelength;

        _isRunning = false;
        _meterRecords = new ArrayList();

        // Background panel
        {
            _panelNode = PImageFactory.create( HAConstants.IMAGE_SPECTROMETER_PANEL );
            double scaleX = size.width / _panelNode.getWidth();
            double scaleY = size.height / _panelNode.getHeight();
            AffineTransform panelTransform = new AffineTransform();
            panelTransform.scale( scaleX, scaleY );
            _panelNode.setTransform( panelTransform );
            _panelNode.setOffset( 0, 0 );
        }

        // Inner width of the display area
        final double innerWidth = Math.max( 1, size.getWidth() - ( 2 * DISPLAY_X_MARGIN ) - ( 2 * DISPLAY_INNER_X_MARGIN ) );
        final double uvWidth = innerWidth * DISPLAY_UV_PERCENT;
        final double irWidth = innerWidth * DISPLAY_IR_PERCENT;
        final double visibleWidth = innerWidth * ( 1 - DISPLAY_UV_PERCENT - DISPLAY_IR_PERCENT );
        assert ( visibleWidth > 0 );

        // Positions relative to the display area
        _minUVPosition = DISPLAY_INNER_X_MARGIN;
        _maxUVPosition = _minUVPosition + uvWidth;
        _minVisiblePosition = _maxUVPosition;
        _maxVisiblePosition = _minVisiblePosition + visibleWidth;
        _minIRPosition = _maxVisiblePosition;
        _maxIRPosition = _minIRPosition + irWidth;
        
        // Title
        {
            _titleNode = new PText( title );
            _titleNode.setFont( font );
            _titleNode.setTextPaint( TITLE_COLOR );
            _titleNode.setOffset( TITLE_X_MARGIN, TITLE_Y_MARGIN );
        }

        // Close button
        _closeButton = new CloseButtonNode( canvas );
        _closeButton.setOffset( size.getWidth() - _closeButton.getFullBounds().getWidth() - CLOSE_BUTTON_X_MARGIN, CLOSE_BUTTON_Y_MARGIN );
        
        // Button panel, centered below the black rectangle.
        {
            // Start/Stop button
            String s = _isRunning ? SimStrings.get( "button.spectrometer.stop" ) : SimStrings.get( "button.spectrometer.start" );
            _startStopButton = new JButton( s );
            _startStopButton.setFont( font );
            _startStopButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    if ( _isRunning ) {
                        stop();
                    }
                    else {
                        start();
                    }
                }
            } );

            // Reset button
            _resetButton = new JButton( SimStrings.get( "button.spectrometer.reset" ) );
            _resetButton.setFont( font );
            _resetButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    reset();
                }
            } );

            // Snapshot button
            try {
                BufferedImage snapshotImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_CAMERA );
                Icon snapshotIcon = new ImageIcon( snapshotImage );
                _snapshotButton = new JButton( snapshotIcon );
            }
            catch ( IOException e ) {
                e.printStackTrace();
                _snapshotButton = new JButton( "Snapshot" );
            }
            _snapshotButton.setMargin( new Insets( 0, 0, 0, 0 ) );

            // Put buttons in a panel
            JPanel buttonPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( buttonPanel );
            buttonPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            layout.addComponent( _startStopButton, row, col++ );
            layout.addComponent( _resetButton, row, col++ );
            layout.addComponent( _snapshotButton, row, col );
            _buttonPanelWrapper = new PSwing( canvas, buttonPanel );
            _buttonPanelWrapper.addInputEventListener( new CursorHandler() );
            PBounds fb = _buttonPanelWrapper.getFullBounds();
            _buttonPanelWrapper.setOffset( size.getWidth() / 2 - fb.getWidth() / 2, size.getHeight() - fb.getHeight() - BUTTON_PANEL_Y_MARGIN );
            
            // Opacity
            buttonPanel.setOpaque( false );
            _startStopButton.setOpaque( false );
            _resetButton.setOpaque( false );
            _snapshotButton.setOpaque( false );
        }

        /* 
         * Display area -
         * This includes the black rectangle that the "cells" appear in, 
         * the color key along the bottom of the black rectangle,
         * and the ticks marks and labels that appear below the black rectangle.
         * It also includes any "cells" that are lit.
         */
        {
            PNode tickLabel = createTickLabel( 0 );
            
            // The black background in the display area
            PPath displayBackgroundNode = new PPath();
            {
                double w = size.getWidth() - ( 2 * DISPLAY_X_MARGIN );
                if ( w < 1 ) {
                    w = 1;
                }
                double h = size.getHeight() - TITLE_Y_MARGIN - _titleNode.getFullBounds().getHeight() - 
                    DISPLAY_Y_MARGIN - MAJOR_TICK_LENGTH - TICK_LABEL_SPACING - tickLabel.getHeight() -
                    DISPLAY_Y_MARGIN - _buttonPanelWrapper.getFullBounds().getHeight() - BUTTON_PANEL_Y_MARGIN;
                if ( h < 1 ) {
                    h = 1;
                }
                Shape shape = new RoundRectangle2D.Double( 0, 0, w, h, DISPLAY_CORNER_RADIUS, DISPLAY_CORNER_RADIUS );
                displayBackgroundNode.setPathTo( shape );
                displayBackgroundNode.setPaint( DISPLAY_BACKGROUND );
                displayBackgroundNode.setStroke( null );
                displayBackgroundNode.setOffset( 0, 0 );
            }
            
            // The color key along the bottom of the display area
            PNode colorKeyNode = new PNode();
            {
                // UV portion
                PPath uvNode = new PPath( new Rectangle2D.Double( 0, 0, uvWidth, COLOR_KEY_HEIGHT ) );
                uvNode.setPaint( HAConstants.UV_COLOR );
                uvNode.setStroke( null );
                
                // IR portion
                PPath irNode = new PPath( new Rectangle2D.Double( 0, 0, irWidth, COLOR_KEY_HEIGHT ) );
                irNode.setPaint( HAConstants.IR_COLOR );
                irNode.setStroke( null );

                // Visible portion
                Image spectrumImage = SpectrumImageFactory.createHorizontalSpectrum( (int)visibleWidth, (int)COLOR_KEY_HEIGHT );
                PNode spectrumNode = new PImage( spectrumImage );
                
                colorKeyNode.addChild( uvNode );
                colorKeyNode.addChild( irNode );
                colorKeyNode.addChild( spectrumNode );

                uvNode.setOffset( _minUVPosition, 0 );
                spectrumNode.setOffset( _minVisiblePosition, 0 );
                irNode.setOffset( _minIRPosition, 0 );
                
                double xo = 0;
                double yo = displayBackgroundNode.getHeight() - colorKeyNode.getHeight() - COLOR_KEY_Y_MARGIN;
                colorKeyNode.setOffset( xo, yo );
            }
            
            PNode ticksNode = new PNode();
            {
                ticksNode.setOffset( 0, displayBackgroundNode.getHeight() );
                
                // min UV
                PNode tick1 = createMajorTickMark();
                tick1.setOffset( _minUVPosition, 0 );
                PNode label1 = createTickLabel( (int)_minWavelength );
                label1.setOffset( _minUVPosition - label1.getWidth() / 2, tick1.getHeight() + TICK_LABEL_SPACING );
                ticksNode.addChild( tick1 );
                ticksNode.addChild( label1 );
                
                // max IR
                PNode tick4 = createMajorTickMark();
                tick4.setOffset( _maxIRPosition, 0 );
                PNode label4 = createTickLabel( (int)_maxWavelength );
                label4.setOffset( _maxIRPosition - label4.getWidth() / 2, tick4.getHeight() + TICK_LABEL_SPACING );
                ticksNode.addChild( tick4 );
                ticksNode.addChild( label4 );
                
                // major ticks in the visible spectrum
                int[] majorTicks = { (int)VisibleColor.MIN_WAVELENGTH, 500, 600, 700, (int)VisibleColor.MAX_WAVELENGTH };
                for ( int i = 0; i < majorTicks.length; i++ ) {
                    PNode tick = createMajorTickMark();
                    double m = ( majorTicks[i] - VisibleColor.MIN_WAVELENGTH ) / ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH );
                    double x = _minVisiblePosition + ( m *  ( _maxVisiblePosition - _minVisiblePosition ) );
                    tick.setOffset( x, 0 );
                    PNode label = createTickLabel( majorTicks[i] );
                    label.setOffset( x - label.getWidth() / 2, tick.getHeight() + TICK_LABEL_SPACING );
                    ticksNode.addChild( tick );
                    ticksNode.addChild( label );
                }
                
                // minor ticks in the visible spectrum
                int[] minorTicks = { 400, 450, 550, 650, 750 };
                for ( int i = 0; i < minorTicks.length; i++ ) {
                    PNode tick = createMinorTickMark();
                    double m = ( minorTicks[i] - VisibleColor.MIN_WAVELENGTH ) / ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH );
                    double x = _minVisiblePosition + ( m *  ( _maxVisiblePosition - _minVisiblePosition ) );
                    tick.setOffset( x, 0 );
                    ticksNode.addChild( tick );
                }

                // UV label
                PText uvLabel = new PText( "UV" );
                uvLabel.setTextPaint( TICK_COLOR );
                uvLabel.setFont( UV_IR_FONT );
                uvLabel.setOffset( ( ( _minUVPosition + ( _maxUVPosition - _minUVPosition ) / 2 ) ) - ( uvLabel.getWidth() / 2 ), 
                        MAJOR_TICK_LENGTH + TICK_LABEL_SPACING );
                ticksNode.addChild( uvLabel );

                // IR label
                PText irLabel = new PText( "IR" );
                irLabel.setTextPaint( TICK_COLOR );
                irLabel.setFont( UV_IR_FONT );
                irLabel.setOffset( ( ( _minIRPosition + ( _maxIRPosition - _minIRPosition ) / 2 ) ) - ( irLabel.getWidth() / 2 ),
                        MAJOR_TICK_LENGTH + TICK_LABEL_SPACING );
                ticksNode.addChild( irLabel );
            }
            
            PNode parentNode = new PNode();
            parentNode.addChild( displayBackgroundNode );
            parentNode.addChild( colorKeyNode );
            parentNode.addChild( ticksNode );
            
            // Convert display area to an image
            PImage staticDisplayNode = new PImage( parentNode.toImage() );
            staticDisplayNode.setOffset( 0, 0 );
            
            // Node that holds meter cells
            _cellsNode = new PNode();
            _cellsNode.setOffset( 0, displayBackgroundNode.getHeight() - colorKeyNode.getHeight() - COLOR_KEY_Y_MARGIN - 2 );
            
            _displayAreaNode = new PNode();
            double xOffset = DISPLAY_X_MARGIN;
            double yOffset = TITLE_Y_MARGIN + _titleNode.getFullBounds().getHeight() + DISPLAY_Y_MARGIN;
            _displayAreaNode.setOffset( xOffset, yOffset );
            _displayAreaNode.addChild( staticDisplayNode );
            _displayAreaNode.addChild( _cellsNode );
            
            _maxCells = (int) ( ( displayBackgroundNode.getHeight() - colorKeyNode.getHeight() - COLOR_KEY_Y_MARGIN - 2 ) / CELL_HEIGHT );
        }

        // Layering of nodes
        addChild( _panelNode );
        addChild( _displayAreaNode );
        addChild( _titleNode );
        addChild( _closeButton );
        addChild( _buttonPanelWrapper );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a snapshot of the spectrometer in its current state.
     * 
     * @param title
     */
    public SpectrometerSnapshotNode getSnapshot( String title ) {
        return new SpectrometerSnapshotNode( this, title, _canvas );
    }
    
    /**
     * Adds a listener for the "Close" button.
     * 
     * @param listener
     */
    public void addCloseListener( ActionListener listener ) {
        _closeButton.addActionListener( listener );
    }
    
    /**
     * Adds a listener for the "Snapshot" button.
     * 
     * @param listener
     */
    public void addSnapshotListener( ActionListener listener ) {
        _snapshotButton.addActionListener( listener );
    }
    
    //----------------------------------------------------------------------------
    // Accessors used to create snapshot
    //----------------------------------------------------------------------------
    
    /*
     * Gets the size of the spectrometer.
     * 
     * @return Dimension
     */
    protected Dimension getSize() {
        return _size;
    }

    /* 
     * Gets the Font used by the spectrometer.
     * 
     * @return Font
     */
    protected Font getFont() {
        return _font;
    }

    /*
     * Gets an Image that is identical to the "display area" portion
     * of the spectrometer.  This includes the black background, cells,
     * the color key, tick marks, and tick labels.
     * 
     * @return Image
     */
    protected Image getDisplayAreaImage() {
        return _displayAreaNode.toImage();
    }

    //----------------------------------------------------------------------------
    // Button handlers
    //----------------------------------------------------------------------------
    
    /**
     * Starts the spectrometer.
     * While running, the spectrometer records emitted photons.
     */
    public void start() {
        _isRunning = true;
        _startStopButton.setText( SimStrings.get( "button.spectrometer.stop" ) );
    }
    
    /**
     * Stops the spectrometer.
     * While stopped, the spectrometer ignores emitted photons.
     */
    public void stop() {
        _isRunning = false;
        _startStopButton.setText( SimStrings.get( "button.spectrometer.start" ) );
    }
    
    /**
     * Resets the spectrometer.
     * This clears any cells that are displayed.
     */
    public void reset() {
        if ( DEBUG_OUTPUT ) {
            System.out.println( "SpectrometerNode.reset" );
        }
        _meterRecords.clear();
        _cellsNode.removeAllChildren();
    }
    
    //----------------------------------------------------------------------------
    // PhotonEmittedListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when a photon is emitted.
     * Increments the MeterRecord for the photon's wavelength,
     * or creates a MeterRecord if no record exists.
     * Adds a cell to the display.
     * 
     * @param event
     */
    public void photonEmitted( PhotonEmittedEvent event ) {
        if ( _isRunning && isVisible() ) {
            Photon photon = event.getPhoton();
            double wavelength = photon.getWavelength();
            if ( DEBUG_OUTPUT ) {
                System.out.println( "SpectrometerNode.photonEmitted " + wavelength );
            }
            MeterRecord meterRecord = getMeterRecord( wavelength );
            if ( meterRecord == null ) {
                meterRecord = new MeterRecord( wavelength );
                meterRecord.count = 1;
                _meterRecords.add( meterRecord );
            }
            else {
                meterRecord.count++;
            }
            addCell( meterRecord );
        }
    }
    
    /*
     * Gets the MeterRecord for a specified wavelength.
     * If no record exists, returns null
     * 
     * @param wavelength
     * @return MeterRecord, possibly null
     */
    private MeterRecord getMeterRecord( double wavelength ) {
        Iterator i = _meterRecords.iterator();
        while ( i.hasNext() ) {
            MeterRecord record = (MeterRecord) i.next();
            if ( record.wavelength == wavelength ) {
                return record;
            }
        }
        return null;
    }

    //----------------------------------------------------------------------------
    // Tick marks and labels
    //----------------------------------------------------------------------------
    
    /*
     * Creates a major tick mark.
     * 
     * @return PNode
     */
    private PNode createMajorTickMark() {
        return createTickMark( MAJOR_TICK_LENGTH );
    }
    
    /*
     * Creates a minor tick mark.
     * 
     * @return PNode
     */
    private PNode createMinorTickMark() {
        return createTickMark( MINOR_TICK_LENGTH );
    }
    
    /*
     * Creates a tick mark of a specific length.
     * 
     * @param length
     * @return PNode
     */
    private PNode createTickMark( double length ) {
        PPath tickNode = new PPath();
        Shape shape = new Line2D.Double( 0, 0, 0, length );
        tickNode.setPathTo( shape );
        tickNode.setStroke( TICK_STROKE );
        tickNode.setStrokePaint( TICK_COLOR );
        return tickNode;
    }
    
    /*
     * Creates a tick mark label.
     * 
     * @param wavelength
     * @return PNode
     */
    private PNode createTickLabel( int wavelength ) {
        PText textNode = new PText( String.valueOf( wavelength ) );
        textNode.setFont( TICK_FONT );
        textNode.setTextPaint( TICK_COLOR );
        return textNode;
    }
    
    //----------------------------------------------------------------------------
    // Cells
    //----------------------------------------------------------------------------
    
    /*
     * Adds a cell to the display.
     * The cell's MeterRecord tells use where to position cell, based on
     * the number of cells with the same wavelength that are already lit.
     * 
     * @param MeterRecord
     */
    private void addCell( MeterRecord meterRecord ) {
        double wavelength = meterRecord.wavelength;
        int count = meterRecord.count;
        if ( count <= _maxCells ) {
            PNode cellNode = createCell( wavelength );
            _cellsNode.addChild( cellNode );
            double x = 0;
            double y = -( count * CELL_HEIGHT );
            if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
                // UV wavelength
                double m = ( wavelength - _minWavelength ) / ( VisibleColor.MIN_WAVELENGTH  - _minWavelength );
                x = _minUVPosition + ( m * ( _maxUVPosition - _minUVPosition ) ) - ( cellNode.getWidth() / 2 );
            }
            else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
                // IR wavelength
                double m = ( _maxWavelength - wavelength ) / ( _maxWavelength - VisibleColor.MAX_WAVELENGTH );
                x = _minIRPosition + ( m * ( _maxIRPosition - _minIRPosition ) ) - ( cellNode.getWidth() / 2 );
            }
            else {
                // Visible wavelength
                double m = ( wavelength - VisibleColor.MIN_WAVELENGTH ) / ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH );
                x = _minVisiblePosition + ( m * ( _maxVisiblePosition - _minVisiblePosition ) ) - ( cellNode.getWidth() / 2 );

            }
            cellNode.setOffset( x, y );
        }
    }
    
    /*
     * Creates a cell node.
     * 
     * @param wavelength
     * @return PNode
     */
    private PNode createCell( double wavelength ) {
        PPath cellNode = new PPath( new Ellipse2D.Double( 0, 0, CELL_WIDTH, CELL_HEIGHT ) );
        Color color = ColorUtils.wavelengthToColor( wavelength );
        cellNode.setPaint( color );
        cellNode.setStroke( null );
        return cellNode;
    }
    
    //----------------------------------------------------------------------------
    // Snapshot
    //----------------------------------------------------------------------------

    /**
     * SpectrometerSnapshotNode is a snapshot of a SpectrometerNode.
     */
    public static class SpectrometerSnapshotNode extends PhetPNode {

        private CloseButtonNode _closeButton;
        
        /*
         * Constructor.
         * 
         * @param spectrometerNode
         * @param title
         * @param canvas
         */
        protected SpectrometerSnapshotNode( SpectrometerNode spectrometerNode, String title, PSwingCanvas canvas ) {
            super();

            // Background panel
            PImage panelNode = PImageFactory.create( HAConstants.IMAGE_SPECTROMETER_SNAPSHOT_PANEL );
            Dimension size = spectrometerNode.getSize();
            double scaleX = size.width / panelNode.getWidth();
            double scaleY = size.height / panelNode.getHeight();
            AffineTransform panelTransform = new AffineTransform();
            panelTransform.scale( scaleX, scaleY );
            panelNode.setTransform( panelTransform );
            panelNode.setOffset( 0, 0 );
            PBounds pfb = panelNode.getFullBounds();
            
            // Title
            PText titleNode = new PText( title );
            Font font = spectrometerNode.getFont();
            titleNode.setFont( font );
            titleNode.setTextPaint( TITLE_COLOR );
            titleNode.setOffset( pfb.getX() + TITLE_X_MARGIN, pfb.getY() + TITLE_Y_MARGIN );

            // Display Area snapshot
            Image displayAreaImage = spectrometerNode.getDisplayAreaImage();
            PImage displayAreaNode = new PImage( displayAreaImage );
            double x = DISPLAY_X_MARGIN;
            double y = TITLE_Y_MARGIN + titleNode.getFullBounds().getHeight() + DISPLAY_Y_MARGIN;
            displayAreaNode.setOffset( x, y );
            
            // Collapse all of the above nodes to one static image
            PNode parentNode = new PNode();
            parentNode.addChild( panelNode );
            parentNode.addChild( displayAreaNode );
            parentNode.addChild( titleNode );
            PImage staticNode = new PImage( parentNode.toImage() );

            // Close button
            _closeButton = new CloseButtonNode( canvas );
            _closeButton.setOffset( pfb.getMaxX() - _closeButton.getFullBounds().getWidth() - CLOSE_BUTTON_X_MARGIN, pfb.getY() + CLOSE_BUTTON_Y_MARGIN );

            // Node layering order
            addChild( staticNode );
            addChild( _closeButton );
            
            // Snapshots are moveable
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                public void mouseDragged( PInputEvent event ) {
                    final double dx = event.getCanvasDelta().width;
                    final double dy = event.getCanvasDelta().height;
                    translate( dx, dy );
                }

                public void mousePressed( PInputEvent event ) {
                    moveToFront();
                }
            } );
        }
        
        /**
         * Adds a listener to the "Close" button.
         * @param listener
         */
        public void addCloseListener( ActionListener listener ) {
            _closeButton.addActionListener( listener );
        }
    }
}
