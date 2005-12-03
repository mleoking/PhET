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

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.piccolo.ConstrainedDragHandler;
import edu.colorado.phet.quantumtunneling.piccolo.ImageNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * DragHandle is the abstract base class for all drag handles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class DragHandle extends ImageNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
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
        
        // Set the image...
        if ( orientation == HORIZONTAL ) {
            setImageByResourceName( QTConstants.IMAGE_DRAG_HANDLE_HORIZONTAL );
        }
        else {
            setImageByResourceName( QTConstants.IMAGE_DRAG_HANDLE_VERTICAL );
        }
        
        // registration point @ center
        _registrationPoint = new Point2D.Double( getWidth() / 2, getHeight() / 2 );
        
        translate( -_registrationPoint.getX(), -_registrationPoint.getY() );
        
        addInputEventListener( new CursorHandler() );
        _dragHandler = new ConstrainedDragHandler();
        addInputEventListener( _dragHandler );
        
        // Configure the drag handler...
        _dragHandler.setTreatAsPointEnabled( true );
        _dragHandler.setNodeCenter( _registrationPoint.getX(), _registrationPoint.getY() );
        if ( orientation == HORIZONTAL ) {
            _dragHandler.setVerticalLockEnabled( true );
        }
        else {
            _dragHandler.setHorizontalLockEnabled( true );
        }
        
        _textNode = null;
        _textFormat = DEFAULT_TEXT_FORMAT;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setTextEnabled( boolean enabled ) {
        if ( enabled && _textNode == null ) {
            _textNode = new PText();
            addChild( _textNode );
            _textNode.setPickable( false );
            _textNode.setFont( TEXT_FONT );
            _textNode.setTextPaint( TEXT_COLOR );
            _textNode.setText( "0.0" );
            Rectangle2D dragHandleBounds = getBounds();
            if ( _orientation == HORIZONTAL ) {
                _textNode.translate( dragHandleBounds.getX() + dragHandleBounds.getWidth() + 1, 0 );
            }
            else {
                _textNode.translate( 0, dragHandleBounds.getY() - ( _textNode.getHeight() + 1 ) );
            }
            updateText();
        }
        else if ( !enabled && _textNode != null ) {
            removeChild( _textNode );
            _textNode = null;
        }
    }
    
    public boolean isTextEnabled() {
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
