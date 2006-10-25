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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.umd.cs.piccolo.PNode;


public class DeBroglieNode extends AbstractOrbitAtomNode implements Observer {
    
    private static final int DEFAULT_SELECTED_ORBIT = 1; //XXX hack, for demo purposes
    private static final int NUMBER_OF_ORBITS = 6;
    
    private ArrayList _orbitNodes; // array of PNode
    private ProtonNode _protonNode;
    
    private DeBroglieModel _hydrogenAtom;
    
    public DeBroglieNode( DeBroglieModel hydrogenAtom ) {
        super();
        
        _hydrogenAtom = hydrogenAtom;
        _hydrogenAtom.addObserver( this );
        
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
        
        update( null, null );
    }
    
    public void update( Observable o, Object arg ) {
        setOffset( ModelViewTransform.transform( _hydrogenAtom.getPosition() ) ); 
    }
}
