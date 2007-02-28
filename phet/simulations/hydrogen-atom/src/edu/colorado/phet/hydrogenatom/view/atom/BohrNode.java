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
import java.awt.Font;
import java.awt.geom.Point2D;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.*;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.PlumPuddingModel;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

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
    
    // margin between the state display and animation box
    private static final double STATE_MARGIN = 15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _orbitNodes; // array of PNode
    private ProtonNode _protonNode;
    private ElectronNode _electronNode;
    private StateDisplayNode _stateNode;
    
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
        int groundState = BohrModel.getGroundState();
        int numberOfStates = BohrModel.getNumberOfStates();
        for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
            double radius = ModelViewTransform.transform( BohrModel.getOrbitRadius( state ) );
            PNode orbitNode = OrbitNodeFactory.createOrbitNode( radius );
            addChild( orbitNode );
            _orbitNodes.add( orbitNode );
        }
        
        _protonNode = new ProtonNode();
        addChild( _protonNode );
        
        _electronNode = new ElectronNode();
        addChild( _electronNode );
        
        if ( HAConstants.SHOW_STATE_DISPLAY ) {
            _stateNode = new StateDisplayNode();
            _stateNode.setState( atom.getElectronState() );
            addChild( _stateNode );
            
            // lower-right corner, (0,0) is at center of box
            double xOffset = ( HAConstants.ANIMATION_BOX_SIZE.getWidth() / 2 ) - _stateNode.getFullBounds().getWidth() - STATE_MARGIN;
            double yOffset = ( HAConstants.ANIMATION_BOX_SIZE.getHeight() / 2 ) - _stateNode.getFullBounds().getHeight() - STATE_MARGIN;
            _stateNode.setOffset( xOffset, yOffset );
        }
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        Point2D atomPosition = _atom.getPositionRef();
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
                Point2D electronOffset = _atom.getElectronOffsetRef();
                // treat coordinates as distances, since _electronNode is a child node
                double nodeX = ModelViewTransform.transform( electronOffset.getX() );
                double nodeY = ModelViewTransform.transform( electronOffset.getY() );
                _electronNode.setOffset( nodeX, nodeY );
            }
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                // update the state display
                if ( _stateNode != null ) {
                    _stateNode.setState( _atom.getElectronState() );
                }
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_IONIZED ) {
                //XXX
            }
        }
    }
}
