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

        private static final double GROUND_ORBIT_DIAMETER = 17;
        private static final Color ORBIT_COLOR = Color.WHITE;
        private static final Stroke ORBIT_STROKE = 
            new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
        private static final Paint EXCITED_PAINT = ElectronNode.getColor();
        private static final Stroke EXCITED_STROKE = new BasicStroke( 6f );
        private static final double PROTON_ELECTRON_SPACING = 2;
        
        private static final double PROTON_DIAMETER = new ProtonNode().getDiameter();
        private static final double ELECTRON_DIAMETER = new ElectronNode().getDiameter();
        
        private OrbitFactory() {}
        
        protected static PPath createOrbitNode( int orbit ) {
            final double diameter = calculateOrbitDiameter( orbit );
            Shape shape = new Ellipse2D.Double( -diameter / 2, -diameter / 2, diameter, diameter );
            PPath orbitNode = new PPath();
            orbitNode.setPathTo( shape );
            orbitNode.setStroke( ORBIT_STROKE );
            orbitNode.setStrokePaint( ORBIT_COLOR );
            return orbitNode;
        }

        protected static PPath createExcitedNode( int orbit ) {
            final double diameter = calculateOrbitDiameter( orbit );
            Shape shape = new Ellipse2D.Double( -diameter / 2, -diameter / 2, diameter, diameter );
            PPath orbitNode = new PPath();
            orbitNode.setPathTo( shape );
            orbitNode.setStroke( EXCITED_STROKE );
            orbitNode.setStrokePaint( EXCITED_PAINT );
            return orbitNode;
        }

        protected static double calculateOrbitDiameter( int orbit ) {
            double diameter = orbit * orbit * GROUND_ORBIT_DIAMETER;
            if ( orbit == 1 ) {
                double minDiameter = PROTON_DIAMETER + ELECTRON_DIAMETER + PROTON_ELECTRON_SPACING;
                if ( minDiameter > diameter ) {
                    diameter = minDiameter;
                }
            }
            return diameter;
        }
    }
}
