package edu.colorado.phet.nuclearphysics.view;

import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.umd.cs.piccolo.PNode;


public class AtomicNucleusImageNode extends AtomicNucleusNode {

    PNode _displayImage;
    
    public AtomicNucleusImageNode(AtomicNucleus atomicNucleus){
        
        super(atomicNucleus);
        
        // Create a graphical image that will represent this nucleus in the view.
        _displayImage = NucleusImageFactory.getInstance().generateNucleusImage( atomicNucleus.getNumNeutrons(), 
                atomicNucleus.getNumProtons(), 25 );
        
        // Scale the image to the appropriate size.  Note that this is tweaked
        // a little bit in order to make it look better.
        _displayImage.scale( (atomicNucleus.getDiameter()/1.2)/((_displayImage.getWidth() + _displayImage.getHeight()) / 2));
        addChild(0, _displayImage);
        
        update();
    }
    
    protected void handleAtomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
            ArrayList byProducts){
        
        super.handleAtomicWeightChanged( atomicNucleus, numProtons, numNeutrons, byProducts );
        
        // Generate a new image for this node, since the weight has changed.
        removeChild( _displayImage );

        _displayImage = NucleusImageFactory.getInstance().generateNucleusImage( atomicNucleus.getNumProtons(), 
                atomicNucleus.getNumNeutrons(), 20 );
        _displayImage.scale( (atomicNucleus.getDiameter()/1.2)/((_displayImage.getWidth() + _displayImage.getHeight()) / 2));
        addChild(0, _displayImage);
        
        update();
    }
    
    /**
     * Update this node's position on the canvas.
     */
    protected void update(){
        
        super.update();

        if (_displayImage != null){
            _displayImage.setOffset( _atomicNucleus.getPositionReference().getX() - _atomicNucleus.getDiameter()/2,  
                    _atomicNucleus.getPositionReference().getY() - _atomicNucleus.getDiameter()/2);
        }
    }
}
