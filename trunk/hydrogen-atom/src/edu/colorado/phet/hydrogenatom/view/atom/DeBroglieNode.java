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
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
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
        System.out.println( "DeBroglieNode.init view=" + _atom.getView() );//XXX
        
        _orbitNodes = new ArrayList();
        int groundState = DeBroglieModel.getGroundState();
        int numberOfStates = DeBroglieModel.getNumberOfStates();
        for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
            double radius = atom.getOrbitRadius( state );
            PNode orbitNode = OrbitFactory.createOrbitNode( radius );
            addChild( orbitNode );
            _orbitNodes.add( orbitNode );
        }
        
        _protonNode = new ProtonNode();
        addChild( _protonNode );
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        Point2D atomPosition = _atom.getPosition();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
        
        _protonNode.setOffset( 0, 0 );
        update( _atom, AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE );
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
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                //XXX
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET ) {
                //XXX
            }
            else if ( arg == DeBroglieModel.PROPERTY_VIEW ) {
                //XXX
                System.out.println( "DeBroglieNode.update view=" + _atom.getView() );//XXX
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_IONIZED ) {
                //XXX
            }
        }
    }
}
