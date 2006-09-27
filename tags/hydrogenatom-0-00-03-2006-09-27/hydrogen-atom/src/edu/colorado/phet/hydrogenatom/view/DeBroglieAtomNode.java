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
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class DeBroglieAtomNode extends AbstractOrbitAtomNode {
    
    private static final int DEFAULT_SELECTED_ORBIT = 1; //XXX hack, for demo purposes
    private static final int NUMBER_OF_ORBITS = 6;
    
    private ArrayList _orbitNodes; // array of PNode
    private ProtonNode _protonNode;
    
    public DeBroglieAtomNode() {
        super();
        
        _orbitNodes = new ArrayList();
        for ( int orbit = 1; orbit <= NUMBER_OF_ORBITS; orbit++ ) {
            PNode orbitNode = null;
            if ( orbit == DEFAULT_SELECTED_ORBIT ) {
                orbitNode = createExcitedNode( orbit );
            }
            else {
                orbitNode = createOrbitNode( orbit );
            }
            addChild( orbitNode );
            _orbitNodes.add( orbitNode );
        }
        
        _protonNode = new ProtonNode();
        addChild( _protonNode );
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        _protonNode.setOffset( 0, 0 );
    }
    

}
