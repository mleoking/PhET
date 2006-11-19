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
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * SolarSystemNode is the visual representation of the "solar system" model of 
 * the hydrogen atom. The electron spirals into the proton, reaching the proton
 * before any photons or alpha particles.  When the electron reaches the proton,
 * the atom is considered "destroyed" and an explosion graphic is shown.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SolarSystemNode extends AbstractHydrogenAtomNode implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SolarSystemModel _atom;
    private ProtonNode _protonNode;
    private ElectronNode _electronNode;
    private PImage _kaboomNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public SolarSystemNode( SolarSystemModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        _protonNode = new ProtonNode();
        _electronNode = new ElectronNode();
        _kaboomNode = PImageFactory.create( HAConstants.IMAGE_KABOOM );
        
        _kaboomNode.setVisible( false );
        _kaboomNode.setOffset( -_kaboomNode.getWidth() / 2, -_kaboomNode.getHeight() / 2 );
       
        addChild( _protonNode );
        addChild( _electronNode );
        addChild( _kaboomNode );
          
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }

        Point2D atomPosition = _atom.getPosition();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
        
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
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_DESTROYED ) {
                _protonNode.setVisible( false );
                _electronNode.setVisible( false );
                _kaboomNode.setVisible( true );
            }
        }
    }
}
