package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * OrbitNodeFactory creates visual components that are used by hydrogen atoms
 * that involve orbits (eg, Bohr and deBroglie models).
 */
class OrbitNodeFactory {

    private static final Color ORBIT_COLOR = Color.WHITE;
    private static final Stroke ORBIT_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
    
    /* Not intended for instantiation */
    private OrbitNodeFactory() {}
    
    /**
     * Creates a node that shows the orbit in 2D.
     * @param radius
     * @return PPath
     */
    public static PPath createOrbitNode( double radius ) {
        double nodeRadius = ModelViewTransform.transform( radius );
        Shape shape = new Ellipse2D.Double( -nodeRadius, -nodeRadius, 2 * nodeRadius, 2 * nodeRadius );
        PPath orbitNode = new PPath();
        orbitNode.setPathTo( shape );
        orbitNode.setStroke( ORBIT_STROKE );
        orbitNode.setStrokePaint( ORBIT_COLOR );
        orbitNode.setPickable( false );
        orbitNode.setChildrenPickable( false );
        return orbitNode;
    }
    
    /**
     * Creates a node that shows the orbit in pseudo-3D perspective.
     * @param radius
     * @param yxRatio ratio of height (y) to width (x)
     * @return PPath
     */
    public static PPath createPerspectiveOrbitNode( double radius, double yxRatio ) {
        double nodeRadius = ModelViewTransform.transform( radius );
        Shape shape = new Ellipse2D.Double( -nodeRadius, -nodeRadius * yxRatio, 2 * nodeRadius, 2 * nodeRadius * yxRatio );
        PPath orbitNode = new PPath();
        orbitNode.setPathTo( shape );
        orbitNode.setStroke( ORBIT_STROKE );
        orbitNode.setStrokePaint( ORBIT_COLOR );
        orbitNode.setPickable( false );
        orbitNode.setChildrenPickable( false );
        return orbitNode; 
    }
}