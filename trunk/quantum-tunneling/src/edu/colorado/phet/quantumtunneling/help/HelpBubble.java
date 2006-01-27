/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.help;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

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
public class HelpBubble extends AbstractHelpItem {
    
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
    private static final Font TEXT_FONT = new Font( "Lucida Sans", Font.PLAIN, 14 );
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
    
    private PPath _bubbleNode;
    private PText _textNode;
    private PPath _arrowNode; 
    
    private int _arrowPosition;
    private double _arrowLength;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HelpBubble( JComponent helpPanel, String text ) {
        this( helpPanel, text, TOP_LEFT /* don't care */, 0 );
    }
    
    /**
     * Creates a HelpBubble with no arrow.
     * 
     * @param component the parent Component
     * @param text
     */
    public HelpBubble( JComponent helpPanel, String text, int arrowPosition, double arrowLength ) {
        super( helpPanel );
        
        // Validate arguments
        if ( !isValidArrowPostion( arrowPosition ) ) {
            throw new IllegalArgumentException( "invalid arrowPosition: " + arrowPosition );
        }
        if ( arrowLength < 0 ) {
            throw new IllegalArgumentException( "arrowLength < 0: " + arrowLength );
        }
        
        // Not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        // Arrow
        {
            _arrowPosition = arrowPosition;
            _arrowLength = arrowLength;
            
            _arrowNode = new PPath();
            _arrowNode.setPaint( ARROW_FILL_COLOR );
            _arrowNode.setStroke( ARROW_STROKE );
            _arrowNode.setStrokePaint( ARROW_BORDER_COLOR );
        }
        
        // Bubble
        {
            _bubbleNode = new PPath();
            _bubbleNode.setPaint( BUBBLE_FILL_COLOR );
            _bubbleNode.setStroke( BUBBLE_STROKE );
            _bubbleNode.setStrokePaint( BUBBLE_BORDER_COLOR );
        }

        // Text
        {
            _textNode = new PText( text );
            _textNode.setFont( TEXT_FONT );
            _textNode.setTextPaint( TEXT_COLOR );
        }

        addChild( _arrowNode );
        addChild( _bubbleNode );
        addChild( _textNode ); // add text last so that it's on top

        updateDisplay();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setText( String text ) {
        _textNode.setText( text );
        updateDisplay();
    }
    
    public String getText() {
        return _textNode.getText();
    }
    
    public void setFont( Font font ) {
        _textNode.setFont( font );
        updateDisplay();
    }
    
    public void setTextPaint( Paint textPaint ) {
        _textNode.setTextPaint( textPaint );
    }
    
    public void setBubblePaint( Paint fillPaint, Paint strokePaint ) {
        _bubbleNode.setPaint( fillPaint );
        _bubbleNode.setStrokePaint( strokePaint );
    }
    
    public void setBubbleStroke( Stroke stroke ) {
        _bubbleNode.setStroke( stroke );
    }
    
    public void setArrowPaint( Paint fillPaint, Paint strokePaint ) {
        _arrowNode.setPaint( fillPaint );
        _arrowNode.setStrokePaint( strokePaint );
    }
    
    public void setArrowStroke( Stroke stroke ) {
        _arrowNode.setStroke( stroke );
    }
    
    public boolean arrowVertical() {
        return arrowOnTop() || arrowOnBottom();
    }
    
    public boolean arrowHorizontal() {
        return !arrowVertical();
    }
    
    public boolean arrowOnTop() {
        return ( _arrowPosition == TOP_LEFT || _arrowPosition == TOP_CENTER || _arrowPosition == TOP_RIGHT  );
    }
    
    public boolean arrowOnBottom() {
        return ( _arrowPosition == BOTTOM_LEFT || _arrowPosition == BOTTOM_CENTER || _arrowPosition == BOTTOM_RIGHT );
    }
    
    public boolean arrowOnLeft() {
        return ( _arrowPosition == LEFT_TOP || _arrowPosition == LEFT_CENTER || _arrowPosition == LEFT_BOTTOM );
    }
    
    public boolean arrowOnRight() {
        return ( _arrowPosition == RIGHT_TOP || _arrowPosition == RIGHT_CENTER || _arrowPosition == RIGHT_BOTTOM );
    }
    
    //----------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------
    
    /**
     * Sizes the bubble to fit the text.
     * Creates the arrow with its tip at (0,0).
     * Positions the bubble and text relative to the arrow position.
     */
    public void updateDisplay() {
        
        // Resize the bubble to fit the text.
        {
            double width = _textNode.getWidth() + ( 2 * TEXT_MARGIN );
            double height = _textNode.getHeight() + ( 2 * TEXT_MARGIN );
            Shape shape = new RoundRectangle2D.Double( 0, 0, width, height, BUBBLE_CORNER_RADIUS, BUBBLE_CORNER_RADIUS );
            _bubbleNode.setPathTo( shape );
        }
        
        // Do we have an arrow?
        if ( _arrowLength == 0 ) {
            _bubbleNode.setOffset( 0, 0 );
            _textNode.setOffset( TEXT_MARGIN, TEXT_MARGIN );
            _arrowNode.setVisible( false );
            return;
        }
        else {
            _arrowNode.setVisible( true );
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
        _arrowNode.setPathTo( arrow.getShape() );
        
        // Position the bubble & text relative to the arrow
        double x = 0;
        double y = 0;
        switch ( _arrowPosition ) {
        case TOP_LEFT:
            x = -_arrowNode.getWidth();
            y = _arrowNode.getHeight() + SPACING;
            break;
        case TOP_CENTER:
            x = -_bubbleNode.getWidth() / 2;
            y = _arrowNode.getHeight() + SPACING;
            break;
        case TOP_RIGHT:
            x = -( _bubbleNode.getWidth() - _arrowNode.getWidth() );
            y = _arrowNode.getHeight() + SPACING;
            break;
        case BOTTOM_LEFT:
            x = -_arrowNode.getWidth();
            y = -( _bubbleNode.getHeight() + _arrowNode.getHeight() + SPACING );
            break;
        case BOTTOM_CENTER:
            x = -_bubbleNode.getWidth() / 2;
            y = -( _bubbleNode.getHeight() + _arrowNode.getHeight() + SPACING );
            break;
        case BOTTOM_RIGHT:
            x = -( _bubbleNode.getWidth() - _arrowNode.getWidth() );
            y = -( _bubbleNode.getHeight() + _arrowNode.getHeight() + SPACING );
            break;
        case LEFT_TOP:
            x = _arrowNode.getWidth() + SPACING;
            y = -_arrowNode.getHeight();
            break;
        case LEFT_CENTER:
            x = _arrowNode.getWidth() + SPACING;
            y = -_bubbleNode.getHeight() / 2;
            break;
        case LEFT_BOTTOM:
            x = _arrowNode.getWidth() + SPACING;
            y = -( _bubbleNode.getHeight() - _arrowNode.getHeight() );
            break;
        case RIGHT_TOP:
            x = -( _bubbleNode.getWidth() + _arrowNode.getWidth() + SPACING );
            y = -_arrowNode.getHeight();
            break;
        case RIGHT_CENTER:
            x = -( _bubbleNode.getWidth() + _arrowNode.getWidth() + SPACING );
            y = -_bubbleNode.getHeight() / 2;
            break;
        case RIGHT_BOTTOM:
            x = -( _bubbleNode.getWidth() + _arrowNode.getWidth() + SPACING );
            y = -( _bubbleNode.getHeight() - _arrowNode.getHeight() );
            break;
        default:
            throw new IllegalArgumentException( "illegal arrow position: " + _arrowPosition );
        }
        _bubbleNode.setOffset( x, y );
        _textNode.setOffset( x + TEXT_MARGIN, y + TEXT_MARGIN );
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
    
    public Point2D mapLocation( PNode targetNode, PCanvas targetCanvas ) {
        Point2D p = super.mapLocation( targetNode, targetCanvas );
        if ( arrowVertical() ) {
            p.setLocation( p.getX() + targetNode.getWidth() / 2, p.getY() );
        }
        else {
            p.setLocation( p.getX(), p.getY() + targetNode.getHeight() / 2 );
        }
        return p;
    }
    
    public Point2D mapLocation( JComponent targetComponent ) {
        Point2D p = super.mapLocation( targetComponent );
        if ( arrowVertical() ) {
            p.setLocation( p.getX() + targetComponent.getWidth() / 2, p.getY() );
        }
        else {
            p.setLocation( p.getX(), p.getY() + targetComponent.getHeight() / 2 );
        }
        return p;
    }
}