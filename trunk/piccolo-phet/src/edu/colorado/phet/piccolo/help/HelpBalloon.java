/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.help;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.piccolo.HTMLGraphic;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * HelpBalloon is a help item that consists of text (with optional shadow)
 * inside a balloon, with an option arrow.   This class handles the "look"
 * of the help item -- issues related to position and visibility are
 * handles in the base class.
 * <p>
 * If no arrow is specified, calls to pointAt will position the 
 * upper-left corner of the balloon at the specified location.
 * <p>
 * Many visual attributes can be customized using set methods.
 *
 * @author Chris Malley
 * @version $Revision$
 */
public class HelpBalloon extends AbstractHelpItem {
    
    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    // Arrow positions, relative to the text balloon.
    public static final Object TOP_LEFT = new String( "top left" );
    public static final Object TOP_CENTER = new String( "top center" );
    public static final Object TOP_RIGHT = new String( "top right" );
    public static final Object BOTTOM_LEFT = new String( "bottom left" );
    public static final Object BOTTOM_CENTER = new String( "bottom center" );
    public static final Object BOTTOM_RIGHT = new String( "bottom right" );
    public static final Object LEFT_TOP = new String( "left top" );
    public static final Object LEFT_CENTER = new String( "left center" );
    public static final Object LEFT_BOTTOM = new String( "left bottom" );
    public static final Object RIGHT_TOP = new String( "right top" );
    public static final Object RIGHT_CENTER = new String( "right center" );
    public static final Object RIGHT_BOTTOM = new String( "right bottom" );
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------

    // Text parameters
    private static final Font DEFAULT_TEXT_FONT = new Font( "Lucida Sans", Font.PLAIN, 14 );
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final double DEFAULT_TEXT_MARGIN = 4; // pixels
    private static final Dimension DEFAULT_SHADOW_TEXT_OFFSET = new Dimension( 1, 1 ); // pixels
    private static final Color DEFAULT_SHADOW_TEXT_COLOR = Color.RED;
    
    // Arrow parameters
    private static final int DEFAULT_ARROW_HEAD_WIDTH = 10; // pixels
    private static final int DEFAULT_ARROW_HEAD_HEIGHT = 10; // pixels
    private static final int DEFAULT_ARROW_TAIL_WIDTH = 3; // pixels
    private static final Paint DEFAULT_ARROW_FILL_PAINT = new Color( 250, 250, 170, 175 );
    private static final Paint DEFAULT_ARROW_BORDER_PAINT = Color.BLACK;
    private static final Stroke DEFAULT_ARROW_STROKE = new BasicStroke( 1f );
    
    // Balloon parameters
    private static final Paint DEFAULT_BALLOON_FILL_PAINT = DEFAULT_ARROW_FILL_PAINT;
    private static final Paint DEFAULT_BALLOON_BORDER_PAINT = DEFAULT_ARROW_BORDER_PAINT;
    private static final Stroke DEFAULT_BALLOON_STROKE = DEFAULT_ARROW_STROKE;
    private static final int DEFAULT_BALLOON_CORNER_RADIUS = 15; // pixels
    private static final double DEFAULT_BALLOON_ARROW_SPACING = 0; // pixels between arrow and balloon
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HTMLGraphic _textNode;
    private HTMLGraphic _shadowTextNode;
    private PPath _balloonNode;
    private PPath _arrowNode; 
    
    private Object _arrowPosition;
    private double _arrowLength;
    private Dimension _arrowHeadSize;
    private double _arrowTailWidth;
    private double _textMargin;
    private Dimension _shadowTextOffset;
    private double _balloonCornerRadius;
    private double _balloonArrowSpacing;
    
    private boolean _updateEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HelpBalloon( JComponent helpPanel, String text ) {
        this( helpPanel, text, TOP_LEFT /* don't care */, 0 );
    }
    
    /**
     * Creates a HelpBalloon with no arrow.
     * 
     * @param helpPanel
     * @param text
     * @param arrowPosition
     * @param arrowLength
     */
    public HelpBalloon( JComponent helpPanel, String text, Object arrowPosition, double arrowLength ) {
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
            _arrowHeadSize = new Dimension( DEFAULT_ARROW_HEAD_WIDTH, DEFAULT_ARROW_HEAD_HEIGHT );
            _arrowTailWidth = DEFAULT_ARROW_TAIL_WIDTH;
            
            _arrowNode = new PPath();
            _arrowNode.setPaint( DEFAULT_ARROW_FILL_PAINT );
            _arrowNode.setStroke( DEFAULT_ARROW_STROKE );
            _arrowNode.setStrokePaint( DEFAULT_ARROW_BORDER_PAINT );
        }
        
