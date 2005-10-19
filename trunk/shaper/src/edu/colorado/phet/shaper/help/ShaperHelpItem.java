/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.help;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.RectangleUtils;

/**
 * ShaperHelpItem is a help item for this simulation.
 *
 * @author Chris Malley
 * @version $Revision$
 */
public class ShaperHelpItem extends CompositePhetGraphic implements PhetGraphicListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int NONE = -1;
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    private static final double BACKGROUND_LAYER = 1;
    private static final double ARROW_LAYER = 2;
    private static final double TEXT_LAYER = 3;
    
    private static final int ARROW_HEAD_WIDTH = 10;
    private static final int ARROW_TAIL_WIDTH = 3;
    private static final Color ARROW_FILL_COLOR = new Color( 250, 250, 170, 175 );
    private static final Color ARROW_BORDER_COLOR = Color.BLACK;
    private static final Stroke ARROW_STROKE = new BasicStroke( 1f );
    
    private static final Font TEXT_FONT = new Font( "Lucida Sans", Font.PLAIN, 14 );
    private static final Color TEXT_COLOR = Color.BLACK;
    
    private static final Color BUBBLE_FILL_COLOR = ARROW_FILL_COLOR;
    private static final Color BUBBLE_BORDER_COLOR = ARROW_BORDER_COLOR;
    private static final Stroke BUBBLE_STROKE = ARROW_STROKE;
    private static final int BUBBLE_CORNER_RADIUS = 15;
    
    private static final int ARROW_SEPARATION = 0;
    private static final int TARGET_SEPARATION = 1;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetShapeGraphic _bubbleGraphic;
    private PhetShapeGraphic _arrowGraphic;
    private HTMLGraphic _textGraphic;
    
    private PhetGraphic _target;
    private Point _point;
    private int _direction;
    private int _arrowLength;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ShaperHelpItem( Component component, String html ) {
        super( component );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        _textGraphic = new HTMLGraphic( component );
        _textGraphic.setFont( TEXT_FONT );
        _textGraphic.setHTML( html );
        _textGraphic.setColor( TEXT_COLOR );
        
        _bubbleGraphic = new PhetShapeGraphic( component );
        Rectangle bounds = _textGraphic.getBounds();
        Rectangle b = RectangleUtils.expand( bounds, 4, 4 );
        _bubbleGraphic.setShape( new RoundRectangle2D.Double( b.x, b.y, b.width, b.height, BUBBLE_CORNER_RADIUS, BUBBLE_CORNER_RADIUS ) );
        _bubbleGraphic.setColor( BUBBLE_FILL_COLOR );
        _bubbleGraphic.setBorderColor( BUBBLE_BORDER_COLOR );
        _bubbleGraphic.setStroke( BUBBLE_STROKE );

        _arrowGraphic = new PhetShapeGraphic( component );
        _arrowGraphic.setColor( ARROW_FILL_COLOR );
        _arrowGraphic.setStroke( ARROW_STROKE );
        _arrowGraphic.setBorderColor( ARROW_BORDER_COLOR );      

        addGraphic( _bubbleGraphic, BACKGROUND_LAYER );
        addGraphic( _arrowGraphic, ARROW_LAYER );
        addGraphic( _textGraphic, TEXT_LAYER );
       
        _target = null;
        _point = null;
        _direction = NONE;
        _arrowLength = 0;
        
        setIgnoreMouse( true );
    }
    
    //----------------------------------------------------------------------------
    // PhetGraphicListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the layout when the target graphic changes.
     * 
     * @param phetGraphic
     */
    public void phetGraphicChanged( PhetGraphic phetGraphic ) {
        setThisLocation();
    }

    /**
     * Updates visibility when the target's visibility changes.
     * 
     * @param phetGraphic
     */
    public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
        setVisible( _target.isVisible() );
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    /**
     * Points at a target graphic.
     * 
     * @param target
     * @param direction
     * @param arrowLength
     */
    public void pointAt( PhetGraphic target, int direction, int arrowLength ) {
        assert( isValidDirection( direction ) );
        
        if ( _target != null ) {
            _target.removePhetGraphicListener( this );
        }
        _target = target;
        if ( _target != null ) {
            _target.addPhetGraphicListener( this );
        }
        
        _direction = direction;
        _arrowLength = arrowLength;
        
        setArrowLocation();
        setThisLocation();
    }
    
    /**
     * Points at a location (a Point).
     * 
     * @param point
     * @param direction
     * @param arrowLength
     */
    public void pointAt( Point point, int direction, int arrowLength ) {
        pointAt( point.x, point.y, direction, arrowLength );
    }
    
    /**
     * Points at a location (xy coordinates).
     * 
     * @param x
     * @param y
     * @param direction
     * @param arrowLength
     */
    public void pointAt( int x, int y, int direction, int arrowLength ) {
        _point = new Point( x, y );
        pointAt( (PhetGraphic) null, direction, arrowLength );
    }
    
    /**
     * Determines if a direction value is valid.
     * 
     * @param direction
     * @return  true or false
     */
    public boolean isValidDirection( int direction ) {
        return ( direction == UP || direction == DOWN || direction == LEFT || direction == RIGHT );
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    /**
     * Updates the layout of the arrow in relation to the help bubble.
     */
    private void setArrowLocation() {
        
        int x, y;
        switch ( _direction ) {
        case UP:
            x = 0;
            y = -_arrowLength;
            break;
        case DOWN:
            x = 0;
            y = _arrowLength;
            break;
        case LEFT:
            x = -_arrowLength;
            y = 0;
            break;
        case RIGHT:
            x = _arrowLength;
            y = 0;
            break;
        default:
            throw new IllegalArgumentException( "illegal direction: " + _direction );
        }
        
        Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( x, y ), ARROW_HEAD_WIDTH, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        _arrowGraphic.setShape( arrow.getShape() );
        layout( _bubbleGraphic.getBounds(), _arrowGraphic, _direction, ARROW_SEPARATION );
    }
    
    /**
     * Updates the layout of this help item in relation to the target.
     */
    private void setThisLocation() {

        int oppositeDirection;
        switch ( _direction ) {
        case UP:
            oppositeDirection = DOWN;
            break;
        case DOWN:
            oppositeDirection = UP;
            break;
        case LEFT:
            oppositeDirection = RIGHT;
            break;
        case RIGHT:
            oppositeDirection = LEFT;
            break;
        default:
            throw new IllegalArgumentException( "illegal direction: " + _direction );
        }

        if ( _target != null ) {
            layout( _target.getBounds(), this, oppositeDirection, TARGET_SEPARATION );
        }
        else if ( _point != null ) {
            Rectangle pointBounds = new Rectangle( _point.x, _point.y, 1, 1 );
            layout( pointBounds, this, oppositeDirection, 0 );
        }
    }
    
    //----------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------
    
    /**
     * Performs a relative layout of two graphics.  
     * The "fixed" graphic does not move.
     * The "moveable" graphic is repositioned relative to the fixed graphic's
     * location and the specified direction.
     * 
     * @param fixed
     * @param moveable
     * @param direction
     * @param separation
     */
    private static void layout( Rectangle targetBounds, PhetGraphic moveable, int direction, int separation ) {

        Rectangle movableBounds = moveable.getBounds();
        
        Point connector;
        int x, y;
        switch ( direction ) {
        case UP:
            connector = RectangleUtils.getTopCenter( targetBounds );
            x = connector.x - movableBounds.width / 2;
            y = connector.y - movableBounds.height - separation;
            break;
        case DOWN:
            connector = RectangleUtils.getBottomCenter( targetBounds );
            x = connector.x - movableBounds.width / 2;
            y = connector.y + separation;
            break;
        case LEFT:
            connector = RectangleUtils.getLeftCenter( targetBounds );
            x = connector.x - movableBounds.width - separation;
            y = connector.y - movableBounds.height / 2;
            break;
        case RIGHT:
            connector = RectangleUtils.getRightCenter( targetBounds );
            x = connector.x + separation;
            y = connector.y - movableBounds.height / 2;
            break;
        default:
            throw new IllegalArgumentException( "illegal direction: " + direction );
        }

        int origX = movableBounds.x;
        int origY = movableBounds.y;
        int dx = x - origX;
        int dy = y - origY;
        Point location = moveable.getLocation();
        moveable.setLocation( location.x + dx, location.y + dy );
    }
}
