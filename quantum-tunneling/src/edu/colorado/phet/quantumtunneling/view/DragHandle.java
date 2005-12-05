/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.piccolo.ConstrainedDragHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * DragHandle is the abstract base class for all drag handles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class DragHandle extends PPath {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    public static final Shape HANDLE_SHAPE = new Rectangle2D.Double( 0, 0, 7, 7 );
    public static final Color HANDLE_FILL_COLOR = new Color( 0, 0, 0, 0 ); // transparent
    public static final Stroke HANDLE_STROKE = new BasicStroke( 1f );
    public static final Color HANDLE_STROKE_COLOR = new Color( 0, 0, 0, 175 );
    
    private static final Font TEXT_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final NumberFormat DEFAULT_TEXT_FORMAT = new DecimalFormat( "0.0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _orientation;
    private Point2D _registrationPoint;
    private ConstrainedDragHandler _dragHandler;
    private PText _textNode;
    private NumberFormat _textFormat;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param orientation HORIZONTAL or VERTICAL
     * @throws IllegalArgumentException
     */
    public DragHandle( int orientation ) {
        super();
        
        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        _orientation = orientation;
        
        // Visual representation
        setPathTo( HANDLE_SHAPE );
        setPaint( HANDLE_FILL_COLOR );
        setStroke( HANDLE_STROKE );
        setStrokePaint( HANDLE_STROKE_COLOR );
        
        // registration point @ center
        _registrationPoint = new Point2D.Double( getWidth() / 2, getHeight() / 2 );
        translate( -_registrationPoint.getX(), -_registrationPoint.getY() );
        
        // Custom cursor to indicate drag direction
        try {
            String cursorResourceName = null;
            if ( orientation == HORIZONTAL ) {
                cursorResourceName = QTConstants.IMAGE_DRAG_CURSOR_HORIZONTAL;
            }
            else {
                cursorResourceName = QTConstants.IMAGE_DRAG_CURSOR_VERTICAL;
            }
            BufferedImage cursorImage = ImageLoader.loadBufferedImage( cursorResourceName );
            int hotX = cursorImage.getWidth() / 2;
            int hotY = cursorImage.getHeight() / 2;
            Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor( cursorImage, new Point( hotX, hotY ), "DragHandleCursor" );
            addInputEventListener( new CursorHandler( cursor ) );
        }
        catch ( IOException e ) {
            addInputEventListener( new CursorHandler() );
            e.printStackTrace();
        }

        // Drag handler
        _dragHandler = new ConstrainedDragHandler();
        addInputEventListener( _dragHandler );
        _dragHandler.setTreatAsPointEnabled( true );
        _dragHandler.setNodeCenter( _registrationPoint.getX(), _registrationPoint.getY() );
        if ( orientation == HORIZONTAL ) {
            _dragHandler.setVerticalLockEnabled( true );
        }
        else {
            _dragHandler.setHorizontalLockEnabled( true );
        }
        
        // Value display
        _textNode = null;
        _textFormat = DEFAULT_TEXT_FORMAT;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Toggles the display of the value that corresponds to the
     * drag handles position.
     * 
     * @param enabled true or false 
     */
    public void setShowValueEnabled( boolean enabled ) {
        if ( enabled && _textNode == null ) {
            _textNode = new PText();
            addChild( _textNode );
            _textNode.setPickable( false );
            _textNode.setFont( TEXT_FONT );
            _textNode.setTextPaint( TEXT_COLOR );
            _textNode.setText( "0.0" );
            Rectangle2D dragHandleBounds = getBounds();
            double x = dragHandleBounds.getX() + dragHandleBounds.getWidth() + 3;
            double y = dragHandleBounds.getY() - _textNode.getHeight();
            _textNode.translate( x, y );
            updateText();
        }
        else if ( !enabled && _textNode != null ) {
            removeChild( _textNode );
            _textNode = null;
        }
    }
    
    /**
     * Is the value displayed?
     * 
     * @return true or false
     */
    public boolean isShowValueEnabled() {
        return ( _textNode != null );
    }
    
    /**
     * Gets the orientation.
     * 
     * @return HORIZONTAL or VERTICAL
     */
    public int getOrientation() {
        return _orientation;
    }
    
    /**
     * Sets the drag bounds.
     * 
     * @param bounds
     */
    public void setDragBounds( Rectangle2D bounds ) {
        _dragHandler.setDragBounds( bounds );
    }
    
    /**
     * Sets the position of the node, in the global coordinate system.
     * 
     * @param globalPoint
     */
    public void setGlobalPosition( Point2D globalPoint ) {
        Point2D currentGlobalPosition = getGlobalPosition();
        double deltaX = globalPoint.getX() - currentGlobalPosition.getX();
        double deltaY = globalPoint.getY() - currentGlobalPosition.getY();
        translate( deltaX, deltaY );
    }
    
    /**
     * Gets the position of the node, in the global coordinate system.
     * 
     * @return
     */
    public Point2D getGlobalPosition() {
        Rectangle2D bounds = getGlobalBounds();
        double x = bounds.getX() + _registrationPoint.getX();
        double y = bounds.getY() + _registrationPoint.getY();
        return new Point2D.Double( x, y );          
    }
    
    /**
     * Gets the value, in model coordinates, that is represented by
     * the drag handles current location.
     * 
     * @return
     */
    public abstract double getModelValue();
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    protected void updateText() {
        if ( _textNode != null ) {
            double value = getModelValue();
            String text = _textFormat.format( value );
            _textNode.setText( text );
        }
    }
}
