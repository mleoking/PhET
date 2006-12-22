/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.spectrometer;

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

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.Photon;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.PhotonEmittedEvent;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.PhotonEmittedListener;
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
 * SpectrometerNode
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

    private static final double DISPLAY_X_MARGIN = 10;
    private static final double DISPLAY_Y_MARGIN = 5;
    private static final double DISPLAY_CORNER_RADIUS = 10;
    private static final double DISPLAY_UV_PERCENT = 0.15;
    private static final double DISPLAY_IR_PERCENT = 0.15;
    private static final double DISPLAY_INNER_X_MARGIN = 10;

    private static final double TITLE_X_MARGIN = 15;
    private static final double TITLE_Y_MARGIN = 7;
    private static final Color TITLE_COLOR = Color.WHITE;

    private static final double CLOSE_BUTTON_X_MARGIN = 15;
    private static final double CLOSE_BUTTON_Y_MARGIN = 6;
    
    private static final double BUTTON_PANEL_X_MARGIN = 15;
    private static final double BUTTON_PANEL_Y_MARGIN = 5;
    
    private static final double MAJOR_TICK_LENGTH = 4;
    private static final double MINOR_TICK_LENGTH = 2;
    private static final Color TICK_COLOR = Color.WHITE;
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Font TICK_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.PLAIN, 12 );
    private static final double TICK_LABEL_SPACING = 2;

    private static final Font UV_IR_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    
    private static final double COLOR_KEY_HEIGHT = 2;
    private static  final double COLOR_KEY_Y_MARGIN = 4;
    
    private static final double CELL_WIDTH = 5;
    private static final double CELL_HEIGHT = 5;
    
    //----------------------------------------------------------------------------
    // Data structures
    //----------------------------------------------------------------------------
    
    /*
     * MeterRecord lets how many "cells" on the spectrometer 
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
    private PNode _displayNode;
    private PText _titleNode;
    private JButton _closeButton;
    private PSwing _closeButtonWrapper;
    private JButton _startStopButton;
    private JButton _resetButton;
    private JButton _snapshotButton;
    private PSwing _buttonPanelWrapper;
    private PNode _cellsNode;
    
    private double _minUVPosition;
    private double _maxUVPosition;
    private double _minVisiblePosition;
    private double _maxVisiblePosition;
    private double _minIRPosition;
    private double _maxIRPosition;
    private int _maxCells;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public SpectrometerNode( PSwingCanvas canvas, Dimension size, String title, Font font, double minWavelength, double maxWavelength ) {

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
        {
            try {
                BufferedImage closeImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_CLOSE_BUTTON );
                Icon closeIcon = new ImageIcon( closeImage );
                _closeButton = new JButton( closeIcon );
            }
            catch ( IOException e ) {
                e.printStackTrace();
                _closeButton = new JButton( "X" );
            }
            _closeButton.setOpaque( false );
            _closeButton.setMargin( new Insets( 0, 0, 0, 0 ) );
            _closeButtonWrapper = new PSwing( canvas, _closeButton );
            PBounds cb = _closeButtonWrapper.getFullBounds();
            _closeButtonWrapper.setOffset( size.getWidth() - cb.getWidth() - CLOSE_BUTTON_X_MARGIN, CLOSE_BUTTON_Y_MARGIN );
        }

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
         * This includes the black rectangle that the "cell" appear in, 
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
                displayBackgroundNode.setPaint( Color.BLACK );
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

                PNode spectrumNode = PImageFactory.create( HAConstants.IMAGE_SPECTRUM );
                double sx = visibleWidth / spectrumNode.getWidth();
                double sy = COLOR_KEY_HEIGHT / spectrumNode.getHeight();
                AffineTransform tx = new AffineTransform();
                tx.scale( sx, sy );
                spectrumNode.setTransform( tx );
                
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
                        MAJOR_TICK_LENGTH + TICK_LABEL_SPACING + 4 );
                ticksNode.addChild( uvLabel );

                // IR label
                PText irLabel = new PText( "IR" );
                irLabel.setTextPaint( TICK_COLOR );
                irLabel.setFont( UV_IR_FONT );
                irLabel.setOffset( ( ( _minIRPosition + ( _maxIRPosition - _minIRPosition ) / 2 ) ) - ( irLabel.getWidth() / 2 ),
                        MAJOR_TICK_LENGTH + TICK_LABEL_SPACING + 4 );
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
            
            _displayNode = new PNode();
            double xOffset = DISPLAY_X_MARGIN;
            double yOffset = TITLE_Y_MARGIN + _titleNode.getFullBounds().getHeight() + DISPLAY_Y_MARGIN;
            _displayNode.setOffset( xOffset, yOffset );
            _displayNode.addChild( staticDisplayNode );
            _displayNode.addChild( _cellsNode );
            
            _maxCells = (int) ( ( displayBackgroundNode.getHeight() - colorKeyNode.getHeight() - COLOR_KEY_Y_MARGIN - 2 ) / CELL_HEIGHT );
        }

        // Layering of nodes
        addChild( _panelNode );
        addChild( _displayNode );
        addChild( _titleNode );
        addChild( _closeButtonWrapper );
        addChild( _buttonPanelWrapper );
    }

    //----------------------------------------------------------------------------
    // Tick marks and labels
    //----------------------------------------------------------------------------
    
    private PNode createMajorTickMark() {
        return createTickMark( MAJOR_TICK_LENGTH );
    }
    
    private PNode createMinorTickMark() {
        return createTickMark( MINOR_TICK_LENGTH );
    }
    
    private PNode createTickMark( double length ) {
        PPath tickNode = new PPath();
        Shape shape = new Line2D.Double( 0, 0, 0, length );
        tickNode.setPathTo( shape );
        tickNode.setStroke( TICK_STROKE );
        tickNode.setStrokePaint( TICK_COLOR );
        return tickNode;
    }
    
    private PNode createTickLabel( int wavelength ) {
        PText textNode = new PText( String.valueOf( wavelength ) );
        textNode.setFont( TICK_FONT );
        textNode.setTextPaint( TICK_COLOR );
        return textNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public SpectrometerSnapshotNode getSnapshot( String title ) {
        return new SpectrometerSnapshotNode( this, title );
    }
    
    public void addCloseListener( ActionListener listener ) {
        _closeButton.addActionListener( listener );
    }
    
    public void addSnapshotListener( ActionListener listener ) {
        _snapshotButton.addActionListener( listener );
    }
    
    protected Dimension getSize() {
        return _size;
    }

    protected Font getFont() {
        return _font;
    }

    protected Image getDisplayImage() {
        return _displayNode.toImage();
    }
    
    protected Image getCellsImage() {
        return _cellsNode.toImage();
    }

    //----------------------------------------------------------------------------
    // Button handlers
    //----------------------------------------------------------------------------
    
    private void start() {
        _isRunning = true;
        _startStopButton.setText( SimStrings.get( "button.spectrometer.stop" ) );
        //XXX
    }
    
    private void stop() {
        _isRunning = false;
        _startStopButton.setText( SimStrings.get( "button.spectrometer.start" ) );
        //XXX
    }
    
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
    
    public MeterRecord getMeterRecord( double wavelength ) {
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
    // Cells
    //----------------------------------------------------------------------------
    
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

    public static class SpectrometerSnapshotNode extends PhetPNode {

        private JButton _closeButton;
        
        protected SpectrometerSnapshotNode( SpectrometerNode spectrometerNode, String title ) {
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

            // Display snapshot
            Image displayImage = spectrometerNode.getDisplayImage();
            PImage displayNode = new PImage( displayImage );
            double x = DISPLAY_X_MARGIN;
            double y = TITLE_Y_MARGIN + titleNode.getFullBounds().getHeight() + DISPLAY_Y_MARGIN;
            displayNode.setOffset( x, y );
            
            // Collapse all of the above nodes to one static image
            PNode parentNode = new PNode();
            parentNode.addChild( panelNode );
            parentNode.addChild( displayNode );
            parentNode.addChild( titleNode );
            PImage staticNode = new PImage( parentNode.toImage() );

            // Close button
            try {
                BufferedImage closeImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_CLOSE_BUTTON );
                Icon closeIcon = new ImageIcon( closeImage );
                _closeButton = new JButton( closeIcon );
            }
            catch ( IOException e ) {
                e.printStackTrace();
                _closeButton = new JButton( "X" );
            }
            _closeButton.setOpaque( false );
            _closeButton.setMargin( new Insets( 0, 0, 0, 0 ) );
            PSwing closeButtonWrapper = new PSwing( spectrometerNode._canvas, _closeButton );
            PBounds cb = closeButtonWrapper.getFullBounds();
            closeButtonWrapper.setOffset( pfb.getX() + pfb.getWidth() - cb.getWidth() - CLOSE_BUTTON_X_MARGIN, pfb.getY() + CLOSE_BUTTON_Y_MARGIN );

            // Node layering order
            addChild( staticNode );
            addChild( closeButtonWrapper );
            
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
        
        public void addCloseListener( ActionListener listener ) {
            _closeButton.addActionListener( listener );
        }
    }
}
