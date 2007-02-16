/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ZoomIndicatorNode connects the "tiny box" in BoxOfHydrogenNode to the "big box" exploded view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
     * @param tinyBoxOrigin
     * @param tinyBoxSize
     * @param bigBoxOrigin
     * @param bigBoxSize
     */
    public void update( Point2D tinyBoxOrigin, Dimension2D tinyBoxSize, Point2D bigBoxOrigin, Dimension2D bigBoxSize ) {
        
        removeAllChildren();
        
        double x1 = tinyBoxOrigin.getX();
        double y1 = tinyBoxOrigin.getY();
        double x2 = bigBoxOrigin.getX();
        double y2 = bigBoxOrigin.getY();
        
        double x3 = tinyBoxOrigin.getX();
        double y3 = tinyBoxOrigin.getY() + tinyBoxSize.getHeight();
        double x4 = bigBoxOrigin.getX();
        double y4 = bigBoxOrigin.getY() + bigBoxSize.getHeight();
        
        PPath topLine = new PPath();
        topLine.setPathTo( new Line2D.Double( x1, y1, x2, y2 ) );
        topLine.setStroke( STROKE );
        topLine.setStrokePaint( Color.WHITE );
        addChild( topLine );
        
        PPath bottomLine = new PPath();
        bottomLine.setPathTo( new Line2D.Double( x3, y3, x4, y4 ) );
        bottomLine.setStroke( STROKE );
        bottomLine.setStrokePaint( Color.WHITE );
        addChild( bottomLine );
    }
}
