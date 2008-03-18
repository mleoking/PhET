/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class AtomicNucleusNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    final private double NUCLEUS_DIAMETER = 16.0;
    
    private PNode _displayImage;
    private AtomicNucleus _atom;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AtomicNucleusNode(AtomicNucleus atom)
    {
        _atom = atom;
        
        // Do some calculations so that the representation is correctly
        // sized and also centered on the location set by the model.
        _displayImage = NuclearPhysics2Resources.getImageNode("Atomic Nucleus.png");
        _displayImage.scale( NUCLEUS_DIAMETER/_displayImage.getWidth() );
        _displayImage.translate( -(_displayImage.getWidth()/2), -(_displayImage.getHeight()/2) );
        addChild(_displayImage);
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
        /* JPB TBD - Also part of dynamic nucleus experiment
        _displayImage.setOffset( _atom.getPosition().getX() - NUCLEUS_DIAMETER/2,  
                _atom.getPosition().getY() - NUCLEUS_DIAMETER/2);
                */
    }

}
