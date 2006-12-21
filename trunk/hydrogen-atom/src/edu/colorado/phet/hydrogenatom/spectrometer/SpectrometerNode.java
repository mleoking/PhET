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
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
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
public class SpectrometerNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double DISPLAY_X_MARGIN = 10;
    private static final double DISPLAY_Y_MARGIN = 5;
    private static final double DISPLAY_CORNER_RADIUS = 10;

    private static final double TITLE_X_MARGIN = 15;
    private static final double TITLE_Y_MARGIN = 7;
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color SNAPSHOT_TITLE_COLOR = Color.WHITE;

    private static final double CLOSE_BUTTON_X_MARGIN = 15;
    private static final double CLOSE_BUTTON_Y_MARGIN = 6;
    
    private static final double BUTTON_PANEL_X_MARGIN = 15;
    private static final double BUTTON_PANEL_Y_MARGIN = 5;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PSwingCanvas _canvas;
    private Dimension _size;
    private Font _font;

    private boolean _isRunning;

    private PPath _displayNode;
    private JButton _closeButton;
    private PSwing _closeButtonWrapper;
    private JButton _startStopButton;
    private JButton _snapshotButton;
    private PSwing _buttonPanelWrapper;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public SpectrometerNode( PSwingCanvas canvas, Dimension size, String title, Font font ) {

        _canvas = canvas;
        _size = new Dimension( size.width, size.height );
        _font = font;

        _isRunning = false;

        // Background panel
        PImage panelNode = PImageFactory.create( HAConstants.IMAGE_SPECTROMETER_PANEL );
        double scaleX = size.width / panelNode.getWidth();
        double scaleY = size.height / panelNode.getHeight();
        AffineTransform panelTransform = new AffineTransform();
        panelTransform.scale( scaleX, scaleY );
        panelNode.setTransform( panelTransform );
        panelNode.setOffset( 0, 0 );
        PBounds pfb = panelNode.getFullBounds();

        // Title
        PText titleNode = new PText( title );
        titleNode.setFont( font );
        titleNode.setTextPaint( TITLE_COLOR );
        titleNode.setOffset( pfb.getX() + TITLE_X_MARGIN, pfb.getY() + TITLE_Y_MARGIN );

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
        _closeButtonWrapper = new PSwing( canvas, _closeButton );
        PBounds cb = _closeButtonWrapper.getFullBounds();
        _closeButtonWrapper.setOffset( pfb.getX() + pfb.getWidth() - cb.getWidth() - CLOSE_BUTTON_X_MARGIN, pfb.getY() + CLOSE_BUTTON_Y_MARGIN );

        // Start/Stop button
        _startStopButton = new JButton( SimStrings.get( "button.spectrometer.start" ) );
        _startStopButton.setFont( font );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                _isRunning = !_isRunning;
                if ( _isRunning ) {
                    start();
                }
                else {
                    stop();
                }
            }
        } );

        // Reset button
        JButton resetButton = new JButton( SimStrings.get( "button.spectrometer.reset" ) );
        resetButton.setFont( font );
        resetButton.addActionListener( new ActionListener() {
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
        layout.addComponent( resetButton, row, col++ );
        layout.addComponent( _snapshotButton, row, col );
        _buttonPanelWrapper = new PSwing( canvas, buttonPanel );
        _buttonPanelWrapper.addInputEventListener( new CursorHandler() );
        PBounds fb = _buttonPanelWrapper.getFullBounds();
        _buttonPanelWrapper.setOffset( pfb.getX() + pfb.getWidth() - fb.getWidth() - BUTTON_PANEL_X_MARGIN, 
                pfb.getY() + pfb.getHeight() - fb.getHeight() - BUTTON_PANEL_Y_MARGIN );

        // Display area
        _displayNode = new PPath();
        double w = pfb.getWidth() - ( 2 * DISPLAY_X_MARGIN );
        if ( w < 1 ) { w = 1; }
        double h = pfb.getHeight() - TITLE_Y_MARGIN - titleNode.getFullBounds().getHeight() - 
            BUTTON_PANEL_Y_MARGIN - _buttonPanelWrapper.getFullBounds().getHeight() - ( 2 * DISPLAY_Y_MARGIN );
        if ( h < 1 ) { h = 1; }
        Shape shape = new RoundRectangle2D.Double( 0, 0, w, h, DISPLAY_CORNER_RADIUS, DISPLAY_CORNER_RADIUS );
        _displayNode.setPathTo( shape );
        _displayNode.setPaint( Color.BLACK );
        _displayNode.setStroke( null );
        double x = DISPLAY_X_MARGIN;
        double y = TITLE_Y_MARGIN + titleNode.getFullBounds().getHeight() + DISPLAY_Y_MARGIN;
        _displayNode.setOffset( x, y );
        
        // Opacity
        buttonPanel.setOpaque( false );
        _startStopButton.setOpaque( false );
        resetButton.setOpaque( false );
        _snapshotButton.setOpaque( false );

        // Layering of nodes
        addChild( panelNode );
        addChild( _displayNode );
        addChild( titleNode );
        addChild( _closeButtonWrapper );
        addChild( _buttonPanelWrapper );
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

    //----------------------------------------------------------------------------
    // Button handlers
    //----------------------------------------------------------------------------
    
    private void start() {
        _startStopButton.setText( SimStrings.get( "button.spectrometer.stop" ) );
        //XXX
    }
    
    private void stop() {
        _startStopButton.setText( SimStrings.get( "button.spectrometer.start" ) );
        //XXX
    }
    
    private void reset() {
        //XXX
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
            titleNode.setTextPaint( SNAPSHOT_TITLE_COLOR );
            titleNode.setOffset( pfb.getX() + TITLE_X_MARGIN, pfb.getY() + TITLE_Y_MARGIN );
            titleNode.setText( title );
            titleNode.setTextPaint( SNAPSHOT_TITLE_COLOR );

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
