/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.umd.cs.piccolo.nodes.PPath;


public class ObjectiveNode extends PPath {

    private static final Color FILL_COLOR = new Color( 217, 240, 255, 100 ); // transparent light blue
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f );
    
    public ObjectiveNode( double width, double height ) {
        super();
        
        setPathTo( new Ellipse2D.Double( 0, 0, width, height ) );
        setPaint( FILL_COLOR );
        setStroke( STROKE );
        setStrokePaint( STROKE_COLOR );
    }
}