        // Balloon
        {
            _balloonCornerRadius = DEFAULT_BALLOON_CORNER_RADIUS;
            _balloonArrowSpacing = DEFAULT_BALLOON_ARROW_SPACING;
            
            _balloonNode = new PPath();
            _balloonNode.setPaint( DEFAULT_BALLOON_FILL_PAINT );
            _balloonNode.setStroke( DEFAULT_BALLOON_STROKE );
            _balloonNode.setStrokePaint( DEFAULT_BALLOON_BORDER_PAINT );
        }

        // Text
        {
            _textMargin = DEFAULT_TEXT_MARGIN;

            _textNode = new HTMLGraphic( text );
            _textNode.setFont( DEFAULT_TEXT_FONT );
            _textNode.setColor( DEFAULT_TEXT_COLOR );
            
            _shadowTextOffset = new Dimension( DEFAULT_SHADOW_TEXT_OFFSET );
            
            _shadowTextNode = new HTMLGraphic( text );
            _shadowTextNode.setFont( DEFAULT_TEXT_FONT );
            _shadowTextNode.setColor( DEFAULT_SHADOW_TEXT_COLOR );
            _shadowTextNode.setVisible( false ); // default is no shadow text
        }

        addChild( _arrowNode );
        addChild( _balloonNode );
        addChild( _shadowTextNode );
        addChild( _textNode ); // add text last so that it's on top

