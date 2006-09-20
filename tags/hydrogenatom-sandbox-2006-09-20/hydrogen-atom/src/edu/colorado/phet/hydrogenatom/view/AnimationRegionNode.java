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

import java.awt.*;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolox.nodes.PClip;


public class AnimationRegionNode extends PClip {

    public static final Stroke STROKE = new BasicStroke( 2f );
    public static final Color STROKE_COLOR = Color.WHITE;
    
    public AnimationRegionNode( Dimension size, Color background ) {
        super();
        Shape clip = new Rectangle2D.Double( 0, 0, size.width, size.height );
        setPathTo( clip );
        setPaint(  background );
        setStroke( STROKE );
        setStrokePaint( STROKE_COLOR );
    }
}
