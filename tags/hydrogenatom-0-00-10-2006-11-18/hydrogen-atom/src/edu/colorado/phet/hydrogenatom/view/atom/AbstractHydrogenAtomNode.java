/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * AbstractHydrogenAtomNode is the base class for all hydrogen atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AbstractHydrogenAtomNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractHydrogenAtomNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * OrbitFactory creates visual components that are used by hydrogen atoms
     * that involve orbits (eg, Bohr and deBroglie models).
     */
    protected static class OrbitFactory {

        private static final Color ORBIT_COLOR = Color.WHITE;
        private static final Stroke ORBIT_STROKE = 
            new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
        private static final Paint EXCITED_PAINT = ElectronNode.getColor();
        private static final Stroke EXCITED_STROKE = new BasicStroke( 6f );
        
        /* Not intended for instantiation */
        private OrbitFactory() {}
        
        protected static PPath createOrbitNode( double radius ) {
            double nodeRadius = ModelViewTransform.transform( radius );
            Shape shape = new Ellipse2D.Double( -nodeRadius, -nodeRadius, 2 * nodeRadius, 2 * nodeRadius );
            PPath orbitNode = new PPath();
            orbitNode.setPathTo( shape );
            orbitNode.setStroke( ORBIT_STROKE );
            orbitNode.setStrokePaint( ORBIT_COLOR );
            return orbitNode;
        }

        protected static PPath createExcitedNode( double radius ) {
            double nodeRadius = ModelViewTransform.transform( radius );
            Shape shape = new Ellipse2D.Double( -nodeRadius, -nodeRadius, 2 * nodeRadius, 2 * nodeRadius );
            PPath orbitNode = new PPath();
            orbitNode.setPathTo( shape );
            orbitNode.setStroke( EXCITED_STROKE );
            orbitNode.setStrokePaint( EXCITED_PAINT );
            return orbitNode;
        }
    }
}
