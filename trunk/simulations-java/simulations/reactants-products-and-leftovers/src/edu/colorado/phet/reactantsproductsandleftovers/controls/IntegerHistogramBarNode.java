/* Copyright 2009, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Bar that fills to some level, acts as a histogram bar.
 * Vertical orientation only, no support for tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntegerHistogramBarNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Track
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color FILL_COLOR = new Color( 165, 194, 228 ); // light blue
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final IntegerRange range;
    private final PDimension size;
    private final FillNode fillNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IntegerHistogramBarNode( IntegerRange range, PDimension size ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        this.range = new IntegerRange( range );
        this.size = new PDimension( size );
        
        // components
        PNode backgroundNode = new BackgroundNode( size );
        fillNode = new FillNode( size );
        PNode strokeNode = new StrokeNode( size );
        
        // rendering order
        addChild( backgroundNode );
        addChild( fillNode );
        addChild( strokeNode );
        
        // initialize
        setValue( range.getDefault() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Fills the bar to level indicated by value.
     * 
     * @param value
     */
    public void setValue( double value ) {
        if ( !range.contains( value ) ) {
            throw new IllegalArgumentException( "value is out of range: " + value );
        }
        double height = size.getHeight() * ( value - range.getMin() ) / ( range.getLength() );
        fillNode.setFillHeight( height );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Stroke that goes around the bar.
     * Origin is at the upper left corner.
     */
    private static class StrokeNode extends PPath {
        
        public StrokeNode( PDimension size ) {
            super( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            setStroke( STROKE );
            setStrokePaint( STROKE_COLOR );
            setPaint( null );
        }
    }
    
    /*
     * Background of the bar, not stroked.
     * Origin is at the upper left corner.
     */
    private static class BackgroundNode extends PNode {
        
        public BackgroundNode( PDimension size ) {
            super();
            PPath pathNode = new PPath();
            pathNode.setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            pathNode.setPaint( BACKGROUND_COLOR );
            pathNode.setStroke( null );
            addChild( pathNode );
        }
    }
    
    /*
     * The portion of bar that is filled.
     */
    private static class FillNode extends PPath {

        private final PDimension maxSize;
        private final GeneralPath path;

        public FillNode( PDimension maxSize ) {
            super();
            this.maxSize = maxSize;
            path = new GeneralPath();
            setPaint( FILL_COLOR );
            setStroke( null );
        }
        
        public void setFillHeight( double height ) {
            path.reset();
            path.moveTo( 0f, (float) maxSize.getHeight() );
            path.lineTo( 0f, (float) ( maxSize.getHeight() - height ) );
            path.lineTo( (float) maxSize.getWidth(), (float) ( maxSize.getHeight() - height ) );
            path.lineTo( (float) maxSize.getWidth(), (float) maxSize.getHeight() );
            path.closePath();
            setPathTo( path );
        }
    }
}
