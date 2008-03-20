/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.collision.CollisionDetector;
import edu.colorado.phet.faraday.model.FaradayObservable;


/**
 * FaradayMouseHandler is the mouse handler shared by all draggable objects.
 * Besides handling simple translations, it can optionally handle collision
 * detection and constraining the drag boundaries.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayMouseHandler extends MouseInputAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FaradayObservable _modelComponent;
    private PhetGraphic _viewComponent;
    private boolean _dragEnabled;
    private Point _previousPoint;
    private Rectangle _dragBounds;
    private CollisionDetector _collisionDetector;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param modelComponent the model to be translated
     * @param viewComponent the view that is the drag target and is associated with the model
     */
    public FaradayMouseHandler( FaradayObservable modelComponent, PhetGraphic viewComponent ) {
        super();
        
        assert( modelComponent != null );
        assert( viewComponent != null );
        
        _modelComponent = modelComponent;
        _viewComponent = viewComponent;
        _dragBounds = null;
        _collisionDetector = null;
        
        _dragEnabled = true;
        _previousPoint = new Point();
    }
  
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the collision detector, which enables collision detection.
     * 
     * @param collisionDetector the collision detector
     */
    public void setCollisionDetector( CollisionDetector collisionDetector ) {
        _collisionDetector = collisionDetector;
    }
    
    /**
     * Set the drag bounds. 
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setDragBounds( int x, int y, int width, int height ) {
        if ( _dragBounds == null ) {
            _dragBounds = new Rectangle();
        }
        _dragBounds.setBounds( x, y, width, height );
    }

    //----------------------------------------------------------------------------
    // MouseInputAdapter overrides
    //----------------------------------------------------------------------------
    
    /**
     * Handles mouse pressed events.
     * 
     * @param event
     */
    public void mousePressed( MouseEvent event ) {
        _dragEnabled = true;
        _previousPoint.setLocation( event.getPoint() );
    }
    
    /**
     * Handles mouse dragged events.
     * Collision detection and drag boundary checking are enabled if a 
     * collision detector and drag boundaries have been provided.
     * If the object is outside the drag boundaries, or in a collision
     * situation, then the drag is vetoed.  Otherwise the model's location
     * is updated.
     */
    public void mouseDragged( MouseEvent event ) {

        if ( !_dragEnabled && _viewComponent.contains( event.getX(), event.getY() ) ) {
            // Re-enable dragging when the mouse cursor is inside the object.
            _dragEnabled = true;
            _previousPoint.setLocation( event.getPoint() );
        }
        
        if ( _dragEnabled ) {

            int dx = event.getX() - _previousPoint.x;
            int dy = event.getY() - _previousPoint.y;
            
            boolean outOfBounds = false;
            if ( _dragBounds != null ) {
                // Is the mouse cursor outside the drag bounds?
                outOfBounds = !_dragBounds.contains( event.getPoint() );
            }

            boolean collision = false;
            if ( _collisionDetector != null ) {
                // This supports moving objects out of a situation in which they already collide.
                boolean collidesNow = _collisionDetector.collidesNow();
                boolean wouldCollide = _collisionDetector.wouldCollide( dx, dy );
                collision = !collidesNow && wouldCollide;
            }

            if ( outOfBounds || collision ) {
                // Ignore the translate if the mouse is outside the apparatus panel or 
                // if the translate would result in a collision.
                _dragEnabled = false;
            }
            else {
                // Translate the model.
                double x = _modelComponent.getX() + dx;
                double y = _modelComponent.getY() + dy;
                _modelComponent.setLocation( x, y );
                _previousPoint.setLocation( event.getPoint() );
            }
        }
    }
}
