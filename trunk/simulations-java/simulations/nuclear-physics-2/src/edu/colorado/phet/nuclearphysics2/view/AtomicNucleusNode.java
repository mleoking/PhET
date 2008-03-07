/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class AtomicNucleusNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    final private double NUCLEUS_DIAMETER = 75.0f;
    
    private PPath _displayShape;
    private AtomicNucleus _atom;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AtomicNucleusNode(AtomicNucleus atom)
    {
        _atom = atom;
        
        _displayShape = new PPath(new Ellipse2D.Double(atom.getPosition().getX(), atom.getPosition().getY(), NUCLEUS_DIAMETER, NUCLEUS_DIAMETER));
        _displayShape.setPaint( new Color(100, 200, 50) );
        addChild(_displayShape);
        atom.addListener(new AtomicNucleus.Listener(){
            public void positionChanged()
            {
                update();
            }
            
        });
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    private void update(){
        _displayShape.setOffset( _atom.getPosition() );
    }

}
