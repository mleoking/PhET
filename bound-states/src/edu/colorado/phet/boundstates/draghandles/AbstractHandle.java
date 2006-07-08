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

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * AbstractHandle is the abstract base class for all drag handles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractHandle extends PPath implements PropertyChangeListener {

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
    
    private static final NumberFormat DEFAULT_VALUE_FORMAT = new DecimalFormat( "0.00" );
    private static final float ARROW_SCALE = 24f; // change this to make the arrow bigger or smaller
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _orientation;
    private Point2D _registrationPoint;
    private ConstrainedDragHandler _dragHandler;
    
    private PText _valueNode;
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
    public AbstractHandle( int orientation ) {
        super();
        
        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        _orientation = orientation;
        
        // Drag handle representation
        Shape shape = createShape( orientation );
        setPathTo( shape );
        setPaint( BSConstants.DRAG_HANDLE_FILL_COLOR );
        setStroke( BSConstants.DRAG_HANDLE_STROKE );
        setStrokePaint( BSConstants.DRAG_HANDLE_STROKE_COLOR );
        
        // registration point @ center
        _registrationPoint = new Point2D.Double( getWidth() / 2, getHeight() / 2 );
        translate( -_registrationPoint.getX(), -_registrationPoint.getY() );
        
        // Value display 
        _valueFormat = DEFAULT_VALUE_FORMAT;
        _valueNode = new PText();
        addChild( _valueNode );
        _valueNode.setVisible( false );
        _valueNode.setPickable( false );
        _valueNode.setFont( BSConstants.DRAG_HANDLE_FONT );
        _valueNode.setTextPaint( BSConstants.DRAG_HANDLE_VALUE_COLOR );
        _valueNode.setText( "?" );
        Rectangle2D dragHandleBounds = getBounds();
        final double x = dragHandleBounds.getX() + dragHandleBounds.getWidth();
        final double y = dragHandleBounds.getY() - _valueNode.getHeight() + 3;
        _valueNode.translate( x, y );
        
        // Cursor behavior
        addInputEventListener( new CursorHandler() );

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
        
        // Show value while dragging or while mouse is over handle.
        addInputEventListener( new PBasicInputEventHandler() {

            private boolean _mouseIsPressed;
            private boolean _mouseIsInside;

            public void mousePressed( PInputEvent event ) {
                _mouseIsPressed = true;
                setValueVisible( true );
            }

            public void mouseReleased( PInputEvent event ) {
                _mouseIsPressed = false;
                setValueVisible( false || _mouseIsInside );
            }

            public void mouseEntered( PInputEvent event ) {
                _mouseIsInside = true;
                setValueVisible( true );
            }

            public void mouseExited( PInputEvent event ) {
                _mouseIsInside = false;
                setValueVisible( false || _mouseIsPressed );
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the orientation.
     * 
     * @return HORIZONTAL or VERTICAL
     */
    public int getOrientation() {
        return _orientation;
    }
    
    /**
     * Sets the displayed value.
     * This should be called by subclasses in their implementation of updateView.
     * 
     * @param value
     */
    protected void setValueDisplay( double value ) {
        String text = _valueFormat.format( value );
        _valueNode.setText( text );
    }
    
    /**
     * Sets the format of the value display.
     * 
     * @param format
     */
    public void setValueFormat( String format ) {
        _valueFormat = new DecimalFormat( format );
        updateView();
    }
    
    /**
     * Sets the color used to display values.
     * 
     * @param
     */
    public void setValueColor( Color color ) {
        _valueNode.setTextPaint( color );
    }
    
    /**
     * Toggles the display of the value that corresponds to the
     * drag handles position.
     * 
     * @param visible true or false 
     */
    private void setValueVisible( boolean visible ) {
        _valueNode.setVisible( visible );
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
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the model to match the drag handle.
     */
    protected abstract void updateModel();
    
    /*
     * Updates the drag handle to match the model.
     */
    protected abstract void updateView();
    
    /**
     * Updates the drag bounds.
     */
    protected abstract void updateDragBounds();
    
    //----------------------------------------------------------------------------
    // PropertChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the model whenever the drag handle is moved.
     * 
     * @param event
     */
    public void propertyChange( PropertyChangeEvent event ) {
        if ( event.getSource() == this ) {
            if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                updateModel();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Shape for the drag handle.
    //----------------------------------------------------------------------------
    
    /**
     * Gets the shape for the drag handle.
     * The default shape is a 2-headed arrow.
     * Override this method if you want your drag handle to have a different shape.
     * 
     * @param orientation
     * @return
     */
    protected static Shape createShape( int orientation ) {
        return createArrowShape( orientation, ARROW_SCALE );
    }
    
    /*
     * Gets the shape for an "arrow" drag handles, with a specified orientation and scale.
     * A scale of 1 will create an arrow who largest dimension is 1 pixel.
     * 
     * @param orientation
     * @param scale
     * @return
     */
    private static Shape createArrowShape( int orientation, float scale ) {
        
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
