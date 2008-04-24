/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;

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
    private CubicCurve2D _mainVesselTop;
    private CubicCurve2D _mainVesselBottom;
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
        
        // Create the shape that represents the containment vessel.
        _mainVesselTop = new CubicCurve2D.Double();
        _mainVesselBottom = new CubicCurve2D.Double();
        _mainVesselNode = new PPath(_mainVesselTop, new BasicStroke(CONTAINMENT_VESSEL_STROKE_WIDTH));
        setVesselNodeSizeAndPosition( _containmentVessel.getRadius() );
        _mainVesselNode.setVisible( _containmentVessel.getIsEnabled() );
        _mainVesselNode.setPickable( true );
        addChild(_mainVesselNode);
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    private void setVesselNodeSizeAndPosition(double radius){
        
        // TODO: JPB TBD - Figure out how to calculate aperture height.
        double apertureHeight = 30;
        
        double aperturePosX = Math.sqrt( radius * radius - (apertureHeight/2) * (apertureHeight/2) );
        
        _mainVesselTop.setCurve(
                -aperturePosX, -apertureHeight/2, // x1, y1 
                -radius*0.8, -radius*1.3,         // ctrlx1, ctrly1
                radius*0.95, -radius*1.3,         // ctrlx2, ctrly2
                radius, 0                         // x2, y2
                );
        
        _mainVesselBottom.setCurve(
                radius, 0,                       // x1, y1 
                radius*0.95, radius*1.3,         // ctrlx1, ctrly1
                -radius*0.8, radius*1.3,         // ctrlx2, ctrly2
                -aperturePosX, apertureHeight/2  // x2, y2
                );

        _mainVesselNode = new PPath(_mainVesselTop, new BasicStroke(CONTAINMENT_VESSEL_STROKE_WIDTH));
        _mainVesselNode.append( _mainVesselBottom, true );
    }
}
