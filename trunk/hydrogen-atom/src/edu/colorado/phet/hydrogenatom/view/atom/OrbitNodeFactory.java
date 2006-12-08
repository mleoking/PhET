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
     * Gets the Color used to draw orbits.
     * @return Color
     */
    public static Color getOrbitColor() {
        return ORBIT_COLOR;
    }
    
    /**
     * Gets the Stroke used to draw orbits.
     * @return Stroke
     */
    public static Stroke getOrbitStroke() {
        return ORBIT_STROKE;
    }
    
    /**
     * Creates a node that shows the orbit in 2D.
     * @param radius radius, in view coordinates
     * @return PPath
     */
    public static PPath createOrbitNode( double radius ) {
        return createOrbitNodeProjection( radius, 1 /* yScale */ );
    }
    
    /**
     * Creates a 2D projection of a 3D orbit.
     * An orbit is a circle, but it's 3D orbit is an ellipse.
     * Because we're only use 3D viewing angles that are rotations about the x-axis,
     * we can get away with simply scaling the y dimension to create the ellipse.
     * 
     * @param radius radius, in view coordinates
     * @param yScale how much to scale the y dimension
     * @return PPath
     */
    public static PPath createOrbitNodeProjection( double radius, double yScale ) {
        Ellipse2D shape = new Ellipse2D.Double( -radius, -radius * yScale, 2 * radius, 2 * radius * yScale );
        PPath orbitNode = new PPath();
        orbitNode.setPathTo( shape );
        orbitNode.setStroke( ORBIT_STROKE );
        orbitNode.setStrokePaint( ORBIT_COLOR );
        orbitNode.setPickable( false );
        orbitNode.setChildrenPickable( false );
        return orbitNode; 
    }
}