// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ZoomIndicatorNode connects the "tiny box" in BoxOfAtomsNode to the "big box" AnimationBoxNode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ZoomIndicatorNode extends PhetPNode {

    /* Dashed line stroke */
    private static final Stroke STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5,5}, 0 );
    
    /**
     * Constructor.
     */
    public ZoomIndicatorNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
    }
    
    /**
     * Draws dashed lines between the upper-left and lower-left corners of the "tiny box" and the "big box".
     * 
     * @param tinyBoxBounds bounds of the tiny box, in local coordinates
     * @param bigBoxBounds bounds of the big box, in local coordinates
     */
    public void update( Rectangle2D tinyBoxBounds, Rectangle2D bigBoxBounds ) {
        
        removeAllChildren();
        
        // line that connects upper-left corners
        double x1 = tinyBoxBounds.getX();
        double y1 = tinyBoxBounds.getY();
        double x2 = bigBoxBounds.getX();
        double y2 = bigBoxBounds.getY();
        PPath topLine = new PPath();
        topLine.setPathTo( new Line2D.Double( x1, y1, x2, y2 ) );
        topLine.setStroke( STROKE );
        topLine.setStrokePaint( Color.WHITE );
        addChild( topLine );
        
        // line that connects lower-left corners
        double x3 = x1;
        double y3 = tinyBoxBounds.getMaxY();
        double x4 = x2;
        double y4 = bigBoxBounds.getMaxY();
        PPath bottomLine = new PPath();
        bottomLine.setPathTo( new Line2D.Double( x3, y3, x4, y4 ) );
        bottomLine.setStroke( STROKE );
        bottomLine.setStrokePaint( Color.WHITE );
        addChild( bottomLine );
    }
}
