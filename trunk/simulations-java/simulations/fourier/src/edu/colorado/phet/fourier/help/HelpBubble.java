// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.help;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;

/**
 * HelpBubble puts some text in a bubble, with an optional arrow.
 * <p>
 * By default, a HelpBubble is created with no arrow, and 
 * you position it by calling setLocation.
 * <p>
 * To add an arrow to a HelpBubble, call one of the pointAt methods.
 * The arrow can point at either a fixed location, or a target graphic.  
 * If pointing at a target graphic, the location and visibilty of the 
 * HelpBubble is synchronized with the location and visibilty of
 * the target.
 * <p>
 * There is limited control for changing the "look" of a HelpBubble.
 * You can change its text, font and color scheme.
 *
 * @author Chris Malley
 * @version $Revision$
 */
public class HelpBubble extends CompositePhetGraphic implements PhetGraphicListener {
    
    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    // Arrow positions, relative to the text bubble.
    public static final int TOP_LEFT = 0;
    public static final int TOP_CENTER = 1;
    public static final int TOP_RIGHT = 2;
    public static final int BOTTOM_LEFT = 3;
    public static final int BOTTOM_CENTER = 4;
    public static final int BOTTOM_RIGHT = 5;
    public static final int LEFT_TOP = 6;
    public static final int LEFT_CENTER = 7;
    public static final int LEFT_BOTTOM = 8;
    public static final int RIGHT_TOP = 9;
    public static final int RIGHT_CENTER = 10;
    public static final int RIGHT_BOTTOM = 11;
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    // Graphics layers
    private static final double ARROW_LAYER = 1;
    private static final double BUBBLE_LAYER = 2;
    
    // Arrow parameters
    private static final int ARROW_HEAD_WIDTH = 10;
    private static final int ARROW_TAIL_WIDTH = 3;
    private static final Color ARROW_FILL_COLOR = new Color( 250, 250, 170, 175 );
    private static final Color ARROW_BORDER_COLOR = Color.BLACK;
    private static final Stroke ARROW_STROKE = new BasicStroke( 1f );

    // Text parameters
    private static final Font TEXT_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final int TEXT_MARGIN = 4; // pixels
    
    // Bubble parameters
    private static final Color BUBBLE_FILL_COLOR = ARROW_FILL_COLOR;
    private static final Color BUBBLE_BORDER_COLOR = ARROW_BORDER_COLOR;
    private static final Stroke BUBBLE_STROKE = ARROW_STROKE;
    private static final int BUBBLE_CORNER_RADIUS = 15;
    
    private static final int SPACING = 0; // pixels between arrow and bubble
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CompositePhetGraphic _bubbleTextGraphic;
    private PhetShapeGraphic _bubbleGraphic;
    private HTMLGraphic _textGraphic;
    private PhetShapeGraphic _arrowGraphic; 
    private PhetGraphic _target;
    private int _arrowPosition;
    private int _arrowLength;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a HelpBubble with no arrow.
     * 
     * @param component the parent Component
     * @param html the help text, in HTML format
     */
    public HelpBubble( Component component, String html ) {
        super( component );
        assert( component != null );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Bubble text
        {
            _textGraphic = new HTMLGraphic( component );
            _textGraphic.setFont( TEXT_FONT );
            _textGraphic.setHTML( html );
            _textGraphic.setColor( TEXT_COLOR );
            _textGraphic.setLocation( TEXT_MARGIN, TEXT_MARGIN );

            _bubbleGraphic = new PhetShapeGraphic( component );
            int width = _textGraphic.getWidth() + ( 2 * TEXT_MARGIN );
            int height = _textGraphic.getHeight() + ( 2 * TEXT_MARGIN );
            _bubbleGraphic.setShape( new RoundRectangle2D.Double( 0,0 , width, height, BUBBLE_CORNER_RADIUS, BUBBLE_CORNER_RADIUS ) );
            _bubbleGraphic.setColor( BUBBLE_FILL_COLOR );
            _bubbleGraphic.setBorderColor( BUBBLE_BORDER_COLOR );
            _bubbleGraphic.setStroke( BUBBLE_STROKE );
            _bubbleGraphic.setLocation( 0, 0 );

            _bubbleTextGraphic = new CompositePhetGraphic( component );
            _bubbleTextGraphic.addGraphic( _bubbleGraphic );
            _bubbleTextGraphic.addGraphic( _textGraphic );
            _bubbleTextGraphic.setLocation( 0, 0 );
        }
        
        // Arrow
        {
            _arrowGraphic = new PhetShapeGraphic( component );
            _arrowGraphic.setColor( ARROW_FILL_COLOR );
            _arrowGraphic.setStroke( ARROW_STROKE );
            _arrowGraphic.setBorderColor( ARROW_BORDER_COLOR );
            _arrowGraphic.setLocation( 0, 0 );
            // the arrow has no shape until method pointAt is called
        }

        addGraphic( _arrowGraphic, ARROW_LAYER );
        addGraphic( _bubbleTextGraphic, BUBBLE_LAYER );
       
        _target = null;
        _arrowLength = 0;
        
        setIgnoreMouse( true );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the colors used for the text, bubble, and arrow.
     * 
     * @param color
     */
    public void setColors( Color textColor, Color bubbleColor, Color arrowColor ) {
        _textGraphic.setColor( textColor );
        _bubbleGraphic.setColor( bubbleColor );
        _arrowGraphic.setColor( arrowColor );
    }
    
    /**
     * Sets the text string.
     * 
     * @param text the help text, in HTML format
     */
    public void setText( String html ) {
        _textGraphic.setHTML( html );
        layout();
    }
    
    /**
     * Sets the font used for the text.
     * 
     * @param font
     */
    public void setFont( Font font ) {
        _textGraphic.setFont( font );
        layout();
    }
    
    //----------------------------------------------------------------------------
    // PhetGraphicListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates location to follow the target graphic.
     * 
     * @param phetGraphic
     */
    public void phetGraphicChanged( PhetGraphic phetGraphic ) {
        if ( _target != null ) {
            trackTarget();
        }
    }

    /**
     * Updates visibility when the target's visibility changes.
     * 
     * @param phetGraphic
     */
    public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
        setVisible( _target.isVisible() );
    }
    