        _updateEnabled = true;
        updateDisplay();
    }
    
    //----------------------------------------------------------------------------
    // Object overrides
    //----------------------------------------------------------------------------
    
    public String toString() {
        return getClass().getName() + " [text=" + _textNode.getHtml() + "]";
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables or disables updates.
     * If you're going to change a lot of attributes, set update to false first.
     * Then set it back to true after changing the attributes.
     * 
     * @param enabled true or false
     */
    public void setUpdateEnabled( boolean enabled ) {
        if ( enabled != _updateEnabled ) {
            _updateEnabled = enabled;
            if ( enabled ) {
                updateDisplay();
            }
        }
    }
    
    // Text attributes -----------------------------------------------------------
    
    public void setText( String html ) {
        _textNode.setHtml( html );
        _shadowTextNode.setHtml( html );
        updateDisplay();
    }

    public void setFont( Font font ) {
        _textNode.setFont( font );
        _shadowTextNode.setFont( font );
        updateDisplay();
    }
    
    public void setTextColor( Color color ) {
        _textNode.setColor( color );
    }
    
    /**
     * Margin between text and edges of balloon.
     * 
     * @param margin in pixels
     */
    public void setTextMargin( double margin ) {
        _textMargin = margin;
        updateDisplay();
    }
    
    // Shadow text attributes ------------------------------------------------------
        
    public void setShadowTextEnabled( boolean enabled ) {
        _shadowTextNode.setVisible( enabled );
    }
    
    public void setShadowTextColor( Color color ) {
        _shadowTextNode.setColor( color );
    }
    
    /**
     * Offset between primary and shadow text, in pixels.
     * @param x
     * @param y
     */
    public void setShadowTextOffset( double x, double y ) {
        _shadowTextOffset.setSize( x, y );
        updateDisplay();
    }

    // Balloon attributes ------------------------------------------------------
    
    public void setBalloonFillPaint( Paint paint ) {
        _balloonNode.setPaint( paint );
    }
    
    public void setBalloonStrokePaint( Paint paint ) {
        _balloonNode.setStrokePaint( paint );
    }
    
    public void setBalloonStroke( Stroke stroke ) {
        _balloonNode.setStroke( stroke );
    }
    
    /**
     * The balloon is a rounded rectangle.
     * This sets the corner radius on the rounded rectangle.
     * 
     * @param radius
     */
    public void setBalloonCornerRadius( double radius ) {
        if ( radius < 0 ) {
            throw new IllegalArgumentException( "radius < 0: " + radius );
        }
        _balloonCornerRadius = radius;
        updateDisplay();
    }
    
    // Arrow attributes ------------------------------------------------------
    
    public void setArrowFillPaint( Paint paint ) {
        _arrowNode.setPaint( paint );
    }
    
    public void setArrowStrokePaint( Paint paint ) {
        _arrowNode.setStrokePaint( paint );
    }
     
    public void setArrowStroke( Stroke stroke ) {
        _arrowNode.setStroke( stroke );
    }
    
    public void setArrowHeadSize( int width, int height ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "width <= 0: " + width );
        }
        if ( height <= 0 ) {
            throw new IllegalArgumentException( "height <= 0: " + height );
        }
        _arrowHeadSize = new Dimension( width, height );
        updateDisplay();
    }
    
    public void setArrowTailWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "width <= 0: " + width );
        }
        _arrowTailWidth = width;
        updateDisplay();
    }
    
    /**
     * Sets the spacing between the balloon and the arrow tail.
     * 
     * @param spacing
     */
    public void setArrowBalloonSpacing( double spacing ) {
        _balloonArrowSpacing = spacing;
        updateDisplay();
    }
    
    //----------------------------------------------------------------------------
    // Convenience function for determining arrow position and orientation.
    //----------------------------------------------------------------------------
    
    protected boolean isArrowVertical() {
        return isArrowOnTop() || isArrowOnBottom();
    }
    
    protected boolean isArrowHorizontal() {
        return !isArrowVertical();
    }
    
    protected boolean isArrowOnTop() {
        return ( _arrowPosition == TOP_LEFT || _arrowPosition == TOP_CENTER || _arrowPosition == TOP_RIGHT  );
    }
    
    protected boolean isArrowOnBottom() {
        return ( _arrowPosition == BOTTOM_LEFT || _arrowPosition == BOTTOM_CENTER || _arrowPosition == BOTTOM_RIGHT );
    }
    
    protected boolean isArrowOnLeft() {
        return ( _arrowPosition == LEFT_TOP || _arrowPosition == LEFT_CENTER || _arrowPosition == LEFT_BOTTOM );
    }
    
    protected boolean isArrowOnRight() {
        return ( _arrowPosition == RIGHT_TOP || _arrowPosition == RIGHT_CENTER || _arrowPosition == RIGHT_BOTTOM );
    }
      
    /**
     * Determines if a  value for "arrow position" is valid.
     * 
     * @param arrowPosition
     * @return  true or false
     */
    public boolean isValidArrowPostion( Object arrowPosition ) {
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
    
    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display when we become visible.
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( getVisible() == true ) {
            updateDisplay();
        }
    }
    
    //----------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------
    
    /**
     * Sizes the balloon to fit the text.
     * Creates the arrow with its tip at (0,0).
     * Positions the balloon and text relative to the arrow position.
     */
    public void updateDisplay() {
        
        if ( !_updateEnabled ) {
            return;
        }
        
        // Resize the balloon to fit the text.
        {
            double width = _textNode.getWidth() + ( 2 * _textMargin );
            double height = _textNode.getHeight() + ( 2 * _textMargin );
            Shape shape = new RoundRectangle2D.Double( 0, 0, width, height, _balloonCornerRadius, _balloonCornerRadius );
            _balloonNode.setPathTo( shape );
        }
        
        // Do we have an arrow?
        if ( _arrowLength == 0 ) {
            _balloonNode.setOffset( 0, 0 );
            _textNode.setOffset( _textMargin, _textMargin );
            _shadowTextNode.setOffset( _textMargin + _shadowTextOffset.getWidth(), _textMargin + _shadowTextOffset.getHeight() );
            _arrowNode.setVisible( false );
            return;
        }
        else {
            _arrowNode.setVisible( true );
        }
        
        // Create the arrow with tip at (0,0)
        Point tipPoint = new Point( 0, 0 );
        Point tailPoint = new Point();
        if ( isArrowOnTop() ) {
            tailPoint.setLocation( 0, _arrowLength );
        }
        else if ( isArrowOnBottom() ) {
            tailPoint.setLocation( 0, -_arrowLength );
        }
        else if ( isArrowOnLeft() ) {
            tailPoint.setLocation( _arrowLength, 0 );
        }
        else if ( isArrowOnRight() ) {
            tailPoint.setLocation( -_arrowLength, 0 );
        }
        else {
            throw new IllegalArgumentException( "illegal arrow position: " + _arrowPosition );
        }
        Arrow arrow = new Arrow( tailPoint, tipPoint, _arrowHeadSize.getHeight(), _arrowHeadSize.getWidth(), _arrowTailWidth );
        _arrowNode.setPathTo( arrow.getShape() );
        
        // Position the balloon & text relative to the arrow
        double x = 0;
        double y = 0;
        if ( _arrowPosition == TOP_LEFT ) {
            x = -_arrowNode.getWidth() / 2;
            y = _arrowNode.getHeight() + _balloonArrowSpacing;
        }
        else if ( _arrowPosition == TOP_CENTER ) {
            x = -_balloonNode.getWidth() / 2;
            y = _arrowNode.getHeight() + _balloonArrowSpacing;
        }
        else if ( _arrowPosition ==  TOP_RIGHT ) {
            x = -( _balloonNode.getWidth() - _arrowNode.getWidth() / 2 );
            y = _arrowNode.getHeight() + _balloonArrowSpacing;
        }
        else if ( _arrowPosition == BOTTOM_LEFT ) {
            x = -_arrowNode.getWidth() / 2;
            y = -( _balloonNode.getHeight() + _arrowNode.getHeight() + _balloonArrowSpacing );
        }
        else if ( _arrowPosition == BOTTOM_CENTER ) {
            x = -_balloonNode.getWidth() / 2;
            y = -( _balloonNode.getHeight() + _arrowNode.getHeight() + _balloonArrowSpacing );
        }    
        else if ( _arrowPosition == BOTTOM_RIGHT ) {
            x = -( _balloonNode.getWidth() - _arrowNode.getWidth() / 2 );
            y = -( _balloonNode.getHeight() + _arrowNode.getHeight() + _balloonArrowSpacing );
        }
        else if ( _arrowPosition == LEFT_TOP ) {
            x = _arrowNode.getWidth() + _balloonArrowSpacing;
            y =  -( _arrowNode.getHeight() / 2 );
        }
        else if ( _arrowPosition == LEFT_CENTER ) {
            x = _arrowNode.getWidth() + _balloonArrowSpacing;
            y = -_balloonNode.getHeight() / 2;
        }
        else if ( _arrowPosition == LEFT_BOTTOM ) {
            x = _arrowNode.getWidth() + _balloonArrowSpacing;
            y = -( _balloonNode.getHeight() - _arrowNode.getHeight() / 2 );
        }
        else if ( _arrowPosition == RIGHT_TOP ) {
            x = -( _balloonNode.getWidth() + _arrowNode.getWidth() + _balloonArrowSpacing );
            y = -_arrowNode.getHeight() / 2;
        }
        else if ( _arrowPosition == RIGHT_CENTER ) {
            x = -( _balloonNode.getWidth() + _arrowNode.getWidth() + _balloonArrowSpacing );
            y = -_balloonNode.getHeight() / 2;
        }
        else if ( _arrowPosition == RIGHT_BOTTOM ) {
            x = -( _balloonNode.getWidth() + _arrowNode.getWidth() + _balloonArrowSpacing );
            y = -( _balloonNode.getHeight() - _arrowNode.getHeight() / 2 );
        }
        else {
            throw new IllegalArgumentException( "illegal arrow position: " + _arrowPosition );
        }
        
        _balloonNode.setOffset( x, y );
        _textNode.setOffset( x + _textMargin, y + _textMargin );
        _shadowTextNode.setOffset( x + _textMargin + _shadowTextOffset.getWidth(), y + _textMargin + _shadowTextOffset.getHeight() );
    }
    
    //----------------------------------------------------------------------------
    // AbstractHelpItem overrides
    //----------------------------------------------------------------------------
    
    /**
     * Maps to a point that allows us to point to the center of 
     * one of the node's edges, instead of its upper-left corner.
     * Which edge is dependent on the arrow position.
     * 
     * @param node
     * @param canvas
     */
    protected Point2D mapLocation( PNode node, PCanvas canvas ) {
        
        // Map the node's location (upper-left corner) to help pane coordinates...
        Point2D p = super.mapLocation( node, canvas );
        
        // Determine the node's dimensions, in help pane coordinates...
        Rectangle2D globalBounds = node.getGlobalBounds();
        PCamera camera = canvas.getCamera();
        PAffineTransform transform = camera.getViewTransformReference();
        Rectangle2D bounds = transform.transform( globalBounds, null );
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        
        // Translate the help pane coordinates, using the node's dimensions and arrow position...
        if ( isArrowOnTop() ) {
            p.setLocation( p.getX() + ( width / 2 ), p.getY() + height );
        }
        else if ( isArrowOnBottom() ) {
            p.setLocation( p.getX() + ( width / 2 ), p.getY() );
        }
        else if ( isArrowOnRight() ) {
            p.setLocation( p.getX(), p.getY() + ( height / 2 ) );
        }
        else {
            p.setLocation( p.getX() + width, p.getY() + ( height / 2 ) );
        }
        
        return p;
    }
    
    /**
     * Maps to a point that allows us to point to the center of 
     * one of the component's edges, instead of its upper-left corner.
     * Which edge is dependent on the arrow position.
     * 
     * @param component
     */
    protected Point2D mapLocation( JComponent component ) {
        
        // Map the component's location (upper-left corner) to help pane coordinates...
        Point2D p = super.mapLocation( component );
        
        // Determine the component's dimensions...
        double width = component.getWidth();
        double height = component.getHeight();
        
        // Translate the help pane coordinates, using the component's dimensions and arrow position...
        if ( isArrowOnTop() ) {
            p.setLocation( p.getX() + ( width / 2 ), p.getY() + height );
        }
        else if ( isArrowOnBottom() ) {
            p.setLocation( p.getX() + ( width / 2 ), p.getY() );
        }
        else if ( isArrowOnRight() ) {
            p.setLocation( p.getX(), p.getY() + ( height / 2 ) );
        }
        else {
            p.setLocation( p.getX() + width, p.getY() + ( height / 2 ) );
        }

        return p;
    }
}