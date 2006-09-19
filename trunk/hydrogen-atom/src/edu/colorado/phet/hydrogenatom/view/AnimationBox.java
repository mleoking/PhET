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
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class AnimationBox extends PhetPNode {

    public static final Stroke STROKE = new BasicStroke( 2f );
    public static final Color STROKE_COLOR = Color.WHITE;
    
    public AnimationBox( Dimension size ) {
        super();
        
        PPath pathNode = new PPath( new Rectangle2D.Double( 0, 0, size.width, size.height ) );
        pathNode.setPaint( HAConstants.ANIMATION_AREA_COLOR );
        pathNode.setStroke( STROKE );
        pathNode.setStrokePaint( STROKE_COLOR );
        addChild( pathNode );
    }
}
