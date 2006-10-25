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

/**
 * DeBroglieNode is the visual representation of the deBroglie model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieNode extends AbstractHydrogenAtomNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int DEFAULT_SELECTED_ORBIT = 1; //XXX hack, for demo purposes
    private static final int NUMBER_OF_ORBITS = 6;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _orbitNodes; // array of PNode
    private ProtonNode _protonNode;
    
    private DeBroglieModel _atom; // model element
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public DeBroglieNode( DeBroglieModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        _orbitNodes = new ArrayList();
        for ( int orbit = 1; orbit <= NUMBER_OF_ORBITS; orbit++ ) {
            PNode orbitNode = null;
            if ( orbit == DEFAULT_SELECTED_ORBIT ) {
                orbitNode = OrbitFactory.createExcitedNode( orbit );
            }
            else {
                orbitNode = OrbitFactory.createOrbitNode( orbit );
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
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        setOffset( ModelViewTransform.transform( _atom.getPosition() ) ); 
    }
}
