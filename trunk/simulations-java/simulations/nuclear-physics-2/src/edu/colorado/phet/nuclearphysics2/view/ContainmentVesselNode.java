/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.CubicCurve2D;

import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
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
    
    private static final float   CONTAINMENT_VESSEL_STROKE_WIDTH = 8.0f; 
    private static final double  HANDLE_HEIGHT = 30; 
    private static final float   HANDLE_WIDTH = 15; 
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the containment vessel in the model that this node represents.
    private ContainmentVessel _containmentVessel;
    
    // The shape and the node that represent the main vessel.
    private CubicCurve2D _mainVesselTop;
    private CubicCurve2D _mainVesselBottom;
    private PPath _mainVesselNode;
    
    // A node that represents a handle that the user can grab to change the
    // size of the containment vessel.
    private HandleNode _handle;
    
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
                _handle.setVisible( isEnabled );
            }
        });
        
        // Create the handle for sizing the containment vessel.
        _handle = new HandleNode(HANDLE_WIDTH, HANDLE_HEIGHT, Color.GRAY);
        _handle.setVisible( _containmentVessel.getIsEnabled() );
        _handle.rotate( Math.PI * 1.22 );
        addChild(_handle);
        
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
        
        // Set the size of the containment vessel.
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
        
        // Set the position of the handle.
//        _handle.setOffset( radius * Math.cos( Math.PI / 4 ), radius * Math.sin( Math.PI / 4 ) );
      _handle.setOffset( radius * 0.82, radius * 0.92 );
    }
}
