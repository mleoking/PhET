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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;


public class BohrAtomNode extends AbstractAtomNode {

    private static final Color ORBIT_COLOR = Color.WHITE;
    private static final Stroke ORBIT_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
    
    public BohrAtomNode() {
        super();
        
        NeutronNode neutronNode = new NeutronNode();
        ElectronNode electronNode = new ElectronNode();
        PPath o1 = createOrbitNode( 100 );
        PPath o2 = createOrbitNode( 200 );
        PPath o3 = createOrbitNode( 300 );
        PPath o4 = createOrbitNode( 400 );
        PPath o5 = createOrbitNode( 500 );
       
        addChild( o1 );
        addChild( o2 );
        addChild( o3 );
        addChild( o4 );
        addChild( o5 );
        addChild( neutronNode );
        addChild( electronNode );
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        neutronNode.setOffset( 0, 0 );
        electronNode.setOffset( o2.getFullBounds().getWidth() / 2, 0 );
    }
    
    private PPath createOrbitNode( double diameter ) {
        Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );
        PPath orbitNode = new PPath();
        orbitNode.setPathTo( shape );
        orbitNode.setStroke( ORBIT_STROKE );
        orbitNode.setStrokePaint( ORBIT_COLOR );
        return orbitNode;
    }
}
