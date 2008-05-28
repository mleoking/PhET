package edu.colorado.phet.glaciers.view.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ThermometerGlassNode extends PComposite {

    private static final double SHAFT_WIDTH_PROPORTION = 0.5; 
    
    private static final Color LIQUID_COLOR = Color.RED; 
    private static final Stroke GLASS_STROKE = new BasicStroke( 1f );
    private static final Color GLASS_STROKE_COLOR = Color.BLACK;
    private static final Color GLASS_FILL_COLOR = Color.WHITE;
    
    private final PDimension _size;
    private final double _bulbDiameter;
    private final Rectangle2D _liquidShape;
    private final PPath _liquidNode;
    
    public ThermometerGlassNode( PDimension size ) {
        super();
        
        if ( size.getWidth() >= size.getHeight() ) {
            throw new IllegalArgumentException( "height must be > width :" + size );
        }
        
        _size = new PDimension( size );
        
        _bulbDiameter = size.getWidth();
        final double shaftWidth = size.getWidth() * SHAFT_WIDTH_PROPORTION;
        final double shaftHeight = size.getHeight() - _bulbDiameter;
        Shape shaftShape = new RoundRectangle2D.Double( -shaftWidth/2, -shaftHeight, shaftWidth, shaftHeight + _bulbDiameter, _bulbDiameter/2, _bulbDiameter/2 );
        Shape bulbShape = new Ellipse2D.Double( -_bulbDiameter/2, 0, _bulbDiameter, _bulbDiameter );
        Area area = new Area( shaftShape );
        area.add( new Area( bulbShape ) );
        
        PPath glassFillNode = new PPath( area );
        glassFillNode.setStroke( null );
        glassFillNode.setPaint( GLASS_FILL_COLOR );
        
        _liquidShape = new Rectangle2D.Double();
        _liquidNode = new PPath();
        _liquidNode.setPaint( LIQUID_COLOR );
        _liquidNode.setStroke( null );
        
        PClip glassOutlineNode = new PClip();
        glassOutlineNode.setPathTo( area );
        glassOutlineNode.setStroke( GLASS_STROKE );
        glassOutlineNode.setStrokePaint( GLASS_STROKE_COLOR );
        glassOutlineNode.setPaint( null );
        glassOutlineNode.addChild( _liquidNode ); // add liquid to this PClip so that it's clipped to the thermometer shape
        
        addChild( glassFillNode );
        addChild( glassOutlineNode );
    }
    
    public double getBulbDiameter() {
        return _bulbDiameter;
    }
    
    public void setTemperature( double percent ) {
        if ( percent < 0 || percent > 1 ) {
            throw new IllegalArgumentException( "percent out of range: " + percent );
        }
        final double bulbDiameter = _size.getWidth();
        final double liquidWidth = bulbDiameter;
        final double liquidHeight = bulbDiameter + ( percent * ( _size.getHeight() - bulbDiameter ) );
        _liquidShape.setRect( -bulbDiameter/2, -liquidHeight + bulbDiameter, liquidWidth, liquidHeight );
        _liquidNode.setPathTo( _liquidShape );
    }
}
