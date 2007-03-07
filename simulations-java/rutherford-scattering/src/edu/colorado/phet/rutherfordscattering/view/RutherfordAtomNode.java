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


public class RutherfordAtomNode extends PhetPNode implements Observer {
    
    private static final double MAX_NUCLEUS_RADIUS = 80; // view coordinates
    private static final double MIN_NUCLEUS_RADIUS = 10; // view coordinates
    
    private RutherfordAtom _atom;
    
    private PPath _nucleusNode;
    private ElectronNode _electronNode;
    
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
    
    private void updateElectronOffset() {
        Point2D electronOffset = _atom.getElectronOffsetRef();
        // treat coordinates as distances, since _electronNode is a child node
        double nodeX = ModelViewTransform.transform( electronOffset.getX() );
        double nodeY = ModelViewTransform.transform( electronOffset.getY() );
        _electronNode.setOffset( nodeX, nodeY );
    }
    
    private void updateNucleus() {
        int protons = _atom.getNumberOfProtons();
        int neutrons = _atom.getNumberOfNeutrons();
        int min = _atom.getMinNumberOfProtons() + _atom.getMinNumberOfNeutrons();
        int max = _atom.getMaxNumberOfProtons() + _atom.getMaxNumberOfNeutrons();
        double m = ( protons + neutrons - min ) / (double)( max - min );
        double radius = MIN_NUCLEUS_RADIUS + ( m * ( MAX_NUCLEUS_RADIUS - MIN_NUCLEUS_RADIUS ) );
        assert( radius > 0 );
        _nucleusNode.setPathTo( new Ellipse2D.Double( -radius, -radius, 2 * radius, 2 * radius )  );
    }
}
