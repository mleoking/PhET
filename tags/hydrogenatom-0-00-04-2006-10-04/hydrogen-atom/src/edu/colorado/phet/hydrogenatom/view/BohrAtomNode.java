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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class BohrAtomNode extends AbstractOrbitAtomNode {

    private static final int DEFAULT_SELECTED_ORBIT = 1; //XXX hack, for demo purposes
    private static final int NUMBER_OF_ORBITS = 6;
    
    private ArrayList _orbitNodes; // array of PNode
    private ProtonNode _protonNode;
    private ElectronNode _electronNode;
    
    public BohrAtomNode() {
        super();
        
        _orbitNodes = new ArrayList();
        for ( int orbit = 1; orbit <= NUMBER_OF_ORBITS; orbit++ ) {
            PNode orbitNode = createOrbitNode( orbit );
            addChild( orbitNode );
            _orbitNodes.add( orbitNode );
        }
        
        _protonNode = new ProtonNode();
        addChild( _protonNode );
        
        _electronNode = new ElectronNode();
        addChild( _electronNode );
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        _protonNode.setOffset( 0, 0 );
        PNode orbitNode = (PNode)_orbitNodes.get( DEFAULT_SELECTED_ORBIT - 1 );
        _electronNode.setOffset( orbitNode.getFullBounds().getWidth() / 2, 0 );
    }
}
