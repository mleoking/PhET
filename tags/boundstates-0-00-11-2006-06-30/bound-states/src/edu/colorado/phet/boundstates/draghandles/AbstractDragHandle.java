/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.draghandles;

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

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * AbstractDragHandle is the abstract base class for all drag handles.
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
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    private static final NumberFormat DEFAULT_VALUE_FORMAT = new DecimalFormat( "0.0" );
    private static final float HANDLE_ARROW_SCALE = 24f;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _orientation;
    private Point2D _registrationPoint;
    private ConstrainedDragHandler _dragHandler;
    
    private PText _valueNode;
    private Color _valueColor;
    private NumberFormat _valueFormat;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a drag handle with a specified orientation.
     * 
     * @param orientation HORIZONTAL or VERTICAL
     * @throws IllegalArgumentException
     */
    public AbstractDragHandle( int orientation ) {
        super();
        
        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        _orientation = orientation;
        
        // Visual representation
        Shape shape = getArrowShape( orientation );
        setPathTo( shape );
        setPaint( BSConstants.DRAG_HANDLE_FILL_COLOR );
        setStroke( BSConstants.DRAG_HANDLE_STROKE );
        setStrokePaint( BSConstants.DRAG_HANDLE_STROKE_COLOR );
        
        // registration point @ center
        _registrationPoint = new Point2D.Double( getWidth() / 2, getHeight() / 2 );
        translate( -_registrationPoint.getX(), -_registrationPoint.getY() );
        
        // Cursor
        addInputEventListener( new CursorHandler() );

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
            _valueNode.setFont( BSConstants.DRAG_HANDLE_FONT );
            _valueNode.setTextPaint( _valueColor );
            _valueNode.setText( "0.0" );
            Rectangle2D dragHandleBounds = getBounds();
            final double x = dragHandleBounds.getX() + dragHandleBounds.getWidth();
            final double y = dragHandleBounds.getY() - _valueNode.getHeight() + 3;
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
    // Shapes for the drag handle.
    //----------------------------------------------------------------------------
    
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
}
