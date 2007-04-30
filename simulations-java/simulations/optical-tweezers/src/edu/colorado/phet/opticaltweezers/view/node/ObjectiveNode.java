/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ObjectiveNode is the visual representation of the microscope objective (lens).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ObjectiveNode extends PPath {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color FILL_COLOR = new Color( 217, 240, 255, 100 ); // transparent light blue
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param width width in view coordinates
     * @param height height in view coordinates
     */
    public ObjectiveNode( double width, double height ) {
        super();
        
        // (0,0) is at center
        setPathTo( new Ellipse2D.Double( -width/2, -height/2, width, height ) );
        setPaint( FILL_COLOR );
        setStroke( STROKE );
        setStrokePaint( STROKE_COLOR );
    }
}
