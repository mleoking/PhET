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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.PlumPuddingModel;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
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
        int groundState = atom.getGroundState();
        int numberOfStates = atom.getNumberOfStates();
        for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
            double radius = atom.getOrbitRadius( state );
            PNode orbitNode = OrbitNodeFactory.createOrbitNode( radius );
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
        
        Point2D atomPosition = _atom.getPosition();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
        
        _protonNode.setOffset( 0, 0 );
        update( _atom, AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET );
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
        if ( o == _atom ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET ) {
                // the electron has moved
                Point2D electronOffset = _atom.getElectronOffset();
                // treat coordinates as distances, since _electronNode is a child node
                double nodeX = ModelViewTransform.transform( electronOffset.getX() );
                double nodeY = ModelViewTransform.transform( electronOffset.getY() );
                _electronNode.setOffset( nodeX, nodeY );
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_IONIZED ) {
                //XXX
            }
        }
    }
}
