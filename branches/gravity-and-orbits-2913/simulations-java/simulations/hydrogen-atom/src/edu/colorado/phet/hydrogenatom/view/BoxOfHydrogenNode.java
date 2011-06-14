// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HAResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BoxOfHydrogenNode is the small "box of hydrogen" into which
 * the gun fires photons and alpha particles.  A "tiny box"
 * indicates the portion of the box of hydrogen that is shown
 * in the "exploded" view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BoxOfHydrogenNode extends PImage {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Paint BOX_FRONT_PAINT = Color.GRAY;

    private static final Color TOP_COLOR_FRONT = Color.GRAY;
    private static final Color TOP_COLOR_BACK = Color.DARK_GRAY;
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private static final float BACK_DEPTH = 10f;
    private static final float BACK_OFFSET = 0.15f;
    
    private static final int FONT_STYLE = Font.BOLD;
    private static final int DEFAULT_FONT_SIZE = 16;
    
    private static final double Y_SPACING = 5;  // space between label and box
    
    public static final Color TINY_BOX_FILL_COLOR = HAConstants.ANIMATION_BOX_COLOR;
    public static final Color TINY_BOX_STROKE_COLOR = HAConstants.ANIMATION_BOX_STROKE_COLOR;
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
    public BoxOfHydrogenNode( Dimension boxSize, Dimension tinyBoxSize ) {
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

        // Tiny box
        _tinyBoxNode = new PPath( new Rectangle2D.Double( 0, 0, tinyBoxSize.width, tinyBoxSize.height ) );
        _tinyBoxNode.setPaint( TINY_BOX_FILL_COLOR );
        _tinyBoxNode.setStrokePaint( TINY_BOX_STROKE_COLOR );
        _tinyBoxNode.setStroke( TINY_BOX_STROKE );
        
        // Label, origin in upper-left corner of bounds
        HTMLNode labelNode = new HTMLNode();
        labelNode.setHTML( HAResources.getString( "label.boxOfHydrogen" ) );
        labelNode.setHTMLColor( HAConstants.CANVAS_LABELS_COLOR );
        int fontSize = HAResources.getInt( "boxOfHydrogen.font.size", DEFAULT_FONT_SIZE );
        Font labelFont = new PhetFont( FONT_STYLE, fontSize );
        labelNode.setFont( labelFont );
        
        // Parent node for everything
        PComposite parentNode = new PComposite();
        
        // Layering
        parentNode.addChild( boxNode );
        parentNode.addChild( _tinyBoxNode );
        parentNode.addChild( labelNode );
        
        // Label centered above box, orgin in upper-left corner of bounds
        final double labelWidth = labelNode.getFullBounds().getWidth();
        final double boxWidth = boxNode.getFullBounds().getWidth();
        if ( boxWidth > labelWidth ) {
            labelNode.setOffset( ( boxWidth - labelWidth ) / 2, 0 );
            boxNode.setOffset( 0, labelNode.getFullBounds().getHeight() + Y_SPACING );
        }
        else {
            labelNode.setOffset( 0, 0 );
            boxNode.setOffset( ( labelWidth - boxWidth ) / 2, labelNode.getFullBounds().getHeight() + Y_SPACING );
        }
        
        // Tiny box in upper right quadrant of box
        double x = boxNode.getFullBounds().getX() + ( 0.6 * boxNode.getFullBounds().getWidth() );
        double y = boxNode.getFullBounds().getY() + ( 0.3 * boxNode.getFullBounds().getHeight() );
        _tinyBoxNode.setOffset( x, y );
        
        // Flatten everything to an image
        setImage( parentNode.toImage() );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the global bounds of the tiny box that shows the "exploded" 
     * part of the box of hydrogen.  We use these bounds to attached
     * dashed lines between the box of hydrogen and the exploded view.
     * 
     * @return PBounds
     */
    public PBounds getTinyBoxGlobalBounds() {
        PBounds fb = _tinyBoxNode.getFullBounds();
        Point2D gp = localToGlobal( fb.getOrigin() );
        Dimension2D gd = localToGlobal( fb.getSize() );
        PBounds gb = new PBounds( gp.getX(), gp.getY(), gd.getWidth(), gd.getHeight() );
        return gb;
    }
}
