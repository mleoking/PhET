// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This node is the visual representation of a "liquid expansion" type thermometer.
 * <p/>
 * This type of thermometer is commonly made from a glass <b>bulb</b> connected to a <b>tube</b>
 * of glass with a numbered scale and <b>tick</b> marks on the outside. Inside the glass tube
 * is a <b>liquid</b> like mercury or colored alcohol that rises and falls in the tube
 * as the temperature around it warms or cools. When the temperature rises, the liquid
 * in the glass tube warms up and molecules expand, which in turn takes up more space
 * in the tube.
 * <p/>
 * The origin (0,0) of this node is at the upper-left corner of the node's bounds.
 * <p/>
 * This class has a large number of properties that can be customized.
 * Rather than provide constructors with large numbers of arguments,
 * the constructors provide a "look" that should be good for most situations.
 * Setters and getters provide access to the properties.
 * Please resist the urge to add more constructors; instead, subclass this
 * class and call setters in your subclass constructor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LiquidExpansionThermometerNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double TUBE_WIDTH_PROPORTION = 0.5; // tube's width is this portion of the total width
    private static final Color TUBE_FILL_COLOR = new Color( 255, 255, 255, 180 ); // translucent white, like glass

    private static final double INNER_WALL_PROPORTION = 0.15; // inner wall is this portion of the total width
    private static final Color INNER_WALL_COLOR = Color.WHITE;

    private static final Stroke OUTLINE_STROKE = createAreaStroke( 1f );
    private static final Color OUTLINE_COLOR = Color.BLACK;

    private static final Color LIQUID_COLOR = Color.RED;

    private static final double TICK_SPACING = 10;
    private static final float TICK_STROKE_WIDTH = 1f;
    private static final Color TICK_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final double _bulbDiameter;
    private final double _tubeWidth, _tubeHeight;
    private final double _tubeMinX;
    private float _innerWallWidth;

    private final Rectangle2D _liquidShape;
    private final PPath _liquidNode;
    private final PNode _ticksNode;
    private final PPath _innerWallNode;
    private final PClip _backgroundNode;
    private final PPath _outlineNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor that uses a default ratio to determine the relative sizes
     * of the bulb and tube.
     *
     * @param size dimensions of the node, not accounting for stroke widths
     */
    public LiquidExpansionThermometerNode( PDimension size ) {
        this( size.getWidth(), TUBE_WIDTH_PROPORTION * size.getWidth(), size.getHeight() - size.getWidth() );
    }

    /**
     * Constructor.
     *
     * @param bulbDiameter
     * @param tubeWidth
     * @param tubeHeight
     */
    public LiquidExpansionThermometerNode( double bulbDiameter, double tubeWidth, double tubeHeight ) {
        super();

        if ( bulbDiameter <= tubeWidth ) {
            throw new IllegalArgumentException( "bulbDiameter must be > tubeWidth" );
        }

        _bulbDiameter = bulbDiameter;
        _tubeWidth = tubeWidth;
        _tubeHeight = tubeHeight;
        _tubeMinX = ( bulbDiameter - _tubeWidth ) / 2;

        // liquid is a rectangle, size changes to fill the bulb & tube
        _liquidShape = new Rectangle2D.Double();
        _liquidNode = new PPath();
        _liquidNode.setPaint( LIQUID_COLOR );
        _liquidNode.setStroke( null );

        // combine the bulb and tube using constructive area geometry, origin at upper left
        Shape tubeShape = new RoundRectangle2D.Double( _tubeMinX, 0, _tubeWidth, _bulbDiameter + _tubeHeight, _tubeWidth, _tubeWidth );
        Shape bulbShape = new Ellipse2D.Double( 0, _tubeHeight, _bulbDiameter, _bulbDiameter );
        Area area = new Area();
        area.add( new Area( bulbShape ) );
        area.add( new Area( tubeShape ) );

        // inner wall of the tube
        _innerWallWidth = (float) ( _bulbDiameter * INNER_WALL_PROPORTION );
        _innerWallNode = new PPath( area );
        _innerWallNode.setStroke( createAreaStroke( _innerWallWidth ) );
        _innerWallNode.setStrokePaint( INNER_WALL_COLOR );
        _innerWallNode.setPaint( null );

        // background acts as a clipping path, and draws the color for the empty portion of the tube
        _backgroundNode = new PClip();
        _backgroundNode.setPathTo( area );
        _backgroundNode.setStroke( null );
        _backgroundNode.setPaint( TUBE_FILL_COLOR );
        _backgroundNode.addChild( _liquidNode ); // clip liquid to inside of area, behind inner wall
        _backgroundNode.addChild( _innerWallNode ); // clip stroke to inside of area

        // tick marks on the tube
        _ticksNode = new PComposite();
        setTicks( TICK_SPACING, TICK_COLOR, TICK_STROKE_WIDTH );

        // outline around the entire thermometer
        _outlineNode = new PPath( area );
        _outlineNode.setStroke( OUTLINE_STROKE );
        _outlineNode.setStrokePaint( OUTLINE_COLOR );
        _outlineNode.setPaint( null );

        addChild( _backgroundNode );
        addChild( _outlineNode );
        addChild( _ticksNode );

        setLiquidHeight( 0 );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    /**
     * Sets the height of the liquid in the tube.
     * This is expressed as a percent of the tube that is filled with liquid.
     * At zero, the bulb is full but there is no liquid in the tube.
     * At 1, the entire tube is full.
     * <p/>
     * NOTE: We attempt to compensate for the stroke width of the inner wall.
     * If we don't do this, then as the stroke width increases, the liquid
     * will appear to be further up the tube when percent=0.
     *
     * @param percent 0.0 to 1.0
     * @throws IllegalArgumentException if percent is out of range
     */
    public void setLiquidHeight( double percent ) {
        if ( percent < 0 || percent > 1 ) {
            throw new IllegalArgumentException( "percent out of range: " + percent );
        }
        final double fudgeFactor = ( _innerWallNode.getVisible() ? _innerWallWidth : 0 );
        final double liquidWidth = _bulbDiameter;
        final double liquidHeight = _bulbDiameter + ( percent * ( _tubeHeight + fudgeFactor ) );
        _liquidShape.setRect( 0, _bulbDiameter + _tubeHeight - liquidHeight + fudgeFactor, liquidWidth, liquidHeight );
        _liquidNode.setPathTo( _liquidShape );
    }

    /**
     * Gets the diameter of the bulb.
     * This can be useful for positioning this node.
     *
     * @return double
     */
    public double getBulbDiameter() {
        return _bulbDiameter;
    }

    /**
     * Gets the size of the tube.
     * This can be useful for positioning this node.
     *
     * @return PDimension
     */
    public PDimension getTubeSize() {
        return new PDimension( _tubeWidth, _tubeHeight );
    }

    public void setTicksVisible( boolean visible ) {
        _ticksNode.setVisible( visible );
    }

    public void setInnerWallVisible( boolean visible ) {
        _innerWallNode.setVisible( visible );
    }

    public void setInnerWallPaint( Paint paint ) {
        _innerWallNode.setStrokePaint( paint );
    }

    public void setInnerWallWidth( float width ) {
        _innerWallWidth = width;
        _innerWallNode.setStroke( createAreaStroke( width ) );
    }

    public void setLiquidPaint( Paint paint ) {
        _liquidNode.setPaint( paint );
    }

    public void setOutlinePaint( Paint paint ) {
        _outlineNode.setStrokePaint( paint );
    }

    public void setOutlineStrokeWidth( float width ) {
        _outlineNode.setStroke( createAreaStroke( width ) );
    }

    public void setTubeColor( Paint paint ) {
        _backgroundNode.setPaint( paint );
    }

    public void setTicks( double spacing, Paint paint, float strokeWidth ) {
        _ticksNode.removeAllChildren();
        final double markLength = 0.25 * _tubeWidth;
        Stroke stroke = new BasicStroke( strokeWidth );
        for ( double y = spacing; y < _tubeHeight; y += spacing ) {
            PPath tickNode = new PPath( new Line2D.Double( _tubeMinX, y, _tubeMinX + markLength, y ) );
            tickNode.setStroke( stroke );
            tickNode.setStrokePaint( paint );
            _ticksNode.addChild( tickNode );
        }
    }

    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------

    /*
    * Creates a stroke that is appropriate for the shape that describes
    * the area of the thermometer.
    */
    private static Stroke createAreaStroke( float width ) {
        return new BasicStroke( width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND );
    }

    //----------------------------------------------------------------------------
    // Test
    //----------------------------------------------------------------------------

    public static void main( String args[] ) {

        final LiquidExpansionThermometerNode t1 = new LiquidExpansionThermometerNode( new PDimension( 25, 120 ) );

        final LiquidExpansionThermometerNode t2 = new LiquidExpansionThermometerNode( new PDimension( 50, 150 ) );

        final LiquidExpansionThermometerNode t3 = new LiquidExpansionThermometerNode( new PDimension( 25, 120 ) );
        t3.setTicksVisible( false );
        t3.setInnerWallVisible( false );

        // a highly-customized thermometer
        final LiquidExpansionThermometerNode t4 = new LiquidExpansionThermometerNode( 75, 25, 100 );
        t4.setTubeColor( Color.ORANGE );
        t4.setLiquidPaint( Color.GREEN );
        t4.setInnerWallPaint( Color.YELLOW );
        t4.setInnerWallWidth( 12 );
        t4.setOutlinePaint( Color.CYAN );
        t4.setOutlineStrokeWidth( 4 );
        t4.setTicks( 5, Color.RED, 2 );

        PCanvas canvas = new PCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        canvas.setBackground( Color.LIGHT_GRAY );
        canvas.getLayer().addChild( t1 );
        canvas.getLayer().addChild( t2 );
        canvas.getLayer().addChild( t3 );
        canvas.getLayer().addChild( t4 );

        final double xSpacing = 10;
        final double yOffset = 20;
        t1.setOffset( 50, yOffset );
        t2.setOffset( t1.getFullBoundsReference().getMaxX() + xSpacing, yOffset );
        t3.setOffset( t2.getFullBoundsReference().getMaxX() + xSpacing, yOffset );
        t4.setOffset( t3.getFullBoundsReference().getMaxX() + xSpacing, yOffset );

        // slider control for changing liquid height
        final DecimalFormat format = new DecimalFormat( "0.0" );
        JLabel label = new JLabel( "liquid height:" );
        final JSlider slider = new JSlider( 0, 100 );
        slider.setValue( 0 );
        final JLabel valueDisplay = new JLabel( format.format( 0 ) );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                final double percent = 0.01 * slider.getValue();
                valueDisplay.setText( format.format( percent ) );
                t1.setLiquidHeight( percent );
                t2.setLiquidHeight( percent );
                t3.setLiquidHeight( percent );
                t4.setLiquidHeight( percent );
            }
        } );

        JPanel controlPanel = new JPanel();
        controlPanel.add( label );
        controlPanel.add( slider );
        controlPanel.add( valueDisplay );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );

        JFrame frame = new JFrame();
        frame.getContentPane().add( mainPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 300 ) );
        frame.setVisible( true );
    }
}
