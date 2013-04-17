// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * ObjectiveNode is the visual representation of the microscope objective (lens).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ObjectiveNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color LENS_COLOR = new Color( 217, 240, 255, 100 ); // transparent light blue
    private static final Color CENTER_COLOR = new Color( 200, 220, 255, 180 ); // slightly darker, more opaque
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final double THICKNESS_PERCENT = 0.14; // thickness of center rectangle, % of objective height
    
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
        
        PPath ellipseNode = new PPath();
        ellipseNode.setPathTo( new Ellipse2D.Double( -width/2, -height/2, width, height ) ); // origin at center
        ellipseNode.setPaint( LENS_COLOR );
        ellipseNode.setStroke( STROKE );
        ellipseNode.setStrokePaint( STROKE_COLOR );
        addChild( ellipseNode );
        
        PPath rectangleNode = new PPath();
        rectangleNode.setPathTo( new Rectangle2D.Double( -width/2, -( THICKNESS_PERCENT * height ) /2, width, ( THICKNESS_PERCENT * height ) ) ); // origin at center
        rectangleNode.setPaint( CENTER_COLOR );
        rectangleNode.setStroke( STROKE );
        rectangleNode.setStrokePaint( STROKE_COLOR );
        addChild( rectangleNode );
    }
}
