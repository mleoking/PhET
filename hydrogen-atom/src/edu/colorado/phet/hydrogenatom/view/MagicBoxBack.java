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
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class MagicBoxBack extends PhetPNode {

    private static final Color INSIDE_COLOR = Color.DARK_GRAY;
    private static final Color TOP_COLOR_FRONT = Color.DARK_GRAY;
    private static final Color TOP_COLOR_BACK = Color.GRAY;
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    public MagicBoxBack( double width, double height, double depth ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        PPath insideNode = new PPath();
        insideNode.setPathTo(  new Rectangle2D.Double( 0, 0, width, height ) );
        insideNode.setPaint( INSIDE_COLOR );
        insideNode.setStroke( STROKE );
        insideNode.setStrokePaint( STROKE_COLOR );
        
        GeneralPath topPath = new GeneralPath();
        topPath.moveTo( 0, 0 ); 
        topPath.lineTo( 0.2f * (float)width, (float)-depth );
        topPath.lineTo( 0.8f *(float)width, (float)-depth );
        topPath.lineTo( (float)width, 0f );
        topPath.closePath();
        PPath topNode = new PPath();
        topNode.setPathTo( topPath );
        topNode.setPaint( new GradientPaint( 0f, 0f, TOP_COLOR_FRONT, 0f, (float)-depth, TOP_COLOR_BACK ) );
        topNode.setStroke( STROKE );
        topNode.setStrokePaint( STROKE_COLOR );
        
        addChild( insideNode );
        addChild( topNode );
    }
}