    /*
     * Updates the HelpBubble's location to match the target graphic.
     */
    private void trackTarget() {
        int x = 0;
        int y = 0;
        switch ( _arrowPosition ) {
        case TOP_LEFT:
        case TOP_CENTER:
        case TOP_RIGHT:
            x = _target.getBounds().x + ( _target.getWidth() / 2 );
            y = _target.getBounds().y + _target.getHeight();
            break;
        case BOTTOM_LEFT:
        case BOTTOM_CENTER:
        case BOTTOM_RIGHT:
            x = _target.getBounds().x + ( _target.getWidth() / 2 );
            y = _target.getBounds().y;
            break;
        case LEFT_TOP:
        case LEFT_CENTER:
        case LEFT_BOTTOM:
            x = _target.getBounds().x + _target.getWidth();
            y = _target.getBounds().y + ( _target.getHeight() / 2 );
            break;
        case RIGHT_TOP:
        case RIGHT_CENTER:
        case RIGHT_BOTTOM:
            x = _target.getBounds().x;
            y = _target.getBounds().y + ( _target.getHeight() / 2 );
            break;
        default:
            throw new IllegalArgumentException( "illegal arrow position: " + _arrowPosition );
        }
        setLocation( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Method for pointing at a target graphic or point
    //----------------------------------------------------------------------------
    
    /**
     * Points at a target graphic.
     * 
     * @param target
     * @param arrowPosition
     * @param arrowLength
     * @throws IllegalArgumentException
     */
    public void pointAt( PhetGraphic target, int arrowPosition, int arrowLength ) {
        if ( !isValidArrowPostion( arrowPosition ) ) {
            throw new IllegalArgumentException( "illegal arrowPosition: " + arrowPosition );
        }
        if ( arrowLength <= 0 ) {
            throw new IllegalArgumentException( "arrowLength must be > 0: " + arrowLength );
        }
        
        _arrowPosition = arrowPosition;
        _arrowLength = arrowLength;     
        layout();
        
        // Wire up the target so we follow it.
        if ( _target != null ) {
            _target.removePhetGraphicListener( this );
        }
        _target = target;
        if ( _target != null ) {
            _target.addPhetGraphicListener( this );
            setRegistrationPoint( 0, 0 );
            trackTarget();
        }
    }
    
    /**
     * Points at a location (a Point).
     * 
     * @param point
     * @param arrowPosition
     * @param arrowLength
     * @throws IllegalArgumentException
     */
    public void pointAt( Point point, int arrowPosition, int arrowLength ) {
        pointAt( point.x, point.y, arrowPosition, arrowLength );
    }
    
    /**
     * Points at a location (xy coordinates).
     * 
     * @param x
     * @param y
     * @param arrowPosition
     * @param arrowLength
     * @throws IllegalArgumentException
     */
    public void pointAt( int x, int y, int arrowPosition, int arrowLength ) {
        if ( !isValidArrowPostion( arrowPosition ) ) {
            throw new IllegalArgumentException( "illegal arrowPosition: " + arrowPosition );
        }
        if ( arrowLength <= 0 ) {
            throw new IllegalArgumentException( "arrowLength must be > 0: " + arrowLength );
        }
        _arrowPosition = arrowPosition;
        _arrowLength = arrowLength;     
        layout();
        setRegistrationPoint( 0, 0 );
        setLocation( x, y );
    }
    
    /*
     * Lays out the arrow and the bubble text.
     * The tip of the arrow is always at location (0,0).
     */
    private void layout() {
        
        // Resize the bubble to fit the text.
        int width = _textGraphic.getWidth() + ( 2 * TEXT_MARGIN );
        int height = _textGraphic.getHeight() + ( 2 * TEXT_MARGIN );
        _bubbleGraphic.setShape( new RoundRectangle2D.Double( 0, 0, width, height, BUBBLE_CORNER_RADIUS, BUBBLE_CORNER_RADIUS ) );
        
        // Do we have an arrow?
        if ( _arrowLength == 0 ) {
            _arrowGraphic.setShape( null );
            _bubbleGraphic.setLocation( 0, 0 );
            return;
        }
        
        // Create the arrow with tip at (0,0)
        Point tipPoint = new Point( 0, 0 );
        Point tailPoint = new Point();
        switch ( _arrowPosition ) {
        case TOP_LEFT:
        case TOP_CENTER:
        case TOP_RIGHT:
            tailPoint.setLocation( 0, _arrowLength );
            break;
        case BOTTOM_LEFT:
        case BOTTOM_CENTER:
        case BOTTOM_RIGHT:
            tailPoint.setLocation( 0, -_arrowLength );
            break;
        case LEFT_TOP:
        case LEFT_CENTER:
        case LEFT_BOTTOM:
            tailPoint.setLocation( _arrowLength, 0 );
            break;
        case RIGHT_TOP:
        case RIGHT_CENTER:
        case RIGHT_BOTTOM:
            tailPoint.setLocation( -_arrowLength, 0 );
            break;
        default:
            throw new IllegalArgumentException( "illegal arrow position: " + _arrowPosition );
        }
        Arrow arrow = new Arrow( tailPoint, tipPoint, ARROW_HEAD_WIDTH, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        _arrowGraphic.setShape( arrow.getShape() );
        
        // Set the bubble's location relative to the arrow
        int x = 0;
        int y = 0;
        switch ( _arrowPosition ) {
        case TOP_LEFT:
            x = -_arrowGraphic.getWidth();
            y = _arrowGraphic.getHeight() + SPACING;
            break;
        case TOP_CENTER:
            x = -_bubbleTextGraphic.getWidth() / 2;
            y = _arrowGraphic.getHeight() + SPACING;
            break;
        case TOP_RIGHT:
            x = -( _bubbleTextGraphic.getWidth() - _arrowGraphic.getWidth() );
            y = _arrowGraphic.getHeight() + SPACING;
            break;
        case BOTTOM_LEFT:
            x = -_arrowGraphic.getWidth();
            y = -( _bubbleTextGraphic.getHeight() + _arrowGraphic.getHeight() + SPACING );
            break;
        case BOTTOM_CENTER:
            x = -_bubbleTextGraphic.getWidth() / 2;
            y = -( _bubbleTextGraphic.getHeight() + _arrowGraphic.getHeight() + SPACING );
            break;
        case BOTTOM_RIGHT:
            x = -( _bubbleTextGraphic.getWidth() - _arrowGraphic.getWidth() );
            y = -( _bubbleTextGraphic.getHeight() + _arrowGraphic.getHeight() + SPACING );
            break;
        case LEFT_TOP:
            x = _arrowGraphic.getWidth() + SPACING;
            y = -_arrowGraphic.getHeight();
            break;
        case LEFT_CENTER:
            x = _arrowGraphic.getWidth() + SPACING;
            y = -_bubbleTextGraphic.getHeight() / 2;
            break;
        case LEFT_BOTTOM:
            x = _arrowGraphic.getWidth() + SPACING;
            y = -( _bubbleTextGraphic.getHeight() - _arrowGraphic.getHeight() );
            break;
        case RIGHT_TOP:
            x = -( _bubbleTextGraphic.getWidth() + _arrowGraphic.getWidth() + SPACING );
            y = -_arrowGraphic.getHeight();
            break;
        case RIGHT_CENTER:
            x = -( _bubbleTextGraphic.getWidth() + _arrowGraphic.getWidth() + SPACING );
            y = -_bubbleTextGraphic.getHeight() / 2;
            break;
        case RIGHT_BOTTOM:
            x = -( _bubbleTextGraphic.getWidth() + _arrowGraphic.getWidth() + SPACING );
            y = -( _bubbleTextGraphic.getHeight() - _arrowGraphic.getHeight() );
            break;
        default:
            throw new IllegalArgumentException( "illegal arrow position: " + _arrowPosition );
        }
        _bubbleTextGraphic.setLocation( x, y );
    }
    
    /**
     * Determines if a  value for "arrow position" is valid.
     * 
     * @param arrowPosition
     * @return  true or false
     */
    public boolean isValidArrowPostion( int arrowPosition ) {
        return ( 
                arrowPosition == TOP_LEFT ||
                arrowPosition == TOP_CENTER || 
                arrowPosition == TOP_RIGHT ||
                arrowPosition == BOTTOM_LEFT ||
                arrowPosition == BOTTOM_CENTER || 
                arrowPosition == BOTTOM_RIGHT ||
                arrowPosition == LEFT_TOP ||
                arrowPosition == LEFT_CENTER || 
                arrowPosition == LEFT_BOTTOM ||
                arrowPosition == RIGHT_TOP ||
                arrowPosition == RIGHT_CENTER ||
                arrowPosition == RIGHT_BOTTOM );
    }
}
