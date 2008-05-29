/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * ThermometerGlassNode
 * <p>
 * Thermometers are commonly made from a glass <b>bulb</b> connected to a <b>tube</b> 
 * of glass with a numbered <b>scale</b> written on the outside. Inside the glass tube 
 * is a <b>liquid</b> like mercury or colored alcohol that rises and falls in the tube
 * as the temperature around it warms or cools. When the temperature rises, the liquid 
 * in the glass tube warms up and molecules expand, which in turn takes up more space 
 * in the tube.
 * <p>
 * The origin of this node is at the upper-left corner of the node's bounds. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThermometerGlassNode extends PComposite {

    private static final double TUBE_WIDTH_PROPORTION = 0.5; 
    private static final double TUBE_INNER_WALL_PROPORTION = 0.15;
    private static final Color TUBE_INNER_WALL_STROKE_COLOR = Color.WHITE;
    private static final Color TUBE_FILL_COLOR = new Color( 255, 255, 255, 180 );
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    
    private static final Color LIQUID_COLOR = Color.RED; 

    private static final double SCALE_MARK_SPACING = 10;
    private static final Stroke SCALE_MARK_STROKE = new BasicStroke( 1f );
    private static final Color SCALE_MARK_STROKE_COLOR = Color.BLACK;
    
    private final PDimension _size;
    private final double _bulbDiameter;
    private final PDimension _tubeSize;
    private final Rectangle2D _liquidShape;
    private final PPath _liquidNode;
    private final PNode _scaleNode;
    
    /**
     * Constructs a thermometer with default visual characteristics.
     * 
     * @param size dimensions of the node
     */
    public ThermometerGlassNode( PDimension size ) {
        super();
        
        if ( size.getWidth() >= size.getHeight() ) {
            throw new IllegalArgumentException( "height must be > width :" + size );
        }
        
        _size = new PDimension( size );
        
        // liquid is a rectangle, size changes to fill the bulb & tube
        _liquidShape = new Rectangle2D.Double();
        _liquidNode = new PPath();
        _liquidNode.setPaint( LIQUID_COLOR );
        _liquidNode.setStroke( null );
        
        // combine the bulb and tube using constructive area geometry, origin at upper left
        _bulbDiameter = size.getWidth();
        _tubeSize = new PDimension( size.getWidth() * TUBE_WIDTH_PROPORTION, size.getHeight() - _bulbDiameter );
        final double tubeMinX = ( size.getWidth() - _tubeSize.getWidth() ) / 2;
        Shape tubeShape = new RoundRectangle2D.Double( tubeMinX, 0, _tubeSize.getWidth(), size.getHeight(), _tubeSize.getWidth(), _tubeSize.getWidth() );
        Shape bulbShape = new Ellipse2D.Double( 0, _tubeSize.getHeight(), _bulbDiameter, _bulbDiameter );
        Area area = new Area();
        area.add( new Area( bulbShape ) );
        area.add( new Area( tubeShape ) );
        
        // inner wall of the tube
        final double tubeInnerWallWidth = _tubeSize.getWidth() * TUBE_INNER_WALL_PROPORTION;
        PPath tubeInnerWallNode = new PPath( area );
        tubeInnerWallNode.setStroke( new BasicStroke( (float)( 2 * tubeInnerWallWidth ) ) );
        tubeInnerWallNode.setStrokePaint( TUBE_INNER_WALL_STROKE_COLOR );
        tubeInnerWallNode.setPaint( null );
        
        // background acts as a clipping path, and draws the color for the empty part of the tube
        PClip backgroundNode = new PClip();
        backgroundNode.setPathTo( area );
        backgroundNode.setStroke( null );
        backgroundNode.setPaint( TUBE_FILL_COLOR );
        backgroundNode.addChild( _liquidNode ); // clip liquid to inside of area, behind inner wall
        backgroundNode.addChild( tubeInnerWallNode ); // clip stroke to inside of area
        
        // scale marks on the tube
        _scaleNode = new PComposite();
        final double markLength = 0.25 * _tubeSize.getWidth();
        for ( double y = SCALE_MARK_SPACING; y < _tubeSize.getHeight(); y += SCALE_MARK_SPACING ) {
            PPath markNode = new PPath( new Line2D.Double( tubeMinX, y, tubeMinX + markLength, y ) );
            markNode.setStroke( SCALE_MARK_STROKE );
            markNode.setStrokePaint( SCALE_MARK_STROKE_COLOR );
            _scaleNode.addChild( markNode );
        }
        
        // outline around the entire thermometer
        PPath outlineNode = new PPath( area );
        outlineNode.setStroke( OUTLINE_STROKE );
        outlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        outlineNode.setPaint( null );
        
        addChild( backgroundNode );
        addChild( outlineNode );
        addChild( _scaleNode );
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
        return new PDimension( _tubeSize );
    }
    
    /**
     * Sets the height of the liquid in the thermometer.  This is expressed as a percent 
     * of the total possible height of the liquid.  At zero, the bulb is full but there
     * is no liquid in the neck.  At 1, the entire neck is full.
     * 
     * @param percent 0.0 to 1.0
     * @throws IllegalArgumentException if percent is out of range
     */
    public void setLiquidHeight( double percent ) {
        if ( percent < 0 || percent > 1 ) {
            throw new IllegalArgumentException( "percent out of range: " + percent );
        }
        final double bulbDiameter = _size.getWidth();
        final double liquidWidth = bulbDiameter;
        final double liquidHeight = bulbDiameter + ( percent * ( _size.getHeight() - bulbDiameter ) );
        _liquidShape.setRect( 0, _size.getHeight() - liquidHeight, liquidWidth, liquidHeight );
        _liquidNode.setPathTo( _liquidShape );
    }
    
    /**
     * Sets the visibility of the scale markings.
     * 
     * @param visible
     */
    public void setScaleVisible( boolean visible ) {
        _scaleNode.setVisible( visible );
    }
}
