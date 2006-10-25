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
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.view.*;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * BohrNode is the visual representation of the Bohr model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BohrNode extends AbstractHydrogenAtomNode implements Observer {

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
    private ElectronNode _electronNode;
    
    private BohrModel _atom;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor
     * @param atom
     */
    public BohrNode( BohrModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        _orbitNodes = new ArrayList();
        for ( int orbit = 1; orbit <= NUMBER_OF_ORBITS; orbit++ ) {
            PNode orbitNode = OrbitFactory.createOrbitNode( orbit );
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
