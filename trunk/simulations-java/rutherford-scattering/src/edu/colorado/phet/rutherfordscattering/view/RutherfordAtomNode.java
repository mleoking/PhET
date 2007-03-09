/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.rutherfordscattering.model.RutherfordAtom;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * RutherfordAtomNode is the visual representation of the Rutherford Atom model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RutherfordAtomNode extends PhetPNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MIN_NUCLEUS_RADIUS = 20; // view coordinates
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private RutherfordAtom _atom;
    
    private PPath _nucleusNode;
    private ElectronNode _electronNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param atom
     */
    public RutherfordAtomNode( RutherfordAtom atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        _nucleusNode = new PPath();
        _nucleusNode.setPaint( Color.GRAY );
        addChild( _nucleusNode );
        
        _electronNode = new ElectronNode();
        addChild( _electronNode );
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
        
        updateNucleus();
        update( _atom, RutherfordAtom.PROPERTY_ELECTRON_OFFSET );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == RutherfordAtom.PROPERTY_ELECTRON_OFFSET ) {
                updateElectronOffset();
            }
            else if ( arg == RutherfordAtom.PROPERTY_NUMBER_OF_PROTONS || arg == RutherfordAtom.PROPERTY_NUMBER_OF_NEUTRONS ) {
                updateNucleus();
            }
        }
    }
    
    /*
     * Moves the electron to match the model.
     */
    private void updateElectronOffset() {
        Point2D electronOffset = _atom.getElectronOffsetRef();
        // treat coordinates as distances, since _electronNode is a child node
        double nodeX = ModelViewTransform.transform( electronOffset.getX() );
        double nodeY = ModelViewTransform.transform( electronOffset.getY() );
        _electronNode.setOffset( nodeX, nodeY );
    }
    
    /*
     * Builds a new nucleus that matches the model.
     */
    private void updateNucleus() {
        int currentParticles = _atom.getNumberOfProtons() + _atom.getNumberOfNeutrons();
        int minParticles = _atom.getMinNumberOfProtons() + _atom.getMinNumberOfNeutrons();
        double C = MIN_NUCLEUS_RADIUS / Math.pow( minParticles, 1/3d );
        double radius = C * Math.pow( currentParticles, 1/3d );
        System.out.println( "particles=" + currentParticles + " radius=" + radius );
        assert( radius > 0 );
        _nucleusNode.setPathTo( new Ellipse2D.Double( -radius, -radius, 2 * radius, 2 * radius )  );
    }
}
