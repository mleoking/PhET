/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.nuclearphysics2.model.ContainmentVessel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A node that represents a containment vessel in the view.  Each instance
 * must be coupled with a containment vessel in the model portion of the sim.
 *
 * @author John Blanco
 */
public class ContainmentVesselNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final float CONTAINMENT_VESSEL_STROKE_WIDTH = 8.0f; 
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the containment vessel in the model that this node represents.
    private ContainmentVessel _containmentVessel;
    
    // The shape and the node that represent the main vessel.
    private Ellipse2D.Double _mainVesselShape;
    private PPath _mainVesselNode;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public ContainmentVesselNode(ContainmentVessel containmentVessel){
        
        _containmentVessel = containmentVessel;
        
        // Register as a listener to the containment vessel.
        _containmentVessel.addListener( new ContainmentVessel.Listener(){
            public void radiusChanged(double newRadius){
                setVesselNodeSizeAndPosition( newRadius );
            }
            public void enableStateChanged(boolean isEnabled){
                _mainVesselNode.setVisible( isEnabled );
            }
        });
        
        // Create the mostly circular shape that represents the containment vessel.
        _mainVesselShape = new Ellipse2D.Double(10,10,100,100);
        _mainVesselNode = new PPath(_mainVesselShape, new BasicStroke(CONTAINMENT_VESSEL_STROKE_WIDTH));
        _mainVesselNode.setVisible( _containmentVessel.getIsEnabled() );
        _mainVesselNode.setPickable( true );
        setVesselNodeSizeAndPosition( _containmentVessel.getRadius() );
        addChild(_mainVesselNode);
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    private void setVesselNodeSizeAndPosition(double radius){
        _mainVesselShape.setFrame( 0, 0, radius * 2, radius * 2);
        _mainVesselNode.setPathTo(_mainVesselShape);
        setOffset(-radius, -radius);
    }

    
}
