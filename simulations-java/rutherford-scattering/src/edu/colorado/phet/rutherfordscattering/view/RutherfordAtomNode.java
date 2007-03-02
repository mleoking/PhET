/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.rutherfordscattering.model.RutherfordAtomModel;
import edu.umd.cs.piccolo.nodes.PPath;


public class RutherfordAtomNode extends PhetPNode implements Observer {
    
    private RutherfordAtomModel _atom;
    
    public RutherfordAtomNode( RutherfordAtomModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        PPath pathNode = new PPath( new Rectangle2D.Double( -50, -50, 100, 100 ) );
        pathNode.setPaint( Color.RED );
        addChild( pathNode );
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
    }

    public void update( Observable o, Object arg ) {
        //XXX
    }

}
