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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * AbstractOrbitAtomNode contains shared visual representation for the Bohr and deBroglie atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractOrbitAtomNode extends AbstractAtomNode {

    private static final double GROUND_ORBIT_DIAMETER = 17;
    private static final Color ORBIT_COLOR = Color.WHITE;
    private static final Stroke ORBIT_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
    private static final Paint EXCITED_PAINT = Color.BLUE;
    private static final Stroke EXCITED_STROKE = new BasicStroke( 6f );
    private static final double PROTON_ELECTRON_SPACING = 2;
    
    private static final double PROTON_DIAMETER = new ProtonNode().getDiameter();
    private static final double ELECTRON_DIAMETER = new ElectronNode().getDiameter();
    
    public AbstractOrbitAtomNode() {
        super();
    }
    
    protected static PPath createOrbitNode( int orbit ) {
        final double diameter = calculateDiameter( orbit );
        Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );
        PPath orbitNode = new PPath();
        orbitNode.setPathTo( shape );
        orbitNode.setStroke( ORBIT_STROKE );
        orbitNode.setStrokePaint( ORBIT_COLOR );
        return orbitNode;
    }
    
    protected static PPath createExcitedNode( int orbit ) {
        final double diameter = calculateDiameter( orbit );
        Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );
        PPath orbitNode = new PPath();
        orbitNode.setPathTo( shape );
        orbitNode.setStroke( EXCITED_STROKE );
        orbitNode.setStrokePaint( EXCITED_PAINT );
        return orbitNode;
    }
    
    protected static double calculateDiameter( int orbit ) {
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
