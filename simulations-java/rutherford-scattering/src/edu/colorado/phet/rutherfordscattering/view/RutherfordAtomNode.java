/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.model.RutherfordAtomModel;
import edu.umd.cs.piccolo.nodes.PPath;


public class RutherfordAtomNode extends PhetPNode implements Observer {
    
    private static final double MAX_RADIUS = 80;
    private static final double MIN_RADIUS = 10;
    
    private RutherfordAtomModel _atom;
    
    PPath _circleNode;
    
    public RutherfordAtomNode( RutherfordAtomModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        _circleNode = new PPath();
        _circleNode.setPaint( Color.GRAY );
        addChild( _circleNode );
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
        
        updateNucleus();
    }

    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == RutherfordAtomModel.PROPERTY_NUMBER_OF_PROTONS || arg == RutherfordAtomModel.PROPERTY_NUMBER_OF_NEUTRONS ) {
                updateNucleus();
            }
        }
    }
    
    private void updateNucleus() {
        int protons = _atom.getNumberOfProtons();
        int neutrons = _atom.getNumberOfNeutrons();
        int min = RSConstants.MIN_PROTONS + RSConstants.MIN_NEUTRONS;
        int max = RSConstants.MAX_PROTONS + RSConstants.MAX_NEUTRONS;
        double m = ( protons + neutrons - min ) / (double)( max - min );
        double radius = MIN_RADIUS + ( m * ( MAX_RADIUS - MIN_RADIUS ) );
        _circleNode.setPathTo( new Ellipse2D.Double( -radius, -radius, 2 * radius, 2 * radius )  );
    }
}
