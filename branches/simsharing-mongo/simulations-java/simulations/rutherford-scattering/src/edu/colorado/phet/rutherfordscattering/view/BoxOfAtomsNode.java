// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BoxOfAtomsNode is the box of atoms into which the gun fires alpha particles.
 * A "tiny box" indicates the portion of the box that is shown in the "exploded" view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxOfAtomsNode extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Paint BOX_FRONT_PAINT = new Color( 106, 112, 49 ); // gold

    private static final Color TOP_COLOR_FRONT = Color.GRAY;
    private static final Color TOP_COLOR_BACK = Color.DARK_GRAY;
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private static final float BACK_DEPTH = 10f;
    private static final float BACK_OFFSET = 0.15f;
    
    public static final Color TINY_BOX_FILL_COLOR = RSConstants.ANIMATION_BOX_COLOR;
    public static final Color TINY_BOX_STROKE_COLOR = RSConstants.ANIMATION_BOX_STROKE_COLOR;
    public static final Stroke TINY_BOX_STROKE = new BasicStroke( 2f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PPath _tinyBoxNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param boxSize
     * @param tinyBoxSize
     */
    public BoxOfAtomsNode( Dimension boxSize, Dimension tinyBoxSize ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        // Box, origin in upper-left corner of bounds
        PNode boxNode = new PNode();
        {
            final float w = (float)boxSize.width;
            GeneralPath topPath = new GeneralPath();
            topPath.moveTo( BACK_OFFSET * w, 0 );
            topPath.lineTo( ( 1 - BACK_OFFSET ) * w, 0 );
            topPath.lineTo( w, BACK_DEPTH );
            topPath.lineTo( 0, BACK_DEPTH ); 
            topPath.closePath();
            PPath topNode = new PPath();
            topNode.setPathTo( topPath );
            topNode.setPaint( new GradientPaint( 0f, 0f, TOP_COLOR_BACK, 0f, BACK_DEPTH, TOP_COLOR_FRONT ) );
            topNode.setStroke( STROKE );
            topNode.setStrokePaint( STROKE_COLOR );
            
            PPath frontNode = new PPath( new Rectangle2D.Double( 0, BACK_DEPTH, boxSize.width, boxSize.height ) );
            frontNode.setPaint( BOX_FRONT_PAINT );
            frontNode.setStroke( STROKE );
            frontNode.setStrokePaint( STROKE_COLOR );
            
            boxNode.addChild( frontNode );
            boxNode.addChild( topNode );
        }
        boxNode.setOffset( 0, 0 );
        addChild( boxNode );

        // Tiny box
        _tinyBoxNode = new PPath( new Rectangle2D.Double( 0, 0, tinyBoxSize.width, tinyBoxSize.height ) );
        _tinyBoxNode.setPaint( TINY_BOX_FILL_COLOR );
        _tinyBoxNode.setStrokePaint( TINY_BOX_STROKE_COLOR );
        _tinyBoxNode.setStroke( TINY_BOX_STROKE );
        // in upper right quadrant of box
        double x = boxNode.getFullBounds().getX() + ( 0.6 * boxNode.getFullBounds().getWidth() );
        double y = boxNode.getFullBounds().getY() + ( 0.5 * boxNode.getFullBounds().getHeight() );
        _tinyBoxNode.setOffset( x, y );
        addChild( _tinyBoxNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the global full bounds of the tiny box that shows the "exploded" 
     * part of the box of hydrogen.  We use these bounds to attached
     * dashed lines between the box of hydrogen and the exploded view.
     * 
     * @return PBounds
     */
    public Rectangle2D getTinyBoxGlobalFullBounds() {
        return _tinyBoxNode.getGlobalFullBounds();
    }
}
