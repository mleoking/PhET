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
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class ZoomIndicatorNode extends PhetPNode {

    private static final Stroke STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5,5}, 0 );
    
    public ZoomIndicatorNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
    }
    
    public void update( Point2D smallBoxOrigin, Dimension smallBoxSize, Point2D bigBoxOrigin, Dimension bigBoxSize ) {
        
        removeAllChildren();
        
        double x1 = smallBoxOrigin.getX();
        double y1 = smallBoxOrigin.getY();
        double x2 = bigBoxOrigin.getX();
        double y2 = bigBoxOrigin.getY();
        
        double x3 = smallBoxOrigin.getX();
        double y3 = smallBoxOrigin.getY() + smallBoxSize.getHeight();
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
