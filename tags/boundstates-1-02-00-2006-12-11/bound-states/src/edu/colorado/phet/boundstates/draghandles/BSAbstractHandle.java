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
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * AbstractHandle is the abstract base class for all drag handles.
 * A handle looks looks like an arrow with a head on both ends.
 * It has either a horizontal or vertical orientation.
 * When you roll the mouse over a handle, or while you're dragging
 * a handle, it highlights and shows the value of the parameter that
 * the handle is controlling.  The value is positioned above and to
 * the right of the handle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractHandle extends PPath implements PropertyChangeListener {

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
    
    private static final NumberFormat DEFAULT_VALUE_NUMBER_FORMAT = new DecimalFormat( "0.0" );
    private static final String DEFAULT_VALUE_PATTERN = "{0}";
    
    private static final float ARROW_SCALE = 24f; // change this to make the arrow bigger or smaller
    
    private static final Color DEFAULT_NORMAL_COLOR = Color.WHITE;
    private static final Color DEFAULT_HILITE_COLOR = Color.YELLOW;
    private static final Color DEFAULT_VALUE_COLOR = Color.BLACK;
    private static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _orientation;
    private Point2D _registrationPoint;
    private ConstrainedDragHandler _dragHandler;
    
    private HTMLNode _valueNode;
    private NumberFormat _valueNumberFormat;
    private String _valuePattern;
    
    private Color _normalColor;
    private Color _hiliteColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a drag handle with a specified orientation.
     * 
     * @param orientation HORIZONTAL or VERTICAL
     * @throws IllegalArgumentException
     */
    public BSAbstractHandle( int orientation ) {
        super();
        
        _normalColor = DEFAULT_NORMAL_COLOR;
        _hiliteColor = DEFAULT_HILITE_COLOR;

        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        _orientation = orientation;
        
        // Drag handle representation
        Shape shape = createShape( orientation );
        setPathTo( shape );
        setPaint( DEFAULT_NORMAL_COLOR );
        setStroke( BSConstants.DRAG_HANDLE_STROKE );
        setStrokePaint( DEFAULT_STROKE_COLOR );
        
        // registration point @ center
        _registrationPoint = new Point2D.Double( getWidth() / 2, getHeight() / 2 );
        translate( -_registrationPoint.getX(), -_registrationPoint.getY() );
        
        // Value display 
        _valueNumberFormat = DEFAULT_VALUE_NUMBER_FORMAT;
        _valuePattern = DEFAULT_VALUE_PATTERN;
        _valueNode = new HTMLNode();
        addChild( _valueNode );
        _valueNode.setVisible( false );
        _valueNode.setPickable( false );
        _valueNode.setFont( BSConstants.DRAG_HANDLE_FONT );
        _valueNode.setHTMLColor( DEFAULT_VALUE_COLOR );
        _valueNode.setHTML( "?" );
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
        addInputEventListener( new ShowValueHandler() );
        
        // Make drag handle hilite when the mouse is over it or it's being dragged.
        addInputEventListener( new HiliteHandler() );
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
        String valueText = _valueNumberFormat.format( value );
        Object[] args = { valueText };
        String displayText = MessageFormat.format( _valuePattern, args );
        _valueNode.setHTML( displayText );
    }
    
    /**
     * Sets the format of the numberic value displayed.
     * This is used to set the number of digits.
     * See NumberFormat or DecimalFormat for a description of the format's syntax.
     * 
     * @param format
     */
    public void setValueNumberFormat( String format ) {
        _valueNumberFormat = new DecimalFormat( format );
        updateView();
    }
    
    /**
     * Sets the pattern used to display the value.
     * See MessageFormat for a description of the pattern syntax.
     * 
     * @param pattern
     */
    public void setValuePattern( String pattern ) {
        _valuePattern = pattern;
        updateView();
    }
    
    /**
     * Sets the color used to display values.
     * 
     * @param color
     */
    public void setValueColor( Color color ) {
        _valueNode.setHTMLColor( color );
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
     * @return Point2D
     */
    public Point2D getGlobalPosition() {
        Rectangle2D bounds = getGlobalBounds();
        double x = bounds.getX() + _registrationPoint.getX();
        double y = bounds.getY() + _registrationPoint.getY();
        return new Point2D.Double( x, y );          
    }
    
    /**
     * Sets the color scheme.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {
        setNormalColor( colorScheme.getDragHandleColor() );
        setHiliteColor( colorScheme.getDragHandleHiliteColor() );
        setValueColor( colorScheme.getDragHandleValueColor() );
    }
    
    /**
     * Sets the color used for the "normal" (unhighlighted) handle.
     * 
     * @param color
     */
    public void setNormalColor( Color color ) {
        _normalColor = color;
        setPaint( _normalColor );
    }
    
    /**
     * Sets the color used for the highlighted handle.
     * 
     * @param color
     */
    public void setHiliteColor( Color color ) { 
        _hiliteColor = color;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the drag bounds.
     */
    protected abstract void updateDragBounds();
    
    /*
     * Updates the model to match the drag handle.
     */
    protected abstract void updateModel();
    
    /*
     * Updates the drag handle to match the model.
     */
    protected abstract void updateView();
    
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
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Creates a number format string with the specified number 
     * of decimal places.  For example, if significantDecimalPlaces=2,
     * then the string returned is "0.00".
     * 
     * @param significantDecimalPlaces
     * @return String, see NumberFormat for syntax
     */
    protected static String createNumberFormat( int significantDecimalPlaces ) {
        String numberFormat = "0.";
        for ( int i = 0; i < significantDecimalPlaces; i++ ) {
            numberFormat += "0";
        }
        return numberFormat;
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Shows the value while dragging the handle, 
     * or while the mouse is inside the handle.
     */
    private class ShowValueHandler extends PBasicInputEventHandler {
        
        private boolean _mouseIsPressed;
        private boolean _mouseIsInside;

        public ShowValueHandler() {
            super();
        }
        
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
    }
    
    /*
     * Hilites the handle while dragging the handle, 
     * or while the mouse is inside the handle.
     */
    private class HiliteHandler extends PBasicInputEventHandler {
        
        private boolean _mouseIsPressed;
        private boolean _mouseIsInside;

        public HiliteHandler() {
            super();
        }
        
        public void mousePressed( PInputEvent event ) {
            _mouseIsPressed = true;
            setPaint( _hiliteColor );
        }

        public void mouseReleased( PInputEvent event ) {
            _mouseIsPressed = false;
            if ( !_mouseIsInside ) {
                setPaint( _normalColor );
            }
        }

        public void mouseEntered( PInputEvent event ) {
            _mouseIsInside = true;
            setPaint( _hiliteColor );
        }

        public void mouseExited( PInputEvent event ) {
            _mouseIsInside = false;
            if ( !_mouseIsPressed ) {
                setPaint( _normalColor );
            }
        }
    }
}
