/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.opticaltweezers.util.Vector2D;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Vector2DNode is the visual representation of a 2D vector.
 * The vector is drawn as an arrow, with tail located at (0,0).
 * The magnitude of the vector determines the length of the arrow's tail.
 * The size of the arrow's head is constant, independent of the vector magnitude.
 * When the magnitude of the vector is zero, this node becomes invisible.
 * An optional value can be displayed at the tip of the arrow.
 * <p>
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
    
    private static final Font DEFAULT_VALUE_FONT = new JLabel().getFont();
    private static final Paint DEFAULT_VALUE_PAINT = Color.BLACK;
    private static final double DEFAULT_VALUE_SPACING = 2;
    private static final DecimalFormat DEFAULT_VALUE_FORMAT = new DecimalFormat( "0.##E0" );
    private static final String DEFAULT_UNITS = "";
    
    private static final Point2D TAIL_POSITION = new Point2D.Double( 0, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Vector2D _vector;
    private final double _referenceMagnitude;
    private final double _referenceLength;
    private PPath _arrowNode;
    private PText _valueNode;
    
    private double _headWidth, _headHeight;
    private double _tailWidth;
    private double _valueSpacing;
    private DecimalFormat _valueFormat;
    private String _units;
    private boolean _updateEnabled;
    private boolean _valueVisible;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * <p>
     * Vector magnitude is scaled relative to referenceMagnitude and referenceLength. 
     * A magnitude of referenceMagnitude will be drawn with a tail length of referenceLength.
     * Size of the arrow head is constant.
     * 
     * @param vector
     * @param referenceMagnitude
     * @param referenceLength
     */
    public Vector2DNode( Vector2D vector, double referenceMagnitude, double referenceLength ) {
        super();
        
        assert( referenceMagnitude > 0 );
        assert( referenceLength > 0 );
        
        setPickable( false );
        setChildrenPickable( false );
        
        _vector = new Vector2D( vector );
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
        
        _headWidth = DEFAULT_ARROW_HEAD_WIDTH;
        _headHeight = DEFAULT_ARROW_HEAD_HEIGHT;
        _tailWidth = DEFAULT_ARROW_TAIL_WIDTH;
        _valueSpacing = DEFAULT_VALUE_SPACING;
        _valueFormat = DEFAULT_VALUE_FORMAT;
        _units = DEFAULT_UNITS;
        _updateEnabled = true;
        _valueVisible = true;
        
        update();
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
     * @param vector
     */
    public void setVector( Vector2D vector ) {
        setVectorXY( vector.getX(), vector.getY() );
    }
    
    /**
     * Sets the vector via it's x & y components.
     * 
     * @param x
     * @param y
     */
    public void setVectorXY( double x, double y ) {
        if ( x != _vector.getX() || y != _vector.getY() ) {
            _vector.setXY( x, y );
            update();
        }
    }
    
    /**
     * Sets the vector via it's magnitude and angle.
     * 
     * @param magnitude
     * @param angle angle in radians
     */
    public void setVectorMagnitudeAngle( double magnitude, double angle ) {
        if ( magnitude != _vector.getMagnitude() || angle != _vector.getAngle() ) {
            _vector.setMagnitudeAngle( magnitude, angle );
            update();
        }
    }
    
    /**
     * Controls the visibility of the value that appear at the tip of the arrow.
     * 
     * @param visible true or false
     */
    public void setValueVisible( boolean visible ) {
        _valueVisible = visible;
        _valueNode.setVisible( visible );
        update();
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
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the arrow and text to match the node's state.
     */
    private void update() {
        
        if ( _updateEnabled ) {
            
            final double magnitude = _vector.getMagnitude();

            if ( magnitude == 0 ) {
                _arrowNode.setVisible( false );
                _valueNode.setVisible( false );
            }
            else {
                _arrowNode.setVisible( true );
                _valueNode.setVisible( _valueVisible );
                
                // update the arrow, vector magnitude determines tail length
                double tailWidth = _vector.getX() * ( _referenceLength / _referenceMagnitude );
                double tailHeight = _vector.getY() * ( _referenceLength / _referenceMagnitude );
                double headWidth = _headHeight * Math.cos( _vector.getAngle() );
                double headHeight = _headHeight * Math.sin( _vector.getAngle() );
                double xTip = tailWidth + headWidth;
                double yTip = tailHeight + headHeight;
                Point2D tipPosition = new Point2D.Double( xTip, yTip );
                Arrow arrow = new Arrow( TAIL_POSITION, tipPosition, _headHeight, _headWidth, _tailWidth );
                _arrowNode.setPathTo( arrow.getShape() );

                // update the text
                String text = _valueFormat.format( magnitude );
                if ( _units != null && _units.length() > 0 ) {
                    text = text + " " + _units;
                }
                _valueNode.setText( text );

                // position the text at the tip of the arrow
                double x = 0;
                if ( _vector.getX() > 0 ) {
                    x = _arrowNode.getFullBounds().getMaxX() + _valueSpacing;
                }
                else {
                    x = _arrowNode.getFullBounds().getX() - _valueSpacing - _valueNode.getFullBounds().getWidth();
                }
                double y = 0;
                if ( _vector.getY() > 0 ) {
                    y = _arrowNode.getFullBounds().getMaxY() + _valueSpacing;
                }
                else {
                    y = _arrowNode.getFullBounds().getY() - _valueSpacing - _valueNode.getFullBounds().getHeight();
                }
                _valueNode.setOffset( x, y );
            }
        }
    }
}
