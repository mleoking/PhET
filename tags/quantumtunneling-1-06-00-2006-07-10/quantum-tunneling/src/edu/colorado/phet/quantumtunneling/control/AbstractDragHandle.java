/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * AbstractDragHandle is the abstract base class for all drag handles.
 * <p>
 * There are two "looks" supported by this class.
 * The drag handles can either look like standard control points,
 * or they can look like arrows.  If controls points are used,
 * then the cursor changes to arrows to indicate the drag 
 * direction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractDragHandle extends PPath implements PropertyChangeListener {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    /* Specifies a horizontal drag handle */
    public static final int HORIZONTAL = 0;
    
    /* Specified a vertical drag handle */
    public static final int VERTICAL = 1;
    
    /* Drag handle will look like a control point */
    public static final int LOOK_CONTROL_POINT = 2;
    
    /* Drag handle will look like an arrow */
    public static final int LOOK_ARROW = 3;
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    private static final int DEFAULT_LOOK = LOOK_ARROW;
    
    private static final NumberFormat DEFAULT_VALUE_FORMAT = new DecimalFormat( "0.0" );
    
    private static final float HANDLE_ARROW_SCALE = 24f;
    private static final float CURSOR_ARROW_SCALE = 18f;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _orientation;
    private int _look;
    private Point2D _registrationPoint;
    private ConstrainedDragHandler _dragHandler;
    
    private PText _valueNode;
    private Color _valueColor;
    private NumberFormat _valueFormat;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
   
    /**
     * Creates a drag handle with a sepecified orientation and default look.
     * 
     * @param orientation
     */
    public AbstractDragHandle( int orientation ) {
        this( orientation, DEFAULT_LOOK );
    }
    
    /**
     * Creates a drag handle with a specified orientation and look.
     * 
     * @param orientation HORIZONTAL or VERTICAL
     * @throws IllegalArgumentException
     */
    public AbstractDragHandle( int orientation, int look ) {
        super();
        
        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        _orientation = orientation;
        
        if ( look != LOOK_CONTROL_POINT && look != LOOK_ARROW ) {
            throw new IllegalArgumentException( "invalid look: " + look );
        }
        _look = look;
        
        // Visual representation
        Shape shape = null;
        if ( _look == LOOK_CONTROL_POINT ) {
            shape = getControlPointShape( orientation );
        }
        else {
            shape = getArrowShape( orientation );
        }
        setPathTo( shape );
        setPaint( QTConstants.DRAG_HANDLE_FILL_COLOR );
        setStroke( QTConstants.DRAG_HANDLE_STROKE );
        setStrokePaint( QTConstants.DRAG_HANDLE_STROKE_COLOR );
        
        // registration point @ center
        _registrationPoint = new Point2D.Double( getWidth() / 2, getHeight() / 2 );
        translate( -_registrationPoint.getX(), -_registrationPoint.getY() );
        
        // Custom cursor to indicate drag direction
        if ( _look == LOOK_CONTROL_POINT ) {
            // if the handle is not an arrow, use the cursor to indicate drag direction
            Cursor cursor = getArrowCursor( orientation );
            addInputEventListener( new CursorHandler( cursor ) );
        }
        else {
            // if the handle is an arrow, use the default cursor
            addInputEventListener( new CursorHandler() );
        }

        // Drag handler
        _dragHandler = new ConstrainedDragHandler();
        addInputEventListener( _dragHandler );
        _dragHandler.setTreatAsPointEnabled( true );
        _dragHandler.setUseFullBounds( false );
        _dragHandler.setNodeCenter( _registrationPoint.getX(), _registrationPoint.getY() );
        if ( orientation == HORIZONTAL ) {
            _dragHandler.setVerticalLockEnabled( true );
        }
        else {
            _dragHandler.setHorizontalLockEnabled( true );
        }
        
        // Value display
        _valueNode = null;
        _valueColor = Color.BLACK;
        _valueFormat = DEFAULT_VALUE_FORMAT;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color used to display values.
     * 
     * @param
     */
    public void setValueColor( Color color ) {
        _valueColor = color;
        if ( _valueNode != null ) {
            _valueNode.setTextPaint( color );
        }
    }
    
    /**
     * Toggles the display of the value that corresponds to the
     * drag handles position.
     * 
     * @param visible true or false 
     */
    public void setValueVisible( boolean visible ) {
        if ( visible && _valueNode == null ) {
            _valueNode = new PText();
            addChild( _valueNode );
            _valueNode.setPickable( false );
            _valueNode.setFont( QTConstants.DRAG_HANDLE_FONT );
            _valueNode.setTextPaint( _valueColor );
            _valueNode.setText( "0.0" );
            Rectangle2D dragHandleBounds = getBounds();
            double x = 0;
            double y = 0;
            if ( _look == LOOK_CONTROL_POINT ) {
                x = dragHandleBounds.getX() + dragHandleBounds.getWidth() + 3;
                y = dragHandleBounds.getY() - _valueNode.getHeight();
            }
            else {
                x = dragHandleBounds.getX() + dragHandleBounds.getWidth();
                y = dragHandleBounds.getY() - _valueNode.getHeight() + 3;
            }
            _valueNode.translate( x, y );
            updateText();
        }
        else if ( !visible && _valueNode != null ) {
            removeChild( _valueNode );
            _valueNode = null;
        }
    }
    
    /**
     * Is the value displayed?
     * 
     * @return true or false
     */
    public boolean isValueVisible() {
        return ( _valueNode != null );
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
     * Sets the format of the value display.
     * @param format
     */
    public void setValueFormat( String format ) {
        _valueFormat = new DecimalFormat( format );
        updateText();
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
    
    /*
     * Gets the value, in model coordinates, that is represented by
     * the drag handle's current location.
     * 
     * @return
     */
    protected abstract double getModelValue();
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the model to match the drag handle's position.
     */
    protected abstract void updateModel();
    
    /*
     * Updates the value display.
     */
    protected void updateText() {
        if ( _valueNode != null ) {
            double value = getModelValue();
            String text = _valueFormat.format( value );
            _valueNode.setText( text );
        }
    }
    
    //----------------------------------------------------------------------------
    // PropertChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the model and value display whenever the drag handle is moved.
     * 
     * @param event
     */
    public void propertyChange( PropertyChangeEvent event ) {
        if ( event.getSource() == this ) {
            if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                updateModel();
                updateText();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Various shapes for the drag handle and cursor.
    //----------------------------------------------------------------------------
    
    /*
     * Gets the shape for a "control point" drag handle.
     * 
     * @param orientation
     * @return
     */
    private static Shape getControlPointShape( int orientation ) {
        return new Rectangle2D.Double( 0, 0, 7, 7 );
    }
    
    /*
     * Gets the shape for an "arrow" drag handle.
     * 
     * @param orientation
     * @return
     */
    private static Shape getArrowShape( int orientation ) {
        return getArrowShape( orientation, HANDLE_ARROW_SCALE );
    }
    
    /*
     * Gets the shape for an "arrow" drag handles,
     * with a specified orientation and scale.
     * A scale of 1 will provide an arrow who largest
     * dimension is 1.
     * 
     * @param orientation
     * @param scale
     * @return
     */
    private static Shape getArrowShape( int orientation, float scale ) {
        
        Shape shape = null;
        
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( .25f * scale, .33f * scale );
        path.lineTo( .08f * scale, .33f * scale );
        path.lineTo( .08f * scale, .67f * scale );
        path.lineTo( .25f * scale, .67f * scale );
        path.lineTo( 0 * scale, 1 * scale );
        path.lineTo( -.25f * scale, .67f * scale );
        path.lineTo( -.08f * scale, .67f * scale );
        path.lineTo( -.08f * scale, .33f * scale );
        path.lineTo( -.25f * scale, .33f * scale );
        path.closePath();
        
        if ( orientation == VERTICAL ) {
            shape = path;
        }
        else {
            // Convert to a horizontal arrow
            AffineTransform transform = new AffineTransform();
            transform.rotate( Math.toRadians( 90 ) );
            shape = transform.createTransformedShape( path );
        }
        
        return shape;
    }
    
    /*
     * Gets an arrow cursor, to indicate drag direction.
     * 
     * @param orientation
     * @return
     */
    private static Cursor getArrowCursor( int orientation ) {
        
        // Get the arrow shape
        Shape shape = getArrowShape( orientation, CURSOR_ARROW_SCALE );

        // Draw the shape to an image
        Rectangle bounds = shape.getBounds();
        BufferedImage image = new BufferedImage( bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.translate( -bounds.x, -bounds.y );
        g2.setPaint( Color.BLACK );
        g2.fill( shape );
        
        // Use the image to create a cursor
        Point hotSpot = new Point( image.getWidth() / 2, image.getHeight() / 2 );
        String name = "DragHandleCursor";
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor( image, hotSpot, name );
        return cursor;
    }
}
