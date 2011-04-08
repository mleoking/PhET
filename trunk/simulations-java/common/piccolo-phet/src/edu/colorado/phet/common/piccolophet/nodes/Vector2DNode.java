// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Vector2DNode is the visual representation of a 2D vector.
 * The vector is drawn as an arrow, with tail located at (0,0).
 * The magnitude of the vector determines the length of the arrow's tail.
 * An optional value can be displayed at the tip of the arrow.
 * <p/>
 * Note that this node has a lot of properties.  Rather than provide a
 * constructor with lots of arguments, I've chosen to provide a simple
 * constructor that provides a default "look" for the vector.  Properties
 * can be changed using setters. Each time you call a setter, the node
 * is rebuilt (this is because the Arrow class is used to create the
 * vector's shape, and the Arrow class is immutable). If you plan to call
 * a lot of setters, you should  consider calling setUpdateEnabled(false)
 * before calling the setters, and setUpdateEnabled(true) when you're done.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Vector2DNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double DEFAULT_ARROW_HEAD_HEIGHT = 20;
    private static final double DEFAULT_ARROW_HEAD_WIDTH = 20;
    private static final double DEFAULT_ARROW_TAIL_WIDTH = 5;
    private static final Stroke DEFAULT_ARROW_STROKE = new BasicStroke( 1f );
    private static final Paint DEFAULT_ARROW_STROKE_PAINT = Color.BLACK;
    private static final Paint DEFAULT_ARROW_FILL_PAINT = Color.WHITE;

    private static final Font DEFAULT_VALUE_FONT = new PhetFont();
    private static final Paint DEFAULT_VALUE_PAINT = Color.BLACK;
    private static final double DEFAULT_VALUE_SPACING = 0;
    private static final DecimalFormat DEFAULT_VALUE_FORMAT = new DecimalFormat( "0.##E0" );
    private static final String DEFAULT_UNITS = "";

    private static final Point2D TAIL_POSITION = new Point2D.Double( 0, 0 );
    private static final double DEFAULT_FRACTIONAL_HEAD_HEIGHT = 0.5;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _x, _y;
    private final double _referenceMagnitude;
    private final double _referenceLength;
    private PPath _arrowNode;
    private PText _valueNode;

    private double _headWidth, _headHeight;
    private double _tailWidth;
    private double _fractionalHeadHeight;
    private double _valueSpacing;
    private DecimalFormat _valueFormat;
    private String _units;
    private boolean _updateEnabled;

    private Point2D _somePoint; // reusable point

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * <p/>
     * Vector magnitude is scaled relative to referenceMagnitude and referenceLength.
     * A magnitude of referenceMagnitude will be drawn with a tail length of referenceLength.
     *
     * @param x
     * @param y
     * @param referenceMagnitude
     * @param referenceLength
     */
    public Vector2DNode( double x, double y, double referenceMagnitude, double referenceLength ) {
        super();

        assert ( referenceMagnitude > 0 );
        assert ( referenceLength > 0 );

        setPickable( false );
        setChildrenPickable( false );

        _x = x;
        _y = y;
        _referenceMagnitude = referenceMagnitude;
        _referenceLength = referenceLength;

        _arrowNode = new PPath();
        _arrowNode.setPaint( DEFAULT_ARROW_FILL_PAINT );
        _arrowNode.setStroke( DEFAULT_ARROW_STROKE );
        _arrowNode.setStrokePaint( DEFAULT_ARROW_STROKE_PAINT );
        addChild( _arrowNode );

        _valueNode = new PText();
        _valueNode.setTextPaint( DEFAULT_VALUE_PAINT );
        _valueNode.setFont( DEFAULT_VALUE_FONT );
        addChild( _valueNode );
        _valueNode.setVisible( false ); // value is NOT visible by default

        _headWidth = DEFAULT_ARROW_HEAD_WIDTH;
        _headHeight = DEFAULT_ARROW_HEAD_HEIGHT;
        _tailWidth = DEFAULT_ARROW_TAIL_WIDTH;
        _fractionalHeadHeight = DEFAULT_FRACTIONAL_HEAD_HEIGHT;

        _valueSpacing = DEFAULT_VALUE_SPACING;
        _valueFormat = DEFAULT_VALUE_FORMAT;
        _units = DEFAULT_UNITS;
        _updateEnabled = true;

        _somePoint = new Point2D.Double();

        update();
    }

    public Vector2DNode( Vector2D v, double referenceMagnitude, double referenceLength ) {
        this( v.getX(), v.getY(), referenceMagnitude, referenceLength );
    }

    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    /**
     * Use this if you're going to call a lot of setters.
     * Call setUpdateEnabled(false) before calling the setters,
     * and setUpdateEnabled(true) when you're done.
     *
     * @param enabled true or false
     */
    public void setUpdateEnabled( boolean enabled ) {
        if ( enabled != _updateEnabled ) {
            _updateEnabled = enabled;
            update();
        }
    }

    /**
     * Sets the vector that this node displays.
     *
     * @param v
     */
    public void setVector( Vector2D v ) {
        setXY( v.getX(), v.getY() );
    }

    /**
     * Gets the vector that this node displays.
     * Allocates a Vector2D.
     *
     * @return
     */
    public Vector2D getVector() {
        return new Vector2D( _x, _y );
    }

    /**
     * Sets the x & y components.
     *
     * @param x
     * @param y
     */
    public void setXY( double x, double y ) {
        if ( x != _x || y != _y ) {
            _x = x;
            _y = y;
            update();
        }
    }

    /**
     * Sets the magnitude and angle.
     *
     * @param magnitude
     * @param angle     angle in radians
     */
    public void setMagnitudeAngle( double magnitude, double angle ) {
        double x = PolarCartesianConverter.getX( magnitude, angle );
        double y = PolarCartesianConverter.getY( magnitude, angle );
        setXY( x, y );
    }

    /**
     * Controls the visibility of the value that appear at the tip of the arrow.
     *
     * @param visible true or false
     */
    public void setValueVisible( boolean visible ) {
        _valueNode.setVisible( visible );
        if ( visible ) {
            update();
        }
    }

    /**
     * Sets the size of the vector's arrow head.
     *
     * @param width
     * @param height
     */
    public void setHeadSize( double width, double height ) {
        if ( width != _headWidth || height != _headHeight ) {
            _headWidth = width;
            _headHeight = height;
            update();
        }
    }

    public void setHeadSize( Dimension size ) {
        setHeadSize( size.width, size.height );
    }

    /**
     * Sets the width of the vector's arrow tail.
     *
     * @param width
     */
    public void setTailWidth( double width ) {
        if ( width != _tailWidth ) {
            _tailWidth = width;
            update();
        }
    }

    /**
     * Sets the stroke used to outline the arrow.
     *
     * @param stroke
     */
    public void setArrowStroke( Stroke stroke ) {
        _arrowNode.setStroke( stroke );
    }

    /**
     * Sets the paint used to outline the arrow.
     *
     * @param paint
     */
    public void setArrowStrokePaint( Paint paint ) {
        _arrowNode.setStrokePaint( paint );
    }

    /**
     * Sets the paint used to fill the interior of the arrow.
     *
     * @param paint
     */
    public void setArrowFillPaint( Paint paint ) {
        _arrowNode.setPaint( paint );
    }

    /**
     * Gets the paint used to fill the interior of the arrow.
     *
     * @return Paint
     */
    public Paint getArrowFillPaint() {
        return _arrowNode.getPaint();
    }

    /**
     * Sets the paint used to draw the value that appears at the arrow tip.
     *
     * @param paint
     */
    public void setValuePaint( Paint paint ) {
        _valueNode.setTextPaint( paint );
    }

    /**
     * Sets the font used to draw the value that appears at the arrow tip.
     *
     * @param font
     */
    public void setFont( Font font ) {
        _valueNode.setFont( font );
    }

    /**
     * Sets the spacing between the value and the tip of the arrow.
     *
     * @param spacing
     */
    public void setValueSpacing( double spacing ) {
        if ( spacing != _valueSpacing ) {
            _valueSpacing = spacing;
            update();
        }
    }

    /**
     * Sets the format used to display the value.
     *
     * @param format
     */
    public void setValueFormat( DecimalFormat format ) {
        _valueFormat = format;
        update();
    }

    /**
     * Sets the units for the value.
     *
     * @param units
     */
    public void setUnits( String units ) {
        _units = units;
        update();
    }

    /**
     * Sets the fractonal head height for the arrow.
     * When the (head height)/(arrow length) > fractionalHeadHeight,
     * the arrow head and tail will be scaled.
     *
     * @param fractionHeadHeight
     */
    public void setFractionalHeadHeight( double fractionHeadHeight ) {
        if ( !( fractionHeadHeight > 0 && fractionHeadHeight < 1 ) ) {
            throw new IllegalArgumentException( "fractionalHeadHeight must be > 0 and < 1" );
        }
        if ( fractionHeadHeight != _fractionalHeadHeight ) {
            _fractionalHeadHeight = fractionHeadHeight;
            update();
        }
    }

    public double getReferenceMagnitude() {
        return _referenceMagnitude;
    }

    public double getReferenceLength() {
        return _referenceLength;
    }

    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    /*
    * Updates the arrow and text to match the node's state.
    */

    private void update() {

        if ( _updateEnabled ) {

            final double magnitude = PolarCartesianConverter.getRadius( _x, _y );

            // update the arrow
            final double xTip = _x * ( _referenceLength / _referenceMagnitude );
            final double yTip = _y * ( _referenceLength / _referenceMagnitude );
            _somePoint.setLocation( xTip, yTip );
            if ( magnitude == 0 ) {
                _arrowNode.setPathTo( new Rectangle2D.Double() ); // because Arrow doesn't handle zero-length arrows
            }
            else {
                Arrow arrow = new Arrow( TAIL_POSITION, _somePoint, _headHeight, _headWidth, _tailWidth, _fractionalHeadHeight, true /* scaleTailToo */ );
                _arrowNode.setPathTo( arrow.getShape() );
            }

            // update the text
            if ( _valueNode.getVisible() ) {

                String text = _valueFormat.format( magnitude );
                if ( _units != null && _units.length() > 0 ) {
                    text = text + " " + _units;
                }
                _valueNode.setText( text );

                /* 
                 * Position the text at the tip of the arrow.
                 * Sorry about this, and good luck debugging.
                 * There is undoubtedly a simpler way to do this...
                 * 
                 * Basic idea of this algorithm:
                 * Divide a circle into eight 45-degree slices.
                 * The vector's angle falls into one of these slices.
                 * For each slice, the vector's tip slides alone one of 
                 * the sides of the valueNode's bounds as the angle 
                 * sweeps through the slice.
                 * 
                 * Test changes to this algorithm using TestVector2DNode.
                 */
                {
                    double x = 0;
                    double y = 0;
                    final double valueWidth = _valueNode.getFullBoundsReference().getWidth();
                    final double valueHeight = _valueNode.getFullBoundsReference().getHeight();
                    double frac = 0;
                    double angle = PolarCartesianConverter.getAngle( _x, _y );
                    if ( angle < 0 ) {
                        angle += ( 2 * Math.PI );
                    }

                    if ( angle >= 0 && angle < 0.25 * Math.PI ) {
                        frac = angle / ( 0.25 * Math.PI );
                        x = xTip + _valueSpacing;
                        y = yTip - ( ( 1 - frac ) * valueHeight / 2 ) + ( frac * _valueSpacing );
                    }
                    else if ( angle >= 0.25 * Math.PI && angle < 0.5 * Math.PI ) {
                        frac = ( ( 0.5 * Math.PI ) - angle ) / ( 0.25 * Math.PI );
                        x = xTip - ( ( 1 - frac ) * valueWidth / 2 ) + ( frac * _valueSpacing );
                        y = yTip + _valueSpacing;
                    }
                    else if ( angle >= 0.5 * Math.PI && angle < 0.75 * Math.PI ) {
                        frac = ( ( 0.75 * Math.PI ) - angle ) / ( 0.25 * Math.PI );
                        x = xTip - ( valueWidth / 2 ) - ( ( 1 - frac ) * valueWidth / 2 ) - ( ( 1 - frac ) * _valueSpacing );
                        y = yTip + _valueSpacing;
                    }
                    else if ( angle >= 0.75 * Math.PI && angle < Math.PI ) {
                        frac = ( Math.PI - angle ) / ( 0.25 * Math.PI );
                        x = xTip - valueWidth - _valueSpacing;
                        y = yTip - ( ( 1 - frac ) * valueHeight / 2 ) + ( frac * _valueSpacing );
                    }
                    else if ( angle >= Math.PI && angle < 1.25 * Math.PI ) {
                        frac = ( ( 1.25 * Math.PI ) - angle ) / ( 0.25 * Math.PI );
                        x = xTip - valueWidth - _valueSpacing;
                        y = yTip - ( valueHeight / 2 ) - ( ( 1 - frac ) * valueHeight / 2 ) - ( ( 1 - frac ) * _valueSpacing );
                    }
                    else if ( angle >= 1.25 * Math.PI && angle < 1.5 * Math.PI ) {
                        frac = ( ( 1.5 * Math.PI ) - angle ) / ( 0.25 * Math.PI );
                        x = xTip - valueWidth + ( ( 1 - frac ) * valueWidth / 2 ) - ( frac * _valueSpacing );
                        y = yTip - valueHeight - _valueSpacing;
                    }
                    else if ( angle >= 1.5 * Math.PI && angle < 1.75 * Math.PI ) {
                        frac = ( ( 1.75 * Math.PI ) - angle ) / ( 0.25 * Math.PI );
                        x = xTip - ( valueWidth / 2 ) + ( ( 1 - frac ) * valueWidth / 2 ) + ( ( 1 - frac ) * _valueSpacing );
                        y = yTip - valueHeight - _valueSpacing;
                    }
                    else {
                        frac = ( ( 2 * Math.PI ) - angle ) / ( 0.25 * Math.PI );
                        x = xTip + _valueSpacing;
                        y = yTip - valueHeight + ( ( 1 - frac ) * valueHeight / 2 ) - ( frac * _valueSpacing );
                    }
                    _valueNode.setOffset( x, y );
                }
            }
        }
    }
}
