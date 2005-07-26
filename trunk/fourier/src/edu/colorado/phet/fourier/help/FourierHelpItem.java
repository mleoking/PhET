/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.help;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.RectangleUtils;

/**
 * Help items for the Fourier simulation.
 * Adapted from some code in the Forces 1D simulation.
 *
 * @author Sam Reid, Chris Malley
 * @version $Revision$
 */
public class FourierHelpItem extends CompositePhetGraphic implements PhetGraphicListener {
    
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
    private static final Color ARROW_FILL_COLOR = Color.YELLOW;
    private static final Color ARROW_BORDER_COLOR = Color.BLACK;
    private static final Stroke ARROW_STROKE = new BasicStroke( 1f );
    
    private static final Font TEXT_FONT = new Font( "Lucida Sans", Font.PLAIN, 14 );
    private static final Color TEXT_COLOR = Color.BLACK;
    
    private static final Color BACKGROUND_FILL_COLOR = ARROW_FILL_COLOR;
    private static final Color BACKGROUND_BORDER_COLOR = ARROW_BORDER_COLOR;
    private static final Stroke BACKGROUND_STROKE = ARROW_STROKE;
    private static final int BACKGROUND_CORNER_RADIUS = 15;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HTMLGraphic _textGraphic;
    private PhetShapeGraphic _backgroundGraphic;
    private PhetShapeGraphic _arrowGraphic;
    private int _direction;
    private int _arrowLength;
    private PhetGraphic _target;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FourierHelpItem( Component component, String html ) {
        super( component );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _textGraphic = new HTMLGraphic( component, TEXT_FONT, html, TEXT_COLOR );

        _arrowGraphic = new PhetShapeGraphic( component );
        _arrowGraphic.setColor( ARROW_FILL_COLOR );
        _arrowGraphic.setStroke( ARROW_STROKE );
        _arrowGraphic.setBorderColor( ARROW_BORDER_COLOR );
        
        _backgroundGraphic = new PhetShapeGraphic( component );
        Rectangle bounds = _textGraphic.getBounds();
        Rectangle b = RectangleUtils.expand( bounds, 4, 4 );
        _backgroundGraphic.setShape( new RoundRectangle2D.Double( b.x, b.y, b.width, b.height, BACKGROUND_CORNER_RADIUS, BACKGROUND_CORNER_RADIUS ) );
        _backgroundGraphic.setColor( BACKGROUND_FILL_COLOR );
        _backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        _backgroundGraphic.setStroke( BACKGROUND_STROKE );

        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );
        addGraphic( _arrowGraphic, ARROW_LAYER );
        addGraphic( _textGraphic, TEXT_LAYER );
        
        _direction = NONE;
        _target = null;
        _arrowLength = 0;
    }

    //----------------------------------------------------------------------------
    // PhetGraphicListener implementation
    //----------------------------------------------------------------------------
    
    public void phetGraphicChanged( PhetGraphic phetGraphic ) {
        updateTarget();
    }

    public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
        setVisible( _target.isVisible() );
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    public void pointAt( PhetGraphic target, int direction, int arrowLength ) {
        if ( _target != null ) {
            _target.removePhetGraphicListener( this );
        }
        _target = target;
        if ( _target != null ) {
            _target.addPhetGraphicListener( this );
        }
        _direction = direction;
        _arrowLength = arrowLength;
        updateArrow();
        updateTarget();
    }
    
    public void point( int direction, int arrowLength ) {
        pointAt( null, direction, arrowLength );
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    private void updateTarget() {
        if ( _target != null ) {
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
            layout( _target, this, 1, oppositeDirection );
        }
    }
    
    private void updateArrow() {
        
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
        
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( x, y ), ARROW_HEAD_WIDTH, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        _arrowGraphic.setShape( arrow.getShape() );
        layout( _backgroundGraphic, _arrowGraphic, 0, _direction );
    }
    
    //----------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------
    
    private static void layout( PhetGraphic fixed, PhetGraphic moveable, int separation, int direction ) {
        Rectangle targetBounds = fixed.getBounds();
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
