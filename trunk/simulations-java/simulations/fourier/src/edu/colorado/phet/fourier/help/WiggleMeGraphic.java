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

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;


/**
 * WiggleMeGraphic is the graphic that displays a "Wiggle Me".
 * A wiggle me is provides help in situations where the user may not
 * know how to get started.  There is typically a piece of text,
 * along with one or more arrows that point to objects in the 
 * apparatus panel.  The wiggle me directs the user to do something,
 * and it disappears as soon as the user does it.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WiggleMeGraphic extends GraphicLayerSet implements ClockListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Where the arrow originates, relative to the text.
    public static final int TOP_LEFT = 0;
    public static final int TOP_CENTER = 1;
    public static final int TOP_RIGHT = 2;
    public static final int LEFT_CENTER = 3;
    public static final int RIGHT_CENTER = 4;
    public static final int BOTTOM_LEFT = 5;
    public static final int BOTTOM_CENTER = 6;
    public static final int BOTTOM_RIGHT = 7;
    
    // Wiggle direction
    public static final int CLOCKWISE = 0;
    public static final int COUNTER_CLOCKWISE = 1;
    
    // Arrow "look" - see edu.colorado.phet.common.view.graphics.shapes.Arrow
    private static final double ARROW_HEAD_HEIGHT = 15;
    private static final double ARROW_HEAD_WIDTH = 10;
    private static final double ARROW_TAIL_WIDTH = 3;
    private static final double ARROW_FRACTIONAL_HEAD_HEIGHT = 100;
    
    // Fonts and Colors
    private static final Font DEFAULT_TEXT_FONT = new PhetFont( Font.PLAIN, 18 );
    private static final Color DEFAULT_TEXT_COLOR = Color.YELLOW;
    private static final Color DEFAULT_ARROW_FILL_COLOR = Color.YELLOW;
    private static final Color DEFAULT_ARROW_BORDER_COLOR = Color.BLACK;
    private static final Stroke DEFAULT_ARROW_STROKE = new BasicStroke( 1f );
    
    // Space between the text and the arrows.
    private static final int TEXT_MARGIN = 10;
    
    // Default duration of 1 complete wiggle cycle, in clock ticks.
    private static final int DEFAULT_CYCLE_DURATION = 15;
    
    // Default wiggle range.
    private static final Dimension DEFAULT_RANGE = new Dimension( 20, 20 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Clock for animation
    private IClock _clock;
    // The text
    private HTMLGraphic _textGraphic;
    // The width and height that the wiggle will travel.
    private Dimension _range;
    // The number of animation cycles completed, usually fractional.
    private double _cycles;
    // The number of clock ticks in one complete animation cycle.
    private int _cycleDuration;
    // Enabled?
    private boolean _enabled;
    // Starting location
    private Point _startLocation;
    // Direction of the wiggle
    private int _direction;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public WiggleMeGraphic( Component component, IClock clock ) {
        super( component );
        
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( hints );
        
        assert( clock != null );
        _clock = clock;
        
        _textGraphic = new HTMLGraphic( component, DEFAULT_TEXT_FONT, "", DEFAULT_TEXT_COLOR );
        
        _range = new Dimension( DEFAULT_RANGE );
        _cycles = 0;
        _cycleDuration = DEFAULT_CYCLE_DURATION;
        _enabled = false;
        _startLocation = new Point( getX(), getY() );
        _direction = CLOCKWISE;
        
        addGraphic( _textGraphic );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            setVisible( _enabled );
            if ( _enabled ) {
                _clock.addClockListener( this );
            }
            else {
                _clock.addClockListener( this );
            }
        }
    }
    
    /**
     * Sets the text, using a default font and color.
     * 
     * @param text the text
     */
    public void setText( String text ) {
        setText( text, DEFAULT_TEXT_COLOR, DEFAULT_TEXT_FONT );
    }
    
    /**
     * Sets the text, using a specified font and the default color
     * 
     * @param text
     * @param font
     * @param color
     */
    public void setText( String text, Color color ) {
        setText( text, color, DEFAULT_TEXT_FONT );
    }
    
    /**
     * Sets the text, using a specified font and color
     * 
     * @param text
     * @param font
     * @param color
     */
    public void setText( String text, Color color, Font font ) {
        _textGraphic.setHTML( text );
        _textGraphic.setColor( color );
        _textGraphic.setFont( font );
    }

    /**
     * Sets the duration (in clock ticks) of one complete wiggle.
     * 
     * @param cycleDuration
     */
    public void setCycleDuration( int cycleDuration ) {
        _cycleDuration = cycleDuration;
    }
    
    /**
     * Sets the range of the wiggle.
     * 
     * @param width
     * @param height
     */
    public void setRange( int width, int height ) {
        _range.setSize( width, height );
    }
    
    /**
     * Sets the wiggle direction.
     * 
     * @param direction CLOCKWISE or COUNTERCLOCKWISE
     * @throws IllegalArgumentException if direction is illegal
     */
    public void setDirection( int direction ) {
        if ( direction != CLOCKWISE && direction != COUNTER_CLOCKWISE ) {
            throw new IllegalArgumentException( "illegal direction value: " + direction );
        }
        _direction = direction;
    }
    
    //----------------------------------------------------------------------------
    // Arrows
    //----------------------------------------------------------------------------
    
    /**
     * Adds an arrow, using default Paints.
     * 
     * @param origin one of the TOP_*, MIDDLE_*, or BOTTOM_* constants
     * @param vector
     * @throws IllegalArgumentException if origin is not a legal value
     */
    public void addArrow( int origin, Vector2D vector ) {
        addArrow( origin, vector, DEFAULT_ARROW_FILL_COLOR, DEFAULT_ARROW_BORDER_COLOR );
    }
    
    /**
     * Adds an arrow using default fill border color.
     * 
     * @param origin
     * @param vector
     * @param fillColor
     * @throws IllegalArgumentException if origin is not a legal value
     */
    public void addArrow( int origin, Vector2D vector, Color fillColor ) {
        addArrow( origin, vector, fillColor, DEFAULT_ARROW_BORDER_COLOR );
    }
    
    /**
     * Adds an arrow, using specified Paints.
     * 
     * @param origin one of the TOP_*, MIDDLE_*, or BOTTOM_* constants
     * @param vector
     * @param fillColor
     * @param borderColor
     * @throws IllegalArgumentException if origin is not a legal value
     */
    public void addArrow( int origin, Vector2D vector, Color fillColor, Color borderColor ) {
        
        Point2D tail = new Point2D.Double( 0, 0 );
        switch ( origin ) {
        case TOP_LEFT:
            tail.setLocation( -TEXT_MARGIN, 0 );
            break;
        case TOP_CENTER:
            tail.setLocation( _textGraphic.getWidth() / 2, 0 );
            break;
        case TOP_RIGHT:
            tail.setLocation( _textGraphic.getWidth() + TEXT_MARGIN, 0 );
            break;
        case LEFT_CENTER:
            tail.setLocation( -TEXT_MARGIN, _textGraphic.getHeight() / 2 );
            break;
        case RIGHT_CENTER:
            tail.setLocation( _textGraphic.getWidth() + TEXT_MARGIN, _textGraphic.getHeight() / 2  );
            break;
        case BOTTOM_LEFT:
            tail.setLocation( -TEXT_MARGIN, _textGraphic.getHeight() + TEXT_MARGIN );
            break;
        case BOTTOM_CENTER:
            tail.setLocation( _textGraphic.getWidth() / 2, _textGraphic.getHeight() + TEXT_MARGIN );
            break;
        case BOTTOM_RIGHT:
            tail.setLocation( _textGraphic.getWidth() + TEXT_MARGIN, _textGraphic.getHeight() + TEXT_MARGIN );
            break;
        default:
            throw new IllegalArgumentException( "illegal orgin value: " + origin );
        }
        Point2D tip = new Point2D.Double( tail.getX() + vector.getX(), tail.getY() + vector.getY() );
        
        Arrow arrow = new Arrow( tail, tip, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
        
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent() );
        arrowGraphic.setShape( arrow.getShape() );
        arrowGraphic.setPaint( fillColor );
        arrowGraphic.setBorderPaint( borderColor );
        arrowGraphic.setStroke( DEFAULT_ARROW_STROKE );
        addGraphic( arrowGraphic );
    }
    
    //----------------------------------------------------------------------------
    // PhetGraphic overrides
    //----------------------------------------------------------------------------
    
    /**
     * Sets the location, and saves it for use in the animation.
     * 
     * @param p the location
     */
    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }
    
    /**
     * Sets the location and saves it for use in the animation.
     * 
     * @param x
     * @param y
     */
    public void setLocation( int x, int y ) {
        super.setLocation( x, y );
        _startLocation.setLocation( x, y ); 
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * Steps the graphic through its wiggle cycle.
     */
    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        double delta = dt / _cycleDuration;
        if ( _direction == CLOCKWISE ) {
            _cycles += delta;
        }
        else {
            _cycles -= delta;
        }
        int x = (int) ( _startLocation.x + ( _range.width * Math.cos( _cycles ) ) );
        int y = (int) ( _startLocation.y + ( _range.height * Math.sin( _cycles ) ) );
        super.setLocation( x, y );
        repaint();
    }

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    public void simulationTimeChanged( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {}
}
